package org.itishka.pointim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

import org.itishka.pointim.R;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.fragments.AllFragment;
import org.itishka.pointim.fragments.CommentedFragment;
import org.itishka.pointim.fragments.RecentFragment;
import org.itishka.pointim.fragments.SelfFragment;
import org.itishka.pointim.utils.ImageSearchHelper;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_LOGIN = 0;
    FloatingActionButton mNewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        //TODO switch tabs
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mNewPost = (FloatingActionButton) findViewById(R.id.new_post);
        mNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionManager.getInstance().updateAuthorization(this);
        if (!ConnectionManager.getInstance().isAuthorized())
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ConnectionManager.getInstance().isAuthorized()) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageSearchHelper.saveCache(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_bookmarks) {
            startActivity(new Intent(this, BookmarksActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private static final String[] titles = new String[]{
                "Recent",
                "Commented",
                "Blog",
                "All",
        };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new RecentFragment();
            if (position == 1) return new CommentedFragment();
            if (position == 2) return new SelfFragment();
            if (position == 3) return new AllFragment();
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
