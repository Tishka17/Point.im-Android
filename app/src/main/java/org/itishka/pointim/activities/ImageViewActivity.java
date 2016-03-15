package org.itishka.pointim.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.ImageListViewFragment;
import org.itishka.pointim.fragments.ImageViewFragment;

public class ImageViewActivity  extends ConnectedActivity {
    public static final String EXTRA_URLS = "urls";
    public static final String EXTRA_INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        if (savedInstanceState == null) {
            int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
            String []urls = getIntent().getStringArrayExtra(EXTRA_URLS);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ImageListViewFragment.newInstance(urls, index))
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
