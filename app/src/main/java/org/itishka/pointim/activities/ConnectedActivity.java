package org.itishka.pointim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.itishka.pointim.api.ConnectionManager;
import org.itishka.pointim.utils.ImageSearchHelper;

/**
 * Created by Tishka17 on 29.04.2015.
 */
public abstract class ConnectedActivity extends AppCompatActivity {
    @Override
    protected void onStop() {
        super.onStop();
        ImageSearchHelper.saveCache(this);
    }

    private static final int REQUEST_LOGIN = 0x6660;

    @Override
    protected void onStart() {
        super.onStart();
        if (!ConnectionManager.getInstance().isAuthorized())
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ConnectionManager.getInstance().init(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ConnectionManager.getInstance().isAuthorized()) {
            finish();
        }
    }
}
