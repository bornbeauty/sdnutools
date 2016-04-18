package com.jimbo.myapplication.presenter;

import android.os.Handler;
import android.os.Message;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.model.ConnectToNetImp;
import com.jimbo.myapplication.model.IConnectToNet;
import com.jimbo.myapplication.view.IConnectToNetView;

import java.util.Map;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 16:43
 */
public class ConnectToNetPresenter {

    private IConnectToNet connectToNetImp = new ConnectToNetImp();
    private IConnectToNetView connectToNetView;

    private int count = 0;


    public ConnectToNetPresenter(IConnectToNetView connectToNetView) {
        this.connectToNetView = connectToNetView;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //connectToNetView.trying();
            if (msg.what == Config.SUCCESSTOLEADSDNU) {
                connectToNetView.success();
            } else {
                count++;
                if (count < 10) {
                    connectToNetImp.tryAgain();
                } else {
                    connectToNetView.failed();
                    return true;
                }
            }
            return true;
        }
    });

    public void connect(Map<String, String> params, String url) {
        count = 0;
        connectToNetView.trying();
        connectToNetImp.connectToNet(params, url, mHandler);
    }

    public void isConnectedNet() {
        connectToNetView.isCheckingNet();
        connectToNetImp.isConnectNet(NetHandler);
    }

    private Handler NetHandler = new Handler(new Handler.Callback() {
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
}
