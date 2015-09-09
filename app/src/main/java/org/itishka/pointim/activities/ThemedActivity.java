package org.itishka.pointim.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.itishka.pointim.R;

/**
 * Created by Tishka17 on 26.08.2015.
 */
public abstract class ThemedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("themeDark", false))
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }
}
