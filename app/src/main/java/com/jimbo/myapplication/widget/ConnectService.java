package com.jimbo.myapplication.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.MyApplication;
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

    NotificationManager nm = null;
    Notification.Builder builder = null;

    private final static int NOTIFICATION_CODE = 123545;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null == connectToNetPresenter) {
            connectToNetPresenter = new ConnectToNetPresenter(this);
        }
        if (null == builder) {
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            builder = new Notification.Builder(MyApplication.getApplication());
            builder.setContentTitle("upsdnu")
                    .setContentText("正在连接...")
                    .setWhen(System.currentTimeMillis());

            nm.notify(NOTIFICATION_CODE, builder.build());
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

        builder.setContentText("连接成功~")
                .setWhen(System.currentTimeMillis());

        nm.notify(NOTIFICATION_CODE, builder.build());

    }

    @Override
    public void trying() {

    }

    @Override
    public void failed() {
        builder.setContentText("连接失败~")
                .setWhen(System.currentTimeMillis());

        nm.notify(NOTIFICATION_CODE, builder.build());
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
