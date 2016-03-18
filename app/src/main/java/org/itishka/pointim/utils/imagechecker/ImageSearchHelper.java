package org.itishka.pointim.utils.imagechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.itishka.pointim.PointApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tishka17 on 31.12.2014.
 */
public class ImageSearchHelper {
    private static final LruCache<String, ImageInfo> sLinksChecked = new LruCache<>(512);
    private static final String PREFERENCE = "linkTypes";
    private static boolean isLoaded = false;
    private static boolean isSaved = false;
    private static OkHttpClient sOkHttpClient;

    public static void initCache(Context context) {
        synchronized (ImageSearchHelper.class) {
            if (!isLoaded) {
                sOkHttpClient = ((PointApplication) context.getApplicationContext()).getOkHttpClient();
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
        Gson gson = new GsonBuilder().create();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        try {
            for (Map.Entry<String, ?> s : pref.getAll().entrySet()) {
                sLinksChecked.put(s.getKey(), gson.fromJson((String) s.getValue(), ImageInfo.class));
            }
        } catch (JsonSyntaxException e) {
            Log.d("ImageSearchHelper", "Cannot parse cache: "+e.toString());
        }
        isLoaded = true;
        isSaved = true;
    }

    public static void saveCache(Context context) {
        if (isSaved)
            return;
        Gson gson = new GsonBuilder().create();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (Map.Entry<String, ?> s : sLinksChecked.snapshot().entrySet()) {
            if (!ImageInfo.MIME_ERROR.equals(s.getValue()))
                editor.putString(s.getKey(), gson.toJson(s.getValue()));
        }
        editor.apply();
        isSaved = true;
    }

    public static List<String> checkImageLinks(List<String> links) {
        return checkImageLinks(links, false);
    }

    public static List<String> checkImageLinks(List<String> links, boolean offline) {
        List<String> images = new ArrayList<>();
        for (String link : links) {
            ImageInfo info = sLinksChecked.get(link);
            if (info==null || info.mime == null) {
                if (offline)
                    continue;
                info = new ImageInfo();
                info.image = Uri.parse(link);
                info.mime = checkImageLink(link);
                if (info.mime == null) info.mime = ImageInfo.MIME_ERROR;
                sLinksChecked.put(link, info);
                isSaved = false;
            }
            if (info.isImage()) {
                images.add(link);
            }
        }
        return images;
    }

    public static String checkImageLink(String link) {
        try {
            Log.d("ImageSearchHelper", "Checking: " + link);
            Request request = new Request.Builder()
                    .head()
                    .url(link)
                    .build();
            Response response = sOkHttpClient.newCall(request).execute();
            return response.header("Content-Type");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
