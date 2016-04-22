package com.jimbo.myapplication.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jimbo.myapplication.MyApplication;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/19 16:21
 */
public class VersionUtils {

    public int getAppVersionCode() throws PackageManager.NameNotFoundException {

        PackageManager pm = MyApplication.
                getApplication().getPackageManager();

        PackageInfo pi = pm.getPackageInfo(MyApplication.
                getApplication().getPackageName(), 0);

        return pi.versionCode;
    }


}
