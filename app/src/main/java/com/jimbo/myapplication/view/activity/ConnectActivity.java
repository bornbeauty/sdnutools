package com.jimbo.myapplication.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.jimbo.myapplication.view.IConnectToNetView;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 17:11
 */
public class ConnectActivity extends AppCompatActivity implements IConnectToNetView {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void success() {

    }

    @Override
    public void trying() {

    }

    @Override
    public void failed() {

    }

}
