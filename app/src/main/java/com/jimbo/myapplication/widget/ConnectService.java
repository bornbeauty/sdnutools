package com.jimbo.myapplication.widget;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.MyApplication;
import com.jimbo.myapplication.R;
import com.jimbo.myapplication.presenter.ConnectToNetPresenter;
import com.jimbo.myapplication.utils.SDNUUtils;
import com.jimbo.myapplication.view.IConnectToNetView;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/19 17:02
 */
public class ConnectService extends Service implements IConnectToNetView {

    ConnectToNetPresenter connectToNetPresenter = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null == connectToNetPresenter) {
            connectToNetPresenter = new ConnectToNetPresenter(this);
        }

        connectToNetPresenter.connect(SDNUUtils.getSDNU(), Config.URL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void success() {

        //stopForeground(true);

        new Notification.Builder(MyApplication.getApplication())
                .setContentTitle("new message from upsdnd")
                .setContentText("连接成功~")
                .setSmallIcon(R.mipmap.sdnu)
                .setWhen(System.currentTimeMillis())
                .build();

        stopSelf();

    }

    @Override
    public void trying() {

    }

    @Override
    public void failed() {

    }

    @Override
    public void isConnectedNet(boolean is) {

    }

    @Override
    public void isCheckingNet() {

    }

    @Override
    public void update(String versionName, String versionDescription) {

    }
}
