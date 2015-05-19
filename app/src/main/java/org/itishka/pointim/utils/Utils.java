package org.itishka.pointim.utils;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.itishka.pointim.R;

import java.net.MalformedURLException;
import java.net.URL;
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

    public static void showAvatarByLogin(Context context, String login, ImageView imageView) {
        showAvatar(context, login, "http://point.im/avatar/" + login + "/80", imageView);
    }

    public static void showAvatar(Context context, String login, String avatar, ImageView imageView) {
        imageView.setTag(login);
        if (avatar == null) {
            Picasso.with(context)
                    .load(R.drawable.ic_launcher)
                    .placeholder(R.drawable.ic_launcher)
                    .fit()
                    .into(imageView);
            return;
        }
        try {
            URL url;
            if (avatar.contains("/"))
                url = new URL(avatar);
            else
                url = new URL(new URL(AVATAR_URL_STRING), "/a/80/" + avatar);
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(context.getResources().getColor(R.color.form_background))
                    .borderWidthDp(1)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();
            Picasso.with(context)
                    .load(url.toString())
                    .error(R.drawable.ic_action_internet)
                    .placeholder(R.drawable.ic_launcher)
                    .fit()
                    .transform(transformation)
                    .into(imageView);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void showAvatar(Context context, String avatar, ActionBar actionBar) {
        if (avatar == null) {
            actionBar.setLogo(R.drawable.ic_launcher);
            return;
        }
        try {
            URL url;
            if (avatar.contains("/"))
                url = new URL(avatar);
            else
                url = new URL(new URL(AVATAR_URL_STRING), "/a/80/" + avatar);
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(context.getResources().getColor(R.color.form_background))
                    .borderWidthDp(1)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();
            Picasso.with(context)
                    .load(url.toString())
                    .error(R.drawable.ic_action_internet)
                    .placeholder(R.drawable.ic_launcher)
                    .transform(transformation)
                    .into(new PicassoActionBarTarget(actionBar));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static class PicassoActionBarTarget implements Target {

        private final ActionBar mActionBar;

        public PicassoActionBarTarget(ActionBar actionBar) {
            mActionBar = actionBar;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            BitmapDrawable drawable = new BitmapDrawable(mActionBar.getThemedContext().getResources(), bitmap);
            mActionBar.setLogo(drawable);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mActionBar.setLogo(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mActionBar.setLogo(placeHolderDrawable);
        }
    }

    public static final int getGenderString(@Nullable Boolean gender) {
        if (gender==null) return R.string.gender_robot;
        else if (gender) return R.string.male;
        else return R.string.female;
    }
}
