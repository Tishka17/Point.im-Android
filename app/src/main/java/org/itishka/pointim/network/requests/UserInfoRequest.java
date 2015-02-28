package org.itishka.pointim.network.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.itishka.pointim.model.User;
import org.itishka.pointim.network.PointIm;

/**
 * Created by Tishka17 on 08.02.2015.
 */
public class UserInfoRequest extends RetrofitSpiceRequest<User, PointIm> {
    private final String mUserName;

    public UserInfoRequest(String userName) {
        super(User.class, PointIm.class);
        mUserName = userName;
    }

    @Override
    final public User loadDataFromNetwork() throws Exception {
        return getService().getUserInfo(mUserName);
    }

    public String getCacheName() {
        return getClass().getCanonicalName() + "-" + mUserName;
    }
}
