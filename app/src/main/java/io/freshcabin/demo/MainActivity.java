package io.freshcabin.demo;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private WebView browser;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        final String html;
        try (InputStream source = getAssets().open("index.html")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int read;
            while ((read = source.read(chunk)) != -1) buffer.write(chunk, 0, read);
            html = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException error) {
            TextView message = new TextView(this);
            message.setText("FreshCabin 시연 화면을 열 수 없습니다.");
            setContentView(message);
            return;
        }

        browser = new WebView(this);
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        setContentView(browser);
        browser.loadDataWithBaseURL("https://freshcabin.invalid/", html, "text/html", "UTF-8", null);
    }

    @Override
    protected void onPause() {
        if (browser != null) {
            browser.evaluateJavascript("if (typeof pause === 'function') { pause(); render(); }", null);
            browser.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (browser != null) browser.onResume();
    }

    @Override
    protected void onDestroy() {
        if (browser != null) browser.destroy();
        super.onDestroy();
    }
}
