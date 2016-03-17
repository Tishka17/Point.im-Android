package org.itishka.pointim.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.ImageListViewFragment;

public class ImageViewActivity extends ConnectedActivity {
    public static final String EXTRA_URLS = "urls";
    public static final String EXTRA_INDEX = "index";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        if (savedInstanceState == null) {
            int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
            String[] urls = getIntent().getStringArrayExtra(EXTRA_URLS);
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
