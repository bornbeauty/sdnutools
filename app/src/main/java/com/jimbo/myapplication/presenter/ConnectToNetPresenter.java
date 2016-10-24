package com.jimbo.myapplication.presenter;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.model.ConnectToNetImp;
import com.jimbo.myapplication.model.IConnectToNet;
import com.jimbo.myapplication.model.bean.Version;
import com.jimbo.myapplication.utils.VersionUtils;
import com.jimbo.myapplication.view.IConnectToNetView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Stack;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 16:43
 */
public class ConnectToNetPresenter {

    private IConnectToNet connectToNetImp = new ConnectToNetImp();
    private IConnectToNetView connectToNetView;

    private Stack<String> urls;
    private Map<String, String> params;

    private int count = 0;
    private boolean hasTrying = false;


    public ConnectToNetPresenter(IConnectToNetView connectToNetView) {
        this.connectToNetView = connectToNetView;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //connectToNetView.trying();
            if (msg.what == Config.SUCCESSTOLEADSDNU) {
                String result = (String) msg.obj;
//                System.out.print(result);
                if (result.contains("has been sent")){
                    hasTrying = false;
                    connectToNetView.success();
                } else if (result.contains("失败")){
                    if (urls != null && urls.size() != 0){
//                        System.out.print("urlss" + urls.size() + urls.peek());
                        String s = urls.pop();
                        System.out.println("try"+s.substring(0,s.length()));
                        connect(params,s);
                        return true;
                    }
                    String errorMessage = result.substring(result.indexOf("失败(")+3);
                    hasTrying = false;
                    connectToNetView.failed(errorMessage.substring(0,errorMessage.indexOf(")")));
                }else if (result.contains("成功")){
                    hasTrying = false;
                    connectToNetView.success();
                }
            } else {
                count++;
                if (count < 10) {
                    connectToNetImp.tryAgain();
                    System.out.print("count=="+count);
                } else {
//                    if (urls != null && urls.size() != 0){
//                        count = 0;
//                        connect(params,urls.pop());
//                    }
                    hasTrying = false;

                    connectToNetView.failed("登陆超时");
                    return true;
                }
            }
            return true;
        }
    });

    private void saveData(JSONObject jsonObject){
    }

    public void connect(Map<String, String> params, Stack<String> urls) {
        this.urls = urls;
        this.params = params;
        connect(params,urls.pop());
    }
    public void connect(Map<String, String> params, String url) {
        count = 0;
        this.params = params;
        if (!hasTrying){
            connectToNetView.trying();
            hasTrying = true;

        }
        connectToNetImp.connectToNet(params, url, mHandler);
    }

    public void isConnectedNet() {
        connectToNetView.isCheckingNet();
        connectToNetImp.isConnectNet(netHandler);
    }

    private Handler netHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == 1) {
                connectToNetView.isConnectedNet(true);
            } else {
                connectToNetView.isConnectedNet(false);
            }

            return true;
        }
    });

    public void checkUpdate() {
        connectToNetImp.checkUpdate(updateHandler);
    }

    Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            
            if (msg.what == 1) {
                String json = (String) msg.obj;
                Gson gson = new Gson();
                Version version = gson.fromJson(json, Version.class);
                int currentVersionCode = 0;
                try {
                    currentVersionCode = new VersionUtils().getAppVersionCode();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (version.buildCode > currentVersionCode) {
                    connectToNetView.update(version.versionName, version.description);
                }
            }
            
            return true;
        }
    });
}
