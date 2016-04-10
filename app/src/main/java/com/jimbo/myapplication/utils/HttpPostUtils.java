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

    private boolean isFirstCalled = true;
    private boolean isOnlyOnceCalled = true;

    private Map<String, String> params;
    private String url = "";
    private Handler handler;

    private Thread myThread;

    public HttpPostUtils(Map<String, String> params, String url, Handler handler) {
        this.params = params;
        this.url = url;
        this.handler = handler;
    }

    public void setOnlyOnceCalled(boolean is) {
        isOnlyOnceCalled = is;
    }

    //开启线程进行网络请求
    public void start() {

        if (isOnlyOnceCalled && !isFirstCalled) {
            throw new RuntimeException("function start() of " +
                    "com.jimbo.myapplication.utils.HttpPostUtils can be only called one time");
        }
        isFirstCalled = false;
        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startNetWork();
            }
        });
        myThread.start();
    }

    public void tryAgain() {
        myThread.run();
    }

    //定义网络请求
    @SuppressWarnings("deprecation")
    private void startNetWork() {
        Message message = new Message();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
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
