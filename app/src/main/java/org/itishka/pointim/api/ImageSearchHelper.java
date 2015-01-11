package org.itishka.pointim.api;

import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 31.12.2014.
 */
public class ImageSearchHelper {
    public static List<String> getAllLinks(Spannable text) {
        URLSpan[] links = text.getSpans(0, text.length(), URLSpan.class);
        List<String> result = new ArrayList<>();
        for (URLSpan u : links) {
            result.add(u.getURL());
        }
        return result;
    }

    private static final LruCache<String, Boolean> sLinksChecked = new LruCache<>(512);

    public static List<String> checkImageLinks(List<String> links) {
        List<String> images = new ArrayList<>();
        for (String link : links) {
            Boolean stored = sLinksChecked.get(link);
            if (stored == null) {
                boolean res = checkImageLink(link);
                if (res) {
                    images.add(link);
                }
                sLinksChecked.put(link, res);
            } else if (stored) {
                images.add(link);
            }
        }
        return images;
    }

    public static boolean checkImageLink(String link) {
        try {
            Log.d("ImageSearchHelper", "Checking: " + link);
            URLConnection connection = new URL(link).openConnection();
            connection.setDoInput(false);
            connection.setDoInput(false);
            String contentType = connection.getHeaderField("Content-Type");
            return !TextUtils.isEmpty(contentType) && contentType.startsWith("image/");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
