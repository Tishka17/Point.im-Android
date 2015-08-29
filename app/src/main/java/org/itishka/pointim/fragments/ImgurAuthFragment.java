package org.itishka.pointim.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.itishka.pointim.BuildConfig;
import org.itishka.pointim.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImgurAuthFragment extends Fragment {

    WebView mWebView;

    public ImgurAuthFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newView = inflater.inflate(R.layout.fragment_imgur_auth, container, false);
        mWebView = (WebView) newView.findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("My Webview", url);
                Uri uri = Uri.parse(url);
                if (url.startsWith(BuildConfig.IMGUR_REDIRECT_URL + "?")) {
                    uri.getQueryParameter("code");
                    return false;
                }
                if (uri.getHost() != "api.imgur.com")
                    return true;
                return false; //Allow WebView to load url
            }
        });
        Uri auth = new Uri.Builder()
                .scheme("https")
                .authority("api.imgur.com")
                .path("/oauth2/authorize")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", BuildConfig.IMGUR_ID)
                .build();
        mWebView.loadUrl(auth.toString());
        return newView;
    }
}
