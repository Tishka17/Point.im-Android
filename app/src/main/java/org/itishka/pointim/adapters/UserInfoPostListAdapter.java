package org.itishka.pointim.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.itishka.pointim.R;
import org.itishka.pointim.api.data.User;

/**
 * Created by Tishka17 on 31.01.2015.
 */
public class UserInfoPostListAdapter extends PostListAdapter {
    private User mUser;

    public UserInfoPostListAdapter(Context context) {
        super(context);
        setHasHeader(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        final View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_info_header, viewGroup, false);
        return new HeaderHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        HeaderHolder headerHolder = (HeaderHolder) holder;
        if (mUser==null)
            return;
        if (TextUtils.isEmpty(mUser.about)) {
            headerHolder.about.setVisibility(View.GONE);
        } else {
            headerHolder.about.setVisibility(View.VISIBLE);
            headerHolder.about.setText(mUser.about);
        }
    }

    public void setUserInfo(User user) {
        if (mUser==null)
            return;
        mUser = user;
        notifyItemChanged(0);
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        final TextView login;
        final TextView name;
        final TextView about;
        public HeaderHolder(View v) {
            super(v);
            login = (TextView) v.findViewById(R.id.login);
            name = (TextView) v.findViewById(R.id.name);
            about = (TextView) v.findViewById(R.id.about);
        }
    }
}
