package org.itishka.pointim.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.itishka.pointim.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 12.01.2015.
 */
public class ContentStorageHelper {

    private static final String PREFERENCE = "tag_list";
    private static final String PREF_TAGS = "tags";
    private static final String PREF_UPDATED = "updated";

    public static void saveTags(Context context, List<Tag> tags) {
        if (context == null)
            return;
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        tags = PointHelper.removeDublicates(tags);
        long tagsUpdated = System.currentTimeMillis();
        pref.edit()
                .putString(PREF_TAGS, gson.toJson(tags))
                .putLong(PREF_UPDATED, tagsUpdated)
                .commit();
    }

    public static TagList loadTags(Context context) {
        TagList tagList = new TagList();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String t = pref.getString(PREF_TAGS, null);
        if (TextUtils.isEmpty(t))
            return null;
        Gson gson = new Gson();
        tagList.tags = gson.fromJson(t, new TypeToken<ArrayList<Tag>>() {
        }.getType());
        tagList.tags = PointHelper.removeDublicates(tagList.tags);
        tagList.updated = pref.getLong(PREF_UPDATED, 0);
        return tagList;
    }

    public static class TagList {
        public List<Tag> tags = null;
        public long updated = 0;
    }
}
