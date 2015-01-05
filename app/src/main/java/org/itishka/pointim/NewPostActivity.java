package org.itishka.pointim;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.itishka.pointim.api.ConnectionManager;


public class NewPostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionManager.getInstance().updateAuthorization(this);  //we need this in every activity that can be launched from itself
        setContentView(R.layout.activity_new_post);
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
                    fragment = NewPostFragment.newInstance((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    fragment = NewPostFragment.newInstance(intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM));
                }
            }
            if (fragment == null) {
                fragment = NewPostFragment.newInstance();
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
