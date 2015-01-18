package org.itishka.pointim.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.itishka.pointim.R;
import org.itishka.pointim.utils.ImageSearchHelper;
import org.itishka.pointim.fragments.TagViewFragment;


public class TagViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_view);
        if (savedInstanceState == null) {
            //TODO user!!!
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, TagViewFragment.newInstance(getIntent().getStringExtra("tag")))
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("*" + getIntent().getStringExtra("tag"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tag_view, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageSearchHelper.saveCache(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
