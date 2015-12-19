package com.jimbo.myapplication.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 *
 * Created by jimbo on 15-12-19.
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

    private ProgressBar progressbar;

    @SuppressLint("NewApi")
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                20, 0, 0));
        addView(progressbar);
        setWebViewClient(new WebCustomClient());
        setWebChromeClient(new WebChromeClient());
        // 发布取消
        //Web开发调试之Chrome远程调试(Remote Debugging)会用到
        // setWebContentsDebuggingEnabled(true);
        getSettings().setDomStorageEnabled(true);//解决总是跳转第三方浏览器
    }

    public class WebCustomClient extends android.webkit.WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            // 页面下载完毕,却不代表页面渲染完毕显示出来
            // WebChromeClient中progress==100时也是一样
            if (view.getContentHeight() != 0) {
                // 这个时候网页才显示
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            // 自身加载新链接,不做外部跳转
            view.loadUrl(url);
            return true;
        }

    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
