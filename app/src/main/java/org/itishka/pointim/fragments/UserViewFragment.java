package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.itishka.pointim.R;
import org.itishka.pointim.adapters.PostListAdapter;
import org.itishka.pointim.adapters.UserInfoPostListAdapter;
import org.itishka.pointim.model.point.ExtendedUser;
import org.itishka.pointim.model.point.PostList;
import org.itishka.pointim.network.PointConnectionManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserViewFragment extends PostListFragment {

    private String mUser;

    public static UserViewFragment newInstance(String tag) {
        UserViewFragment fragment = new UserViewFragment();
        Bundle args = new Bundle();
        args.putString("user", tag);
        fragment.setArguments(args);
        return fragment;
    }

    protected String getCacheName() {
        return getClass().getCanonicalName() + mUser;
    }

    @Override
    protected PostListAdapter createAdapter() {
        return new UserInfoPostListAdapter(getActivity());
    }

    @Override
    protected Observable<PostList> createRequest() {
        return PointConnectionManager.getInstance().pointIm.getBlog(mUser);
    }

    @Override
    protected Observable<PostList> createRequest(long before) {
        return PointConnectionManager.getInstance().pointIm.getBlog(mUser, before);
    }

    protected Observable<ExtendedUser> createUserInfoRequest() {
        return PointConnectionManager.getInstance().pointIm.getUserInfo(mUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getArguments().getString("user");
    }

    protected String getUserCacheName() {
        return "UserViewFragment" + mUser;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Observable<ExtendedUser> request = getCache()
                .get(getUserCacheName(), ExtendedUser.class);
        addSubscription(request
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> {
                    if (user != null && user.isSuccess()) {
                        ((UserInfoPostListAdapter) getAdapter()).setUserInfo(user);
                    }
                    update();
                }));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void update() {
        super.update();
        addSubscription(createUserInfoRequest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> {
                    if (user != null && user.isSuccess()) {
                        ((UserInfoPostListAdapter) getAdapter()).setUserInfo(user);
                    } else if (!isDetached()) {
                        Toast.makeText(getActivity(), String.format(getString(R.string.toast_error_template), (user == null) ? "null" : user.error), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    if (!isDetached()) {
                        Toast.makeText(getActivity(), String.format(getString(R.string.toast_error_template), error.toString()), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_subscribe) {
            addSubscription(PointConnectionManager.getInstance().pointIm.subscribeUser(mUser, "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(pointResult -> {
                        if (!isDetached())
                            Toast.makeText(getActivity(), getString(R.string.toast_subscribed), Toast.LENGTH_SHORT).show();
                    }, error -> {
                        Log.d("UserViewFragment", "failure " + error.toString());
                        if (!isDetached())
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }));
            return true;
        } else if (id == R.id.action_unsubscribe) {
            addSubscription(PointConnectionManager.getInstance().pointIm.unsubscribeUser(mUser)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(pointResult -> {
                        if (!isDetached()) {
                            if (pointResult.isSuccess()) {
                                Toast.makeText(getActivity(), getString(R.string.toast_unsubscribed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), pointResult.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, error -> {
                        Log.d("UserViewFragment", "failure " + error.toString());
                        if (!isDetached())
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }));
            return true;
        } else if (id == R.id.action_subscribe_recommendations) {
            addSubscription(PointConnectionManager.getInstance().pointIm.subscribeUserRecommendations(mUser, "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(pointResult -> {
                        if (!isDetached())
                            Toast.makeText(getActivity(), getString(R.string.toast_recommendations_subscribed), Toast.LENGTH_SHORT).show();
                    }, error -> {
                        Log.d("UserViewFragment", "failure " + error.toString());
                        if (!isDetached())
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }));
            return true;
        } else if (id == R.id.action_unsubscribe_recommendations) {
            addSubscription(PointConnectionManager.getInstance().pointIm.unsubscribeUserRecommendations(mUser)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(pointResult -> {
                        if (!isDetached()) {
                            if (pointResult.isSuccess()) {
                                Toast.makeText(getActivity(), getString(R.string.toast_recommendations_unsubscribed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), pointResult.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, error -> {
                        Log.d("UserViewFragment", "failure " + error.toString());
                        if (!isDetached())
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_user, menu);

    }
}
