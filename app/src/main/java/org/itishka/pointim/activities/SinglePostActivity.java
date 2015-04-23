package org.itishka.pointim.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.itishka.pointim.R;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.fragments.SinglePostFragment;
import org.itishka.pointim.utils.ImageSearchHelper;


public class SinglePostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionManager.getInstance().updateAuthorization(this);
        setContentView(R.layout.activity_single_post);
        if (savedInstanceState == null) {
            String post = getIntent().getStringExtra("post");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, SinglePostFragment.newInstance(post))
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("#" + getIntent().getStringExtra("post"));
    }


}
