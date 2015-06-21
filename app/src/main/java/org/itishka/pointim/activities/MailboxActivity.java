package org.itishka.pointim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.IncomingFragment;
import org.itishka.pointim.fragments.OutgoingFragment;

public class MailboxActivity extends ConnectedActivity {

    private FloatingActionButton mNewPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        if (savedInstanceState == null) {
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNewPost = (FloatingActionButton) findViewById(R.id.new_post);
        mNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MailboxActivity.this, NewPostActivity.class));
            }
        });
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
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
