package com.jimbo.myapplication.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.jimbo.myapplication.MyApplication;
import com.jimbo.myapplication.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/22 14:14
 */
public class DownLoadService extends Service {

    private Notification.Builder builder = null;
    private NotificationManager nm = null;
    private RemoteViews remoteViews = null;

    private static final int DOWNLOADNOTIFICATION = 123232;
    private static final String DOWNLOADPATH =
            Environment.getExternalStorageDirectory().getPath()+"/upsdnu/update/";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.progressbar_download);
        builder = new Notification.Builder(MyApplication.
                getApplication());
        builder.setContent(remoteViews)
                .setTicker("开始下载~")
                .setSmallIcon(R.mipmap.sdnu);

        String url;
        if (null == (url = intent.getStringExtra("APKURL"))) {
            throw new RuntimeException("不合法的参数");
        }


        asyncTask.execute(url);

        return super.onStartCommand(intent, flags, startId);
    }


    AsyncTask<String, Integer, Void> asyncTask = new AsyncTask<String, Integer, Void>() {

        @Override
        protected void onPreExecute() {
            nm.notify(DOWNLOADNOTIFICATION, builder.build());
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            FileOutputStream os = null;
            BufferedInputStream bfin = null;
            int currentLength;
            int total = 0;
            try {

                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(3000);
                connection.connect();
                int contentLength = connection.getContentLength();
                File file = new File(DOWNLOADPATH);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        return null;
                    }
                }
                os = new FileOutputStream(file);
                bfin = new BufferedInputStream(connection.getInputStream());
                byte[] buff = new byte[1024];
                while ((currentLength = bfin.read(buff)) != -1) {
                    os.write(buff);
                    total += currentLength;
                }
                publishProgress((int)(total/contentLength));

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (bfin != null) {
                        bfin.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            remoteViews.setProgressBar(R.id.download_progress, 100, values[0], true);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
