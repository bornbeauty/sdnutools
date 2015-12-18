package com.jimbo.myapplication;

import android.os.Handler;
import android.os.Message;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by Administrator on 2015/8/31.
 */
public class NetWorkPost {
    private static final int NUMBER_EXECUTORS = 10;
    /**
     * params 是用户名和密码
     * url 是认证地址
     * handler 是回调方法
     */
    private Map<String, String> params;
    private String url = "";
    private Handler handler;
//    private ScheduledExecutorService executors =
//            Executors.newScheduledThreadPool(NUMBER_EXECUTORS);

    private int mCount = 0;

    public NetWorkPost(Map<String, String> params, String url, Handler handler) {
        this.params = params;
        this.url = url;
        this.handler = handler;
    }

    public int getCount() {
        return mCount;
    }

    //开启线程进行网络请求
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startNetWork();
            }
        }).start();

//        executors.schedule(new Runnable() {
//            @Override
//            public void run() {
//                startNetWork();
//                number++;
//                System.out.println("lead number:"+number);
//            }
//        }, 0L, TimeUnit.SECONDS);
    }

    /**
     * 没间隔一段时间就执行一次请求
     * @param delayTime 延时多久后开始执行
     * @param interval 间隔多长时间执行操作
     */
//    public void start(long delayTime, long interval) {
//        executors.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                startNetWork();
//
//            }
//        }, delayTime, interval, TimeUnit.SECONDS);
//    }

    /**
     * 关闭线程池
     */
//    public void shutdownExecutor() {
//        executors.shutdownNow();
//    }

    //定义网络请求
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
                mCount = 0;
            } else {
                message.what = Config.FAILTOLEADSDNU;
                mCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            mCount++;
            message.what = Config.OUTTIMETOLEADSDNU;
        } catch (Exception e) {
            e.printStackTrace();
            mCount++;
            message.what = Config.FAILTOLEADSDNU;
        }
        handler.sendMessage(message);
    }
}
