package com.jimbo.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public static JSONObject getWifiInfo() throws JSONException {
        WifiManager manager = (WifiManager) MyApplication.getApplication().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
//        Map<String,String> map = new HashMap();
//        map.put("IP",""+wifiInfo.getIpAddress());
//        map.put("MAC",wifiInfo.getMacAddress());
//        map.put("SSID",wifiInfo.getSSID());
//        map.put("NetworkID",""+wifiInfo.getNetworkId());
//        map.put("LinkSpeed",""+wifiInfo.getLinkSpeed());
//        map.put("RSSI",""+wifiInfo.getRssi());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("IP", "" + ipIntToString(wifiInfo.getIpAddress()));
        jsonObject.put("MAC", wifiInfo.getMacAddress());
//        jsonObject.put("SSID", wifiInfo.getSSID());
        jsonObject.put("NetworkID", "" + wifiInfo.getNetworkId());
        jsonObject.put("LinkSpeed", "" + wifiInfo.getLinkSpeed());
        jsonObject.put("RSSI", "" + wifiInfo.getRssi());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String time = formatter.format(curDate);
        jsonObject.put("TimeStamp", time);
        return jsonObject;
    }

    /**
     * ip int转String
     */
    private static String ipIntToString(int i) {
        return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
    }

    /**
     * 检测当的网络状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
