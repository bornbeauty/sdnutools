package com.jimbo.myapplication.model;


import java.util.Map;
import android.os.Handler;
import com.jimbo.myapplication.utils.HttpPostUtils;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 15:39
 */
public class ConnectToNetImp implements ConnectToNet {
    @Override
    public void connectToNet(Map<String, String> params, String url, Handler mHandler) {
        new HttpPostUtils(params, url, mHandler).start();
    }
}
