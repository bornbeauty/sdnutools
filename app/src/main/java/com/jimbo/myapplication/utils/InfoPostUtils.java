package com.jimbo.myapplication.utils;

import android.os.Handler;
import android.os.Message;

import com.jimbo.myapplication.model.IConnectToNet;
import com.jimbo.myapplication.view.IConnectToNetView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于将登陆wifi数据推送到服务器
 * Created by jsj1996m on 2016/10/22.
 */
public class InfoPostUtils {
    private static String POST_URL = "https://i.sdnu.edu.cn/oauth/rest/sdnu/push";
    private static String POST_KEY = "message";
    private Map<String,String> params;
    public InfoPostUtils(JSONArray jsonArray){
        params = new HashMap();
        params.put(POST_KEY,jsonArray.toString());
    }
    public void postInfo(Handler handler){
        HttpPostUtils httpPostUtils = new HttpPostUtils(params,POST_URL,handler);
        httpPostUtils.start();
    }

}
