package com.jimbo.myapplication.view.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.R;
import com.jimbo.myapplication.presenter.ConnectToNetPresenter;
import com.jimbo.myapplication.utils.WIFIUtils;
import com.jimbo.myapplication.view.IConnectToNetView;

import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 17:11
 */
public class ConnectActivity extends AppCompatActivity implements IConnectToNetView {

    private TextView tvSDNUMessage;
    private TextView tvNetStatus;
    private TextView tvWifiName;
    private TextView tvUserName;

    private ConnectToNetPresenter connectToNetPresenter =
            new ConnectToNetPresenter(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        tvWifiName.setText(WIFIUtils.getWifiName() == null ?
                "无WIFI" : WIFIUtils.getWifiName());

        tvUserName.setText(WIFIUtils.getWifiAccountName() == null ?
                "无账号" : WIFIUtils.getWifiAccountName());
    }

    private void initView() {

        tvWifiName = (TextView) findViewById(R.id.wifiName);
        tvUserName = (TextView) findViewById(R.id.userName);
        tvNetStatus = (TextView) findViewById(R.id.wifiStatus);
        tvSDNUMessage = (TextView) findViewById(R.id.sdnuMessage);

        ButtonRectangle btRefresh = (ButtonRectangle) findViewById(R.id.refresh);
        ButtonRectangle btRefreshWifiStatus = (ButtonRectangle) findViewById(R.id.refreshWiFiStatus);
        ButtonRectangle btLendToSDNU = (ButtonRectangle) findViewById(R.id.lendToSDNU);

        btLendToSDNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WIFIUtils.getWifiName() == null) {
                    Toast.makeText(ConnectActivity.this, "请先连接SDNU", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectToNetPresenter.connect(getSDNU(), Config.URL);
            }
        });

        btRefreshWifiStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToNetPresenter.isConnectedNet();
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountStatus();
            }
        });
    }

    private void accountStatus() {
        tvWifiName.setText(WIFIUtils.getWifiName() == null ?
                "" : WIFIUtils.getWifiName());
        tvUserName.setText(WIFIUtils.getWifiAccountName() == null ?
                "" : WIFIUtils.getWifiAccountName());
    }

    @Override
    public void success() {
        tvSDNUMessage.setText("连接成功~");
        isConnectedNet(true);
        tvSDNUMessage.setTextColor(Color.BLUE);
    }

    @Override
    public void trying() {
        tvSDNUMessage.setText("连接中~");
        tvSDNUMessage.setTextColor(Color.GREEN);
    }

    @Override
    public void failed() {
        tvSDNUMessage.setText("连接失败~");
        isConnectedNet(false);
        tvSDNUMessage.setTextColor(Color.RED);
    }

    @Override
    public void isConnectedNet(boolean is) {
        if (is) {
            tvNetStatus.setText("网络畅通~");
            tvNetStatus.setTextColor(Color.BLUE);
        } else {
            tvNetStatus.setText("网络断开~");
            tvNetStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public void isCheckingNet() {
        tvNetStatus.setText("正在判断~");
    }

    private Map<String, String> getSDNU() {
        Map<String, String> map = new HashMap<>();
        String name = WIFIUtils.getWifiAccountName();
        String password = WIFIUtils.getWifiAccountPassword();

        if (name == null || password == null) {
            return null;
        }
        map.put("id", "2000");
        map.put("strAccount", name);
        map.put("strPassword", password);
        map.put("savePWD", "0");
        return map;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:

                final AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
                aboutBuilder.setTitle(getResources().getString(R.string.meau_about));
                aboutBuilder.setMessage(getResources().getString(R.string.description_about));
                aboutBuilder.setPositiveButton("好哒~", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                aboutBuilder.create().show();
                break;

            case R.id.namePassword:
                final View view = getLayoutInflater().inflate(
                        R.layout.edittext_name_password, null);
                AlertDialog.Builder NPBuilder = new AlertDialog.Builder(this);
                NPBuilder.setView(view);
                NPBuilder.setTitle("设置账号和密码");

                final EditText nameEditText = (EditText) view.findViewById(R.id.name);
                final EditText passwordEditText = (EditText) view.findViewById(R.id.password);

                nameEditText.setText(WIFIUtils.getWifiAccountName() == null
                    ? "" : WIFIUtils.getWifiAccountName());
                passwordEditText.setText(WIFIUtils.getWifiAccountPassword() == null
                        ? "" : WIFIUtils.getWifiAccountPassword());

                NPBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        if (((CheckBox) view.findViewById(R.id.rememberAccount)).isChecked()) {
                            WIFIUtils.saveName(name);
                        } else {
                            WIFIUtils.saveName("");
                        }
                        if (((CheckBox) view.findViewById(R.id.rememberPassword)).isChecked()) {
                            WIFIUtils.savePassword(password);
                        } else {
                            WIFIUtils.savePassword("");
                        }
                        accountStatus();
                    }
                });
                NPBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                NPBuilder.create().show();
                break;

            default:
        }

        return true;
    }
}
