package com.jimbo.myapplication.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.DownloadListener;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.R;
import com.jimbo.myapplication.view.view.ProgressWebView;

/**
 *
 * Created by jimbo on 15-12-18.
 */

public class HelpActivity extends AppCompatActivity {

    ProgressWebView webview = null;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("使用教程");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webview = (ProgressWebView) findViewById(R.id.webview);

        // ~~~ 设置数据
        webview.setVerticalScrollbarOverlay(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url != null
                        && (url.startsWith("http://") || url
                        .startsWith("file:///")))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        webview.loadUrl(Config.HELP_PAGE);


    }
}
