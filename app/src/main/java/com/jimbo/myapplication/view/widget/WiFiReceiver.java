package com.jimbo.myapplication.view.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.R;
import com.jimbo.myapplication.utils.HttpPostUtils;
import com.jimbo.myapplication.utils.PrefUtils;
import com.jimbo.myapplication.view.activity.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jimbo
 * Created by Administrator on 2015/8/31.
 */
public class WiFiReceiver extends BroadcastReceiver {

    WifiManager manager = null;
    NotificationManager notificationManager = null;
    Notification notification = null;
    private HttpPostUtils post;

    private boolean isConnectting = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("-接收到广播-");
        System.out.println("component"+intent.getComponent());
        System.out.println("action"+intent.getAction());
        System.out.println("categoies"+intent.getCategories());
        System.out.println("data"+intent.getData());
        System.out.println("datatype"+intent.getType());
        System.out.println("dataschema"+intent.getScheme());
        if (null == notificationManager) {
            notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (null == notification) {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.sdnu)
                    .setTicker(System.currentTimeMillis() + "")
                    .setContentText("成功连接到sdnu")
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .getNotification();
            notification.icon = R.mipmap.sdnu;

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);

//            notification.setLatestEventInfo(context, "UpSDNU", "成功连接到sdnu", pendingIntent);

        }


        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int status = manager.getWifiState();
        switch (status) {
            case WifiManager.WIFI_STATE_ENABLED:
                PrefUtils.putInt(context, Config.WIFI_STATUS_RECORD, WifiManager.WIFI_STATE_ENABLED);
                String wifiName = manager.getConnectionInfo().getSSID();
                if ("\"sdnu\"".equals(wifiName) && !isConnectting) {
                    post = new HttpPostUtils(getSDNU(context), Config.URL, handler);
                    post.start();
                    isConnectting = true;
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            default:
                break;
        }

    }

    private Map<String, String> getSDNU(Context context) {
        Map<String, String> map = new HashMap<>();
        String name = PrefUtils.getString(context,
                Config.SDNU_USERNAME, null);
        String password = PrefUtils.getString(context,
                Config.SDNU_PASSWORD, null);

        if (name == null || password == null) {
            return null;
        }

        map.put("id", "2000");
        map.put("strAccount", name);
        map.put("strPassword", password);
        map.put("savePWD", "0");
        return map;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (Config.SUCCESSTOLEADSDNU == msg.what) {
                internet();
            }
            return true;
        }
    });

    public void internet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                    HttpGet get = new HttpGet("https://www.baidu.com");
                    HttpResponse response = client.execute(get);
                    if (200 == response.getStatusLine().getStatusCode()) {
                        message.what = Config.SUCCESSTOLEADSDNU;
                    } else {
                        message.what = Config.FAILTOLEADSDNU;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = Config.FAILTOLEADSDNU;
                }
                internetHandler.sendMessage(message);
            }
        }).start();
    }
    Handler internetHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                notificationManager.notify(100001, notification);
            } else {
                if (post.getCount() < 10) {
                    post.start();
                }
            }
            return true;
        }
    });
}
