package org.itishka.pointim.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.ImageViewFragment;

public class ImageViewActivity  extends ConnectedActivity {
    public static final String EXTRA_POST = "post";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_COMMENT = "comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        if (savedInstanceState == null) {
            String post = getIntent().getStringExtra(EXTRA_POST);
            String url = getIntent().getStringExtra(EXTRA_URL);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ImageViewFragment.newInstance(post, url))
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("#" + getIntent().getStringExtra(EXTRA_POST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
