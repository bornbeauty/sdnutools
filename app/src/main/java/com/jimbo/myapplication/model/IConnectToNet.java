package com.jimbo.myapplication.model;

import android.os.Handler;

import java.util.Map;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 15:33
 */
public interface IConnectToNet {
    void connectToNet(Map<String, String> params, String url, Handler mHandler);
    public void tryAgain();
}
