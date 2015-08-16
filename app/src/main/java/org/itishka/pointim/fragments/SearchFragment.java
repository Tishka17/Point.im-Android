package org.itishka.pointim.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.TagViewActivity;
import org.itishka.pointim.activities.UserViewActivity;
import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.model.Tag;
import org.itishka.pointim.model.TagList;
import org.itishka.pointim.model.User;
import org.itishka.pointim.model.UserList;
import org.itishka.pointim.network.requests.TagsRequest;
import org.itishka.pointim.network.requests.UserSubscriptionsRequest;
import org.itishka.pointim.utils.Utils;
import org.itishka.pointim.widgets.FlowLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends SpicedFragment {
    FlowLayout mUsersLayout;
    FlowLayout mTagsLayout;

    public SearchFragment() {
    }

    private View.OnClickListener mOnTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), TagViewActivity.class);
            intent.putExtra("tag", ((TextView) view).getText());
            getActivity().startActivity(intent);
        }
    };
    private View.OnClickListener mOnUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String user = (String) view.getTag();
            Intent intent = new Intent(getActivity(), UserViewActivity.class);
            intent.putExtra("user", user);
            getActivity().startActivity(intent);
        }
    };

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        mTagsLayout = (FlowLayout) rootView.findViewById(R.id.tags);
        mUsersLayout = (FlowLayout) rootView.findViewById(R.id.users);

        TagsRequest request = new TagsRequest(ConnectionManager.getInstance().loginResult.login);
        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheName(), DurationInMillis.ONE_DAY, mTagsRequestListener);
        UserSubscriptionsRequest request2 = new UserSubscriptionsRequest(ConnectionManager.getInstance().loginResult.login);
        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request2, request2.getCacheName(), DurationInMillis.ONE_DAY, mUsersRequestListener);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getActivity().finish();//FIXME
                return false;
            }
        });
        searchMenuItem.expandActionView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private RequestListener<TagList> mTagsRequestListener = new RequestListener<TagList>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //
        }

        @Override
        public void onRequestSuccess(TagList tags) {
            if (tags != null) {
                int n = 0;
                mTagsLayout.removeAllViews();
                for (Tag tag : tags) {
                    LayoutInflater li;
                    li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final TextView v = (TextView) li.inflate(R.layout.tag, null);
                    v.setText(tag.tag);
                    mTagsLayout.addView(v, n++, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    v.setOnClickListener(mOnTagClickListener);
                }
            }
        }
    };

    private RequestListener<UserList> mUsersRequestListener = new RequestListener<UserList>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            //
        }

        @Override
        public void onRequestSuccess(UserList users) {
            if (users != null) { int n = 0;
                mUsersLayout.removeAllViews();
                for (User user : users) {
                    LayoutInflater li;
                    li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View v = li.inflate(R.layout.user_chip, null);
                    v.setTag(user.login);
                    ((TextView)v.findViewById(R.id.login)).setText(user.login);
                    mUsersLayout.addView(v, n++, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    Utils.showAvatar(user.login, user.avatar, (ImageView) v.findViewById(R.id.avatar));
                    v.setOnClickListener(mOnUserClickListener);
                }
            }
        }
    };
}
