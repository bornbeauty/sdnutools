package com.jimbo.myapplication.utils;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/18 20:00
 */
public class HttpGetUtils {

    private String url = "http://www.baidu.com";
    private Handler mHandler;

    public HttpGetUtils(String url, Handler mHandler) {
        this.url = url;
        this.mHandler = mHandler;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startNetWork();
            }
        }).start();
    }

    private void startNetWork() {
        Message message = Message.obtain();
        try {
            URL mUrl = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setReadTimeout(2000);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                message.what = 1;
            } else {
                message.what = 0;
            }
            message.obj = connection.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
            message.what = 0;
            message.obj = "IOException occurred in" + getClass();
        }
            mHandler.sendMessage(message);
    }
}
