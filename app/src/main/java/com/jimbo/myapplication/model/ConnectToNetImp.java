package com.jimbo.myapplication.model;


import android.os.Handler;

import com.jimbo.myapplication.utils.HttpGetUtils;
import com.jimbo.myapplication.utils.HttpPostUtils;

import java.util.Map;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 15:39
 */
public class ConnectToNetImp implements IConnectToNet {
    private HttpPostUtils httpPostUtils = null;

    @Override
    public void connectToNet(Map<String, String> params, String url, Handler mHandler) {
        if (null == httpPostUtils || httpPostUtils.getUrl() != url) {
            httpPostUtils = new HttpPostUtils(params, url, mHandler);
        }
        httpPostUtils.start();
    }

    public void tryAgain() {
        httpPostUtils.tryAgain();
    }

    @Override
    public void isConnectNet(Handler mHandler) {
        new HttpGetUtils("http://www.baidu.com", mHandler).start();
    }

    @Override
    public void checkUpdate(Handler mHandler) {
        new HttpGetUtils("http://7xt50a.com2.z0.glb.clouddn.com/upsdnu.json",
                mHandler).start();
    }

}
