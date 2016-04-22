package com.jimbo.myapplication.utils;

import android.os.Handler;
import android.os.Message;

import com.jimbo.myapplication.Config;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 15:03
 */
public class HttpPostUtils {

    static int i = 0;
    private Map<String, String> params;
    private String url = "";
    private Handler handler;

    public HttpPostUtils(Map<String, String> params, String url, Handler handler) {
        this.params = params;
        this.url = url;
        this.handler = handler;
    }

    //开启线程进行网络请求
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startNetWork();
                System.out.println(++i+"run number");
            }
        }).start();
    }

    public void tryAgain() {
        start();
    }

    //定义网络请求
    @SuppressWarnings("deprecation")
    private void startNetWork() {
        Message message = new Message();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            HttpPost post = new HttpPost(url);
            List<NameValuePair> list = null;
            if (null != params) {
                list = new ArrayList<>();
                for (String key : params.keySet()) {
                    list.add(new BasicNameValuePair(key, params.get(key)));
                }
            }
            post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity entity = httpResponse.getEntity();
            if (200 == httpResponse.getStatusLine().getStatusCode()) {
                //message.what = Config.SUCCESSTOLEADSDNU;
                message.what = Config.SUCCESSTOLEADSDNU;
                message.obj = EntityUtils.toString(entity);
            } else {
                message.what = Config.FAILTOLEADSDNU;
            }
        } catch (IOException e) {
            e.printStackTrace();
            message.what = Config.OUTTIMETOLEADSDNU;
        } catch (Exception e) {
            e.printStackTrace();
            message.what = Config.FAILTOLEADSDNU;
        }
        handler.sendMessage(message);
    }
}
