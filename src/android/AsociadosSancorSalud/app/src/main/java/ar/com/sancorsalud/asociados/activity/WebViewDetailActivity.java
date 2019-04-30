package ar.com.sancorsalud.asociados.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ar.com.sancorsalud.asociados.R;
import ar.com.sancorsalud.asociados.rest.core.HRequest;
import ar.com.sancorsalud.asociados.utils.ConstantsUtil;

/**
 * Created by francisco on 23/10/17.
 */

public class WebViewDetailActivity extends BaseActivity {

    private static final String TAG = "WEB_DET_ACT";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.activity_web_link);

        mMainContainer = findViewById(R.id.main);

        mWebView = (WebView) findViewById(R.id.webview);
        setWebViewLayout();

        if (getIntent().getExtras() != null) {
            String link = getIntent().getStringExtra(ConstantsUtil.ARG);

            if (link != null && !link.isEmpty()) {
                Log.e(TAG, "linK: " + link);

                mWebView.setBackgroundColor(Color.TRANSPARENT);
                mWebView.setWebViewClient(new WebViewClient());

                String url = "https://testservicios.sancorsalud.com.ar/asociados/api/ServicioFichas/Buscar_Archivo?content=inline&id=" + link + "&token=" + HRequest.authorizationHeaderValue;
                if (link.endsWith("pdf")) {
                    mWebView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
                } else {
                    mWebView.loadUrl(url);
                }
            }
        }
    }

    private void setWebViewLayout() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalFadingEdgeEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalFadingEdgeEnabled(false);
        //mWebView.setInitialScale(170);
    }


}
