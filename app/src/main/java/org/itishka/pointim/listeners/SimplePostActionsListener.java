package org.itishka.pointim.listeners;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.itishka.pointim.R;
import org.itishka.pointim.activities.NewPostActivity;
import org.itishka.pointim.model.point.PointResult;
import org.itishka.pointim.model.point.PostData;
import org.itishka.pointim.network.PointConnectionManager;
import org.itishka.pointim.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tishka17 on 28.04.2016.
 */
public class SimplePostActionsListener implements OnPostActionsListener {

    private Fragment mFragment = null;
    private OnPostChangedListener mOnPostChangedListener;

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
                            notifyChanged(post);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmark_error_template), post.id, error), Toast.LENGTH_SHORT).show();
                            notifyChanged(post);
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
                            notifyChanged(post);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_bookmark_remove_error_template), post.id, error), Toast.LENGTH_SHORT).show();
                            notifyChanged(post);
                        }
                    }
            );
        }
    }

    @Override
    public void onMenuClicked(@NonNull PostData post, PopupMenu menu, MenuItem item) {
        //// TODO: 02.05.2016
        switch (item.getItemId()) {
            case R.id.action_edit:
                onEditPost(post, menu, item);
                break;
            case R.id.action_delete:
                onDeletePost(post, menu, item);
                break;
            case R.id.action_copy_link:
                onCopyLink(post, menu, item);
                break;
            case R.id.action_recommend:
                onRecommendPost(post, menu, item);
                break;
        }
    }

    private void onRecommendPost(@NonNull final PostData post, PopupMenu menu, MenuItem item) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(String.format(getContext().getString(R.string.dialog_recommend_title_template), post.id))
                .positiveText(android.R.string.ok)
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String text = ((EditText) (dialog.findViewById(R.id.recommend_text))).getText().toString();
                        PointConnectionManager.getInstance().pointIm.recommend(post.id, text, new Callback<PointResult>() {
                            @Override
                            public void success(PointResult pointResult, Response response) {
                                if (pointResult.isSuccess()) {
                                    Toast.makeText(getContext(), getContext().getString(R.string.toast_recommended), Toast.LENGTH_SHORT).show();
                                    if (mOnPostChangedListener != null) {
                                        mOnPostChangedListener.onChanged(post);
                                    }
                                } else {
                                    Toast.makeText(getContext(), pointResult.error, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getContext(), error.toString() + "\n\n" + error.getCause(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .customView(R.layout.dialog_input, true)
                .build();
        dialog.show();
    }

    private void onEditPost(@NonNull PostData post, PopupMenu menu, MenuItem item) {
        Intent intent = new Intent(getContext(), NewPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(NewPostActivity.EXTRA_ID, post.id);
        bundle.putBoolean(NewPostActivity.EXTRA_PRIVATE, post.isPrivate);
        bundle.putString(NewPostActivity.EXTRA_TEXT, post.text.text.toString());
        bundle.putStringArray(NewPostActivity.EXTRA_TAGS, post.tags.toArray(new String[post.tags.size()]));
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    private void onCopyLink(@NonNull PostData post, PopupMenu menu, MenuItem item) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        Uri uri = Utils.generateSiteUri(post.id);
        ClipData clip = ClipData.newRawUri(uri.toString(), uri);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), String.format(getContext().getString(R.string.toast_link_copied__template), uri.toString()), Toast.LENGTH_SHORT).show();
    }

    private void onDeletePost(@NonNull final PostData post, PopupMenu menu, MenuItem item) {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(String.format(getContext().getString(R.string.dialog_delete_title_template), post.id))
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PointConnectionManager.getInstance().pointIm.deletePost(post.id, new Callback<PointResult>() {
                            @Override
                            public void success(PointResult pointResult, Response response) {
                                if (pointResult.isSuccess()) {
                                    Toast.makeText(getContext(), getContext().getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show();
                                    if (mOnPostChangedListener != null) {
                                        mOnPostChangedListener.onDeleted(post);
                                    }
                                } else {
                                    Toast.makeText(getContext(), pointResult.error, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getContext(), error.toString() + "\n\n" + error.getCause(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .build();
        dialog.show();
    }

    private void notifyChanged(PostData post) {
        if (mOnPostChangedListener != null)
            mOnPostChangedListener.onChanged(post);
    }

    public void setOnPostChangedListener(OnPostChangedListener onPostChangedListener) {
        mOnPostChangedListener = onPostChangedListener;
    }

    public interface OnPostChangedListener {
        void onChanged(PostData post);

        void onDeleted(PostData post);
    }

}
