package org.itishka.pointim.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.NewPostFragment;


public class NewPostActivity extends ConnectedActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_TAGS = "tags";
    public static final String EXTRA_PRIVATE = "private";
    public static final String EXTRA_RESULT_POST = "post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (savedInstanceState == null) {
            Fragment fragment = null;
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            Log.d("NPA", "type: " + type);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    fragment = NewPostFragment.newInstance(intent.getStringExtra(Intent.EXTRA_TEXT));
                } else if (type.startsWith("image/")) {
                    fragment = NewPostFragment.newInstance((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM), type);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    fragment = NewPostFragment.newInstance(intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM), type);
                }
            } else {
                String id = getIntent().getStringExtra(EXTRA_ID);
                String text = getIntent().getStringExtra(EXTRA_TEXT);
                String[] tags = getIntent().getStringArrayExtra(EXTRA_TAGS);
                boolean isPrivate = getIntent().getBooleanExtra(EXTRA_PRIVATE, false);
                if (!TextUtils.isEmpty(id)) {
                    fragment = NewPostFragment.newInstanceForEdit(id, text, tags, isPrivate);
                } else {
                    fragment = NewPostFragment.newInstance(isPrivate);
                }
                if (!TextUtils.isEmpty(id)) {
                    getSupportActionBar().setTitle("#" + id);
                }
            }
            if (fragment == null)
                fragment = NewPostFragment.newInstance(false);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
