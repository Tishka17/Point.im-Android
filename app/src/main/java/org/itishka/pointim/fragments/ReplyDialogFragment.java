package org.itishka.pointim.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.itishka.pointim.R;

/**
 * Created by Tishka17 on 05.05.2016.
 */
public class ReplyDialogFragment extends DialogFragment {
    private static final String ARG_POST = "post";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ReplyFragment fragment = (ReplyFragment) getChildFragmentManager().findFragmentById(R.id.fragment_reply);
        fragment.setPostId(getArguments().getString(ARG_POST));
    }

    public static void show(AppCompatActivity context, String postId) {
        ReplyDialogFragment dialog = new ReplyDialogFragment();
        if (dialog.getArguments() == null) {
            dialog.setArguments(new Bundle());
        }
        dialog.getArguments().putString(ARG_POST, postId);
        dialog.show(context.getSupportFragmentManager(), "[ABOUT_DIALOG]");
    }
}
