package com.jimbo.myapplication.utils;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        BufferedReader in = null;
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

            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            String re = result.toString();
            if (re.contains("192.168.255.195:8080")) {
                message.what = 0;
            }
            message.obj = re;
        } catch (IOException e) {
            e.printStackTrace();
            message.what = 0;
            message.obj = "IOException occurred in" + getClass();
        }

        mHandler.sendMessage(message);
    }
}
