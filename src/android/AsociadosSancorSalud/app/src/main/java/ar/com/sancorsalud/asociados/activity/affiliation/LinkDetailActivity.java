package ar.com.sancorsalud.asociados.activity.affiliation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.activity.BaseActivity;
import ar.com.sancorsalud.asociados.model.affiliation.EntidadEmpleadora;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;
import ar.com.sancorsalud.asociados.utils.ImageProvider;

/**
 * Created by francisco on 8/9/17.
 */

public class LinkDetailActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anses_cuil_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMainContainer = findViewById(R.id.main);

        String title = "link";
        String url = "";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title = getIntent().getStringExtra(ConstantsUtil.LINK_TITLE);
            url =  getIntent().getStringExtra(ConstantsUtil.LINK_URL);
        }

        setupToolbar(toolbar, title);

        mWebView = (WebView) findViewById(R.id.webview);
        setWebViewLayout();

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(url);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
    }


    private void setWebViewLayout() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalFadingEdgeEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalFadingEdgeEnabled(false);
    }
}
