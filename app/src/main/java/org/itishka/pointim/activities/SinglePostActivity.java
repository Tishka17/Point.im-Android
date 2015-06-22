package org.itishka.pointim.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.SinglePostFragment;


public class SinglePostActivity extends ConnectedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
