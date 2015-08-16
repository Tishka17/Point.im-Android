package org.itishka.pointim.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.SinglePostActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String BASE_URL_STRING = "https://point.im/api/";
    public static final String AVATAR_URL_STRING = "https://i.point.im/";
    public static final String SITE_URL_STRING = "https://point.im/";
    public static final String BLOG_SITE_URL_TEMPLATE = "https://%s.point.im/blog";

    public static Uri getnerateSiteUri(String postId) {
        return Uri.parse(SITE_URL_STRING + postId);
    }

    public static Uri getnerateSiteUri(String postId, String commendId) {
        return Uri.parse(SITE_URL_STRING + postId + "#" + (commendId == null ? "" : commendId));
    }

    public static Uri getnerateBlogUri(String login) {
        return Uri.parse(String.format(BLOG_SITE_URL_TEMPLATE, login));
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatDateOnly(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getAvatarByLogin(String login) {
        return "http://point.im/avatar/login/" + login + "/80";
    }

    public static void showAvatarByLogin(String login, ImageView imageView) {
        showAvatar(login,getAvatarByLogin(login), imageView);
    }

    public static void showAvatar(String login, String avatar, ImageView imageView) {
        imageView.setTag(login);
        if (avatar == null) {
            imageView.setImageURI(null);
            return;
        }
        Uri url;
        if (avatar.contains("/"))
            url = Uri.parse(avatar);
        else
            url = Uri.parse(AVATAR_URL_STRING + "/a/80/" + avatar);
        imageView.setImageURI(url);
    }


    public static final int getGenderString(@Nullable Boolean gender) {
        if (gender == null) return R.string.gender_robot;
        else if (gender) return R.string.male;
        else return R.string.female;
    }

    public static void showPostSentSnack(final Activity activity, View view, final String postId) {
        Snackbar
                .make(view, String.format("Post #%s sent", postId), Snackbar.LENGTH_SHORT)
                .setAction("View", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, SinglePostActivity.class);
                        intent.putExtra("post", postId);
                        ActivityCompat.startActivity(activity, intent, null);
                    }
                })
                .show();
    }
}
