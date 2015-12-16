package com.jimbo.myapplication.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * http工具类
 * Created by Administrator on 2015/9/15.
 */
public class HttpUtils {
    private static final String URL = "http://www.tuling123.com/openapi/api";
    private static final String API_KEY = "5eea7e89484428c1c0d495cc5a2ab515";

    public static String doGet(String userAsk) {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        String result = "";
        try {
            URL url = new URL(setParams(userAsk));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            in = connection.getInputStream();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            result = new String(out.toByteArray());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String setParams(String message) {
        String url = "";
        try {
            url = URL + "?key=" + API_KEY + "&info=" + URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
