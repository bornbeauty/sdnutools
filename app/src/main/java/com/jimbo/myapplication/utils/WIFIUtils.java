package com.jimbo.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.MyApplication;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 19:25
 */
public class WIFIUtils {
    public static String getWifiName() {
        WifiManager manager = (WifiManager) MyApplication.getApplication().getSystemService(
                Context.WIFI_SERVICE);
        String wifiName = manager.getConnectionInfo().getSSID();
        if (wifiName.equals("<unknown ssid>")) {
            wifiName = "未连接WIFi";
        } else {
            wifiName = wifiName.substring(1, wifiName.length() - 1);
        }
        return wifiName;
    }

    public static String getWifiAccountName() {
        return PrefUtils.getString(MyApplication.getApplication(),
                Config.SDNU_USERNAME, null);
    }

    public static String getWifiAccountPassword() {
        return PrefUtils.getString(MyApplication.getApplication(),
                Config.SDNU_PASSWORD, null);
    }

    public static void saveName(String name) {
        PrefUtils.putString(MyApplication.getApplication(), Config.SDNU_USERNAME, name);
    }

    public static void savePassword(String password) {
        PrefUtils.putString(MyApplication.getApplication(), Config.SDNU_PASSWORD, password);
    }
}
