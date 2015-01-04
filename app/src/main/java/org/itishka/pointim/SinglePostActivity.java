package org.itishka.pointim;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.itishka.pointim.api.ConnectionManager;


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
        getSupportActionBar().setTitle("#"+getIntent().getStringExtra("post"));
    }


}
