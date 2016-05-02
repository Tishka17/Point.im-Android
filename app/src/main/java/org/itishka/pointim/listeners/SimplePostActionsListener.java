package org.itishka.pointim.listeners;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.widget.CheckBox;

import org.itishka.pointim.model.point.Post;
import org.itishka.pointim.utils.BookmarkToggleListener;

/**
 * Created by Tishka17 on 28.04.2016.
 */
public class SimplePostActionsListener implements OnPostActionsListener {

    private Fragment mFragment = null;

    public SimplePostActionsListener(Fragment fragment) {
        mFragment = fragment;
    }

    BookmarkToggleListener mBookmarkToggleListener = new BookmarkToggleListener();

    @Override
    public void onBookmark(@NonNull Post post, PopupMenu menu) {
        onBookmarkImpl(post, menu, null);
    }

    @Override
    public void onBookmark(@NonNull Post post, CheckBox button) {
        onBookmarkImpl(post, null, button);
    }

    private void onBookmarkImpl(Post post, PopupMenu menu, CheckBox button) {
        mBookmarkToggleListener.onClick(button);//// FIXME: 02.05.2016
    }

    @Override
    public void onMenuClicked(@NonNull Post post, PopupMenu menu) {
        //// TODO: 02.05.2016
    }
}
