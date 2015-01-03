package org.itishka.pointim.api;

import android.os.AsyncTask;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.LruCache;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.itishka.pointim.api.ConnectionManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tishka17 on 31.12.2014.
 */
public class ImageSearchHelper {

    public static List<String> getAllLinks(Spannable text) {
        URLSpan[] links = text.getSpans(0, text.length(), URLSpan.class);
        List<String> result = new ArrayList<>();
        for (URLSpan u: links) {
            result.add(u.getURL());
        }
        return result;
    }

    private static final LruCache<String, Boolean> sLinksChecked = new LruCache<>(512);

    public static List<String> checkImageLinks(List<String> links) {
        List<String> images = new ArrayList<>();
        for (String link: links) {
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
            Request request = new Request.Builder()
                    .head()
                    .url(link)
                    .build();
            Response response = ConnectionManager.getInstance().okHttpClient.newCall(request).execute();
            String contentType = response.header("Content-Type");
            if (TextUtils.isEmpty(contentType))
                return false;
            return contentType.startsWith("image/");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
