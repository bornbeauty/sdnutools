package com.jimbo.myapplication;

import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by jimbo on 15-11-17.
 */
public class UpSDNU extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PgyCrashManager.register(this);
    }
}
