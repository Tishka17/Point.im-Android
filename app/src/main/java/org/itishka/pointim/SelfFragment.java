package org.itishka.pointim;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class SelfFragment extends PostListFragment {
    @Override
    protected void update() {
        ConnectionManager.getInstance().pointService.getBlog(ConnectionManager.getInstance().loginResult.login, getCallback());
    }
}
