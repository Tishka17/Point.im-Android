package org.itishka.pointim.listeners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import org.itishka.pointim.R;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.model.point.PostData;
import org.itishka.pointim.network.PointConnectionManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tishka17 on 28.04.2016.
 */
public class SimplePostActionsListener implements OnPostActionsListener {

    private Fragment mFragment = null;

    public SimplePostActionsListener(Fragment fragment) {
        mFragment = fragment;
    }

    private Context getContext() {
        return mFragment.getContext();
    }

    @Override
    public void onBookmark(@NonNull final PostData post, final CheckBox checkBox) {
        if (checkBox.isChecked()) {
            PointConnectionManager.getInstance().pointIm.addBookmark(
                    post.id,
                    null,
                    new Callback<PointResult>() {
                        @Override
                        public void success(PointResult pointResult, Response response) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmarked_template), post.id), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmark_error_template), post.id, error), Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(false);
                        }
                    }
            );
        } else {
            PointConnectionManager.getInstance().pointIm.deleteBookmark(
                    post.id,
                    new Callback<Void>() {
                        @Override
                        public void success(Void pointResult, Response response) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmark_remove_template), post.id), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmark_remove_error_template), post.id, error), Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(true);
                        }
                    }
            );
        }
    }

    @Override
    public void onMenuClicked(@NonNull PostData post, PopupMenu menu, MenuItem item) {
        //// TODO: 02.05.2016
        switch (item.getItemId()) {
            case R.id.action_delete:
                break;
        }
    }
}
