package org.itishka.pointim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;

import org.itishka.pointim.R;
import org.itishka.pointim.fragments.IncomingFragment;
import org.itishka.pointim.fragments.OutgoingFragment;
import org.itishka.pointim.utils.Utils;

public class MailboxActivity extends ConnectedActivity {

    private static final int REQUEST_NEW_POST = 13;
    private FloatingActionButton mNewPost;
    private ViewPager mPager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Utils.showPostSentSnack(this, mPager, data.getStringExtra("post"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
                startActivityForResult(new Intent(MailboxActivity.this, NewPostActivity.class), REQUEST_NEW_POST);
            }
        });
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
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
