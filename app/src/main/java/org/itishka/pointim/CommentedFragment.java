package org.itishka.pointim;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class CommentedFragment extends PostListFragment {
    @Override
    protected void update() {
        ConnectionManager.getInstance().pointService.getCommented(getCallback());
    }
}
