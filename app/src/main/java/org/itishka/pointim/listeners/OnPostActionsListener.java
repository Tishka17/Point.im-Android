package org.itishka.pointim.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.CheckBox;

import org.itishka.pointim.model.point.PostData;

/**
 * Created by Tishka17 on 02.05.2016.
 */
public interface OnPostActionsListener {
    void onBookmark(@NonNull PostData post, final CheckBox button);

    void onMenuClicked(@NonNull PostData post, PopupMenu menu, MenuItem item);
}
