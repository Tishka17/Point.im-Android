package org.itishka.pointim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

import org.itishka.pointim.R;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.fragments.IncomingFragment;
import org.itishka.pointim.fragments.OutgoingFragment;
import org.itishka.pointim.utils.ImageSearchHelper;


public class MailboxActivity extends ActionBarActivity {

    private static final int REQUEST_LOGIN = 0;
    private FloatingActionButton mNewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        //TODO switch tabs
        setContentView(R.layout.activity_mailbox);
        if (savedInstanceState == null) {
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
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
    protected void onStop() {
        super.onStop();
        ImageSearchHelper.saveCache(this);
    }

    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private static final String[] titles = new String[]{
                "Incoming",
                "Outgoing",
        };

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new IncomingFragment();
            if (position == 1) return new OutgoingFragment();
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
