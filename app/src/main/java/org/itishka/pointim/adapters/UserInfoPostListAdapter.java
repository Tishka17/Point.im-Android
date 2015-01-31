package org.itishka.pointim.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.itishka.pointim.R;
import org.itishka.pointim.api.data.User;
import org.itishka.pointim.utils.Utils;

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

        Utils.showAvatar(getContext(), mUser.login, mUser.avatar, headerHolder.avatar);
        if (TextUtils.isEmpty(mUser.about.text)) {
            headerHolder.about.setVisibility(View.GONE);
        } else {
            headerHolder.about.setVisibility(View.VISIBLE);
            headerHolder.about.setText(mUser.about.text);
        }
        setText(mUser.name, headerHolder.name, headerHolder.name);
        setText(mUser.xmpp, headerHolder.xmpp_group, headerHolder.xmpp);
        setText(mUser.icq, headerHolder.icq_group, headerHolder.icq);
        setText(mUser.skye, headerHolder.skype_group, headerHolder.skype);
        setText(mUser.homepage, headerHolder.web_group, headerHolder.web);
        setText(mUser.email, headerHolder.email_group, headerHolder.email);

        if (mUser.created==null) {
            headerHolder.registered_group.setVisibility(View.GONE);
        } else {
            headerHolder.registered_group.setVisibility(View.VISIBLE);
            headerHolder.registered.setText(Utils.formatDateOnly(mUser.created));
        }
        if (mUser.birthdate==null) {
            headerHolder.birthday_group.setVisibility(View.GONE);
        } else {
            headerHolder.birthday_group.setVisibility(View.VISIBLE);
            headerHolder.birthday.setText(Utils.formatDateOnly(mUser.birthdate));
        }
        headerHolder.login.setText(mUser.login);
    }

    public void setUserInfo(User user) {
        if (user==null)
            return;
        mUser = user;
        notifyItemChanged(0);
    }

    private void setText(CharSequence text, View group, TextView field) {
        if (TextUtils.isEmpty(text)) {
            group.setVisibility(View.GONE);
        } else {
            group.setVisibility(View.VISIBLE);
            field.setText(text);
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        final TextView login;
        final TextView name;
        final TextView about;
        final TextView registered;
        final View registered_group;
        final View birthday_group;
        final TextView birthday;
        final View xmpp_group;
        final TextView xmpp;
        final View icq_group;
        final TextView icq;
        final View skype_group;
        final TextView skype;
        final View web_group;
        final TextView web;
        final View email_group;
        final TextView email;
        final ImageView avatar;
        public HeaderHolder(View v) {
            super(v);
            login = (TextView) v.findViewById(R.id.login);
            name = (TextView) v.findViewById(R.id.name);
            about = (TextView) v.findViewById(R.id.about);
            registered = (TextView) v.findViewById(R.id.registered);
            registered_group =  v.findViewById(R.id.registered_group);
            birthday = (TextView) v.findViewById(R.id.birthday);
            birthday_group =  v.findViewById(R.id.birthday_group);
            xmpp = (TextView) v.findViewById(R.id.xmpp);
            xmpp_group =  v.findViewById(R.id.xmpp_group);
            icq = (TextView) v.findViewById(R.id.icq);
            icq_group =  v.findViewById(R.id.icq_group);
            skype = (TextView) v.findViewById(R.id.skype);
            skype_group =  v.findViewById(R.id.skype_group);
            web = (TextView) v.findViewById(R.id.web);
            web_group =  v.findViewById(R.id.web_group);
            email = (TextView) v.findViewById(R.id.email);
            email_group =  v.findViewById(R.id.email_group);
            avatar = (ImageView) v.findViewById(R.id.avatar);
        }
    }
}
