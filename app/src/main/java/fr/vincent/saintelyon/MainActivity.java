package fr.vincent.saintelyon;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    // Remplacé automatiquement à la compilation par https://<ton-compte>.github.io/<depot>/
    static final String SITE_URL = "__SITE_URL__";
    static final String FALLBACK = "file:///android_asset/index.html";
    private WebView web;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.parseColor("#0a0e1a"));
        getWindow().setNavigationBarColor(Color.parseColor("#111827"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0a0e1a"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setAllowFileAccess(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpError(WebView v, WebResourceRequest r, WebResourceResponse e) {
                // Page introuvable (404...) : copie embarquée
                if (r.isForMainFrame() && e.getStatusCode() >= 400 && !r.getUrl().toString().startsWith("file:")) v.loadUrl(FALLBACK);
            }

            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                // Pas de réseau et rien en cache : on ouvre la copie embarquée
                if (r.isForMainFrame() && !r.getUrl().toString().startsWith("file:")) v.loadUrl(FALLBACK);
            }
        });
        setContentView(web);
        if (b != null) web.restoreState(b);
        else web.loadUrl(SITE_URL.startsWith("http") ? SITE_URL : FALLBACK);
    }

    @Override protected void onSaveInstanceState(Bundle o) { super.onSaveInstanceState(o); web.saveState(o); }

    @Override public void onBackPressed() { if (web.canGoBack()) web.goBack(); else super.onBackPressed(); }
}
