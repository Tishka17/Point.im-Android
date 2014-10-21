package org.itishka.pointim.api.data;

import android.text.TextUtils;

/**
 * Created by atikhonov on 15.05.2014.
 */
public class PointResult {
    public String error;
    public boolean isSuccess() {
        return TextUtils.isEmpty(error);
    }
}
