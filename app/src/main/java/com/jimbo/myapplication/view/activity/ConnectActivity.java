package com.jimbo.myapplication.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.jimbo.myapplication.utils.SDNUUtils;
import com.jimbo.myapplication.utils.WIFIUtils;
import com.jimbo.myapplication.view.IConnectToNetView;

/**
 * description:
 *
 * @author jimbo zhongjinbao1994@gmail.com
 * @since 2016/4/10 17:11
 */
public class ConnectActivity extends AppCompatActivity implements IConnectToNetView {

    //信息提示
    private TextView tvSDNUMessage;
    private TextView tvNetStatus;
    private TextView tvWifiName;
    private TextView tvUserName;

    private ConnectToNetPresenter connectToNetPresenter =
            new ConnectToNetPresenter(this);

    //权限申请
    private static final int REQUEST_PERMISSION_CODE = 0x001;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMyApplicationPermission();
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

        assert btLendToSDNU != null;
        btLendToSDNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WIFIUtils.getWifiName() == null) {
                    Toast.makeText(ConnectActivity.this, "请先连接SDNU", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectToNetPresenter.connect(SDNUUtils.getSDNU(), Config.URL);
            }
        });

        assert btRefreshWifiStatus != null;
        btRefreshWifiStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToNetPresenter.isConnectedNet();
            }
        });

        assert btRefresh != null;
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountStatus();
            }
        });
    }

    /*
        int checkSelfPermission(String permission) 用来检测应用是否已经具有权限
        void requestPermissions(String[] permissions, int requestCode) 进行请求单个或多个权限
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    */
    @TargetApi(Build.VERSION_CODES.M)
    void requestMyApplicationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            //connectToNetPresenter.checkUpdate();
            return;
        }
        if (ContextCompat.checkSelfPermission(ConnectActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        } else {
            //connectToNetPresenter.checkUpdate();
            //Toast.makeText(ConnectActivity.this, "检测更新", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //connectToNetPresenter.checkUpdate();
                //Toast.makeText(ConnectActivity.this, "检测更新", Toast.LENGTH_SHORT).show();
            } else {

                //Toast.makeText(ConnectActivity.this, "拒绝授权", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this).setTitle("警告")
                        .setMessage("您已经拒绝给应用读取SD的权限，应用将无法检测自动升级。" +
                                "您可以在设置中重新给应用添加权限或者手动去" +
                                "https://www.pgyer.com/upsdun下载最新版")
                        .setPositiveButton("知道啦~", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        }
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

    @Override
    public void update(String versionName, String versionDescription) {
        String[] descriptions = versionDescription.split("-");
        String message = "最新版:"+versionName;
        for (String d:descriptions) {
            message += ("\n" + d);
        }
        new AlertDialog.Builder(this).setTitle("发现新版本")
                .setMessage(message)
                .setPositiveButton("更新~", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("忽略~", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

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

//            case R.id.checkNew:
//                requestMyApplicationPermission();
//                break;

            default:
        }
        return true;
    }

}
