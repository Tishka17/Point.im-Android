package org.itishka.pointim.utils;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import org.itishka.pointim.R;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.network.PointConnectionManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tishka17 on 29.11.2015.
 */
public class BookmarkToggleListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        final CheckBox checkBox = (CheckBox) view;
        final String id = (String) view.getTag();
        final Context context = view.getContext();
        if (checkBox.isChecked()) {
            PointConnectionManager.getInstance().pointIm.addBookmark(
                    id,
                    null,
                    new Callback<PointResult>() {
                        @Override
                        public void success(PointResult pointResult, Response response) {
                            Toast.makeText(context, String.format(context.getString(R.string.toast_bookmarked_template), id), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(context, String.format(context.getString(R.string.toast_bookmark_error_template), id, error), Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(false);
                        }
                    }
            );
        } else {
            PointConnectionManager.getInstance().pointIm.deleteBookmark(
                    id,
                    new Callback<Void>() {
                        @Override
                        public void success(Void pointResult, Response response) {
                            Toast.makeText(context, String.format(context.getString(R.string.toast_bookmark_remove_template), id), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(context, String.format(context.getString(R.string.toast_bookmark_remove_error_template), id, error), Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(true);
                        }
                    }
            );
        }
    }
}