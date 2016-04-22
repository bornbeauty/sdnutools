package com.jimbo.myapplication.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.jimbo.myapplication.MyApplication;

/**
 * @author jimbo
 * Created by Administrator on 2015/8/31.
 */
public class WiFiReceiver extends BroadcastReceiver {

    WifiManager manager = null;

    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("-接收到广播-");
//        System.out.println("component"+intent.getComponent());
//        System.out.println("action"+intent.getAction());
//        System.out.println("categoies"+intent.getCategories());
//        System.out.println("data"+intent.getData());
//        System.out.println("datatype"+intent.getType());
//        System.out.println("dataschema"+intent.getScheme());
        if (null == manager) {
            manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        int status = manager.getWifiState();


        switch (status) {
            case WifiManager.WIFI_STATE_ENABLED:
                String wifiName = manager.getConnectionInfo().getSSID();
                if (wifiName.contains("sdnu")) {
                    MyApplication.getApplication()
                            .startService(new Intent(MyApplication.getApplication(),
                                    ConnectService.class));
                }
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            default:
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
        }

    }



}
