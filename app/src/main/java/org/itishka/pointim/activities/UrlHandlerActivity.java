package org.itishka.pointim.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.itishka.pointim.R;

public class UrlHandlerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = null;
        Uri uri = getIntent().getData();
        String tag = uri.getQueryParameter("tag");
        String post = uri.getLastPathSegment();
        String comment = uri.getFragment();
        String user = getUser(uri.getHost());
        if (TextUtils.isEmpty(post)) {
            if (TextUtils.isEmpty(tag)) {
                if (TextUtils.isEmpty(user)) { //all
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("target", "all");
                } else { //blog
                    intent = new Intent(this, UserViewActivity.class);
                    intent.putExtra("user", user);
                }
            } else { //tag
                intent = new Intent(this, TagViewActivity.class);
                intent.putExtra("tag", tag);
                intent.putExtra("user", user);
            }
        } else if ("recent".equals(post)
                || "bookmarks".equals(post)
                || "all".equals(post)
                || "comments".equals(post)) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("target", post);
        } else { //post
            intent = new Intent(this, SinglePostActivity.class);
            intent.putExtra("post", post);
            intent.putExtra("comment", comment);
        }
        startActivity(intent);
        finish();
    }

    String getUser(String host) {
        if (host.endsWith(".point.im")) {
            return host.split("\\.point.im")[0];
        }
        return null;
    }
}
