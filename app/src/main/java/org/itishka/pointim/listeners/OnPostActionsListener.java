package org.itishka.pointim.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.widget.CheckBox;

import org.itishka.pointim.model.point.Post;

/**
 * Created by Tishka17 on 02.05.2016.
 */
public interface OnPostActionsListener {
    void onBookmark(@NonNull Post post, PopupMenu menu);
    void onBookmark(@NonNull Post post, CheckBox button);
    void onMenuClicked(@NonNull Post post, PopupMenu menu);
}
