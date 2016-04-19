package com.jimbo.myapplication;

import android.app.Application;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 15:03
 */
public class MyApplication extends Application {

    private static MyApplication INSTANCE_OF_APPLICATION;

    public MyApplication() {
        INSTANCE_OF_APPLICATION = this;
    }

    public static MyApplication getApplication() {
        return INSTANCE_OF_APPLICATION;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
