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

    public static List<String> getAllLinks(Spannable text) {
        URLSpan[] links = text.getSpans(0, text.length(), URLSpan.class);
        List<String> result = new ArrayList<>();
        for (URLSpan u : links) {
            result.add(u.getURL());
        }
        return result;
    }

    public static final void loadCache(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        for (Map.Entry<String, ?> s : pref.getAll().entrySet()) {
            sLinksChecked.put(s.getKey(), (String) s.getValue());
        }
    }

    public static final void saveCache(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (Map.Entry<String, ?> s : sLinksChecked.snapshot().entrySet()) {
            if (!MIME_ERROR.equals(s.getValue()))
                editor.putString(s.getKey(), (String) s.getValue());
        }
        editor.commit();
    }

    public static List<String> checkImageLinks(Context context, List<String> links) {
        synchronized (ImageSearchHelper.class) {
            if (!isLoaded) {
                loadCache(context);
            }
        }
        List<String> images = new ArrayList<>();
        for (String link : links) {
            String mime = sLinksChecked.get(link);
            if (mime == null) {
                mime = checkImageLink(link);
                if (mime == null) mime = MIME_ERROR;
                sLinksChecked.put(link, mime);
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
            connection.setDoInput(false);
            String contentType = connection.getHeaderField("Content-Type");
            return contentType;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
