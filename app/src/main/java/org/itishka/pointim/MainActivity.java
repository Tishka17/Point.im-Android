package org.itishka.pointim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.melnykov.fab.FloatingActionButton;

import org.itishka.pointim.api.ConnectionManager;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_LOGIN = 0;
    FloatingActionButton mNewPost;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        /*
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        */
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        mSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.toolbar_main_spinner, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAdapter.setDropDownViewResource(R.layout.spinner_toolbar);
        spinner.setAdapter(mSpinnerAdapter);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence item = mSpinnerAdapter.getItem(position);
                Fragment f = getSupportFragmentManager().findFragmentByTag(item.toString());

                if (f == null)
                    f = Fragment.instantiate(MainActivity.this, fragmentClass[position].getName());
                else if (f.isAdded())
                    return;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, f, item.toString())
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //ignore
            }
        });

    }

    Class[] fragmentClass = new Class[]{
            RecentFragment.class,
            CommentedFragment.class,
            SelfFragment.class,
            SelfFragment.class
    };

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
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
                "Blog"
        };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new RecentFragment();
            if (position == 1) return new CommentedFragment();
            else return new SelfFragment();
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
