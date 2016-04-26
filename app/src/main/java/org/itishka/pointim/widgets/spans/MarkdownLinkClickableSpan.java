package org.itishka.pointim.widgets.spans;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.text.style.ClickableSpan;
import android.view.View;

import org.itishka.pointim.activities.SinglePostActivity;

import java.lang.Override;

/**
 * Created by Alexey Skobkin on 02.09.2015.
 */
public class MarkdownLinkClickableSpan extends ClickableSpan {
    private final String mTitle;
    private final String mUri;

    public MarkdownLinkClickableSpan(String uri, String title) {
        mUri = uri;
        mTitle = title;
    }

    @Override
    public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) mUri));
        getContext().startActivity(browserIntent);
    }

}
