package org.itishka.pointim.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tishka17 on 31.12.2014.
 */
public class ImageSearchHelper {
    private static final String MIME_ERROR = "^";
    private static final LruCache<String, String> sLinksChecked = new LruCache<>(512);
    private static final String PREFERENCE = "linkTypes";
    private static boolean isLoaded = false;
    private static boolean isSaved = false;

    public static void initCache(Context context) {
        synchronized (ImageSearchHelper.class) {
            if (!isLoaded) {
                reloadCache(context);
            }
        }
    }

    public static List<String> getAllLinks(Spannable text) {
        URLSpan[] links = text.getSpans(0, text.length(), URLSpan.class);
        List<String> result = new ArrayList<>();
        for (URLSpan u : links) {
            result.add(u.getURL());
        }
        return result;
    }

    public static void reloadCache(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> s : pref.getAll().entrySet()) {
            sLinksChecked.put(s.getKey(), (String) s.getValue());
        }
        isLoaded = true;
        isSaved = true;
    }

    public static void saveCache(Context context) {
        if (isSaved)
            return;
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (Map.Entry<String, ?> s : sLinksChecked.snapshot().entrySet()) {
            if (!MIME_ERROR.equals(s.getValue()))
                editor.putString(s.getKey(), (String) s.getValue());
        }
        editor.commit();
        isSaved = true;
    }

    public static List<String> checkImageLinks(List<String> links) {
        return checkImageLinks(links, false);
    }

    public static List<String> checkImageLinks(List<String> links, boolean offline) {
        List<String> images = new ArrayList<>();
        for (String link : links) {
            String mime = sLinksChecked.get(link);
            if (mime == null) {
                if (offline)
                    continue;
                mime = checkImageLink(link);
                if (mime == null) mime = MIME_ERROR;
                sLinksChecked.put(link, mime);
                isSaved = false;
            }
            if (isImage(mime)) {
                images.add(link);
            }
        }
        return images;
    }

    private static boolean isImage(String mime) {
        return mime != null && mime.startsWith("image/");
    }

    public static String checkImageLink(String link) {
        try {
            Log.d("ImageSearchHelper", "Checking: " + link);
            URLConnection connection = new URL(link).openConnection();
            connection.setDoInput(false);
            connection.setDoOutput(false);
            return connection.getHeaderField("Content-Type");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
