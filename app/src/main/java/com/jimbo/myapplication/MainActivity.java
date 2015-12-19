package com.jimbo.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptIntrinsicYuvToRGB;
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
import com.gc.materialdesign.widgets.SnackBar;
import com.jimbo.myapplication.utils.NetWorkPost;
import com.jimbo.myapplication.utils.PrefUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView tvSDNUMessage;
    private TextView tvWifiStatus;
    private TextView tvWifiName;
    private TextView tvUserName;
    //联网class
    private NetWorkPost post;

    private int id = R.id.this_grade;

    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PgyUpdateManager.register(this);
        resources = getResources();
        //初始化控件
        initView();
        //显示信息
        showMessage();
        //连接
        internet();

        upNew();
    }

    private void upNew() {
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.update_title))
                                .setMessage(appBean.getReleaseNote()+"\n"+"版本号:"
                                    +appBean.getVersionCode()+"\n版本名称:"+appBean.getVersionName())
                                .setPositiveButton(
                                        "更新",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(
                                                        MainActivity.this,
                                                        appBean.getDownloadURL());
                                            }
                                        })
                                .setNegativeButton(getResources().getString(R.string.update_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                .create().show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        SnackBar bar = new SnackBar(MainActivity.this,
                                resources.getString(R.string.update_latest));
                        bar.show();
                    }
                });
    }

    private String getMessage(String html) {
        Document doc = Jsoup.parse(html);
        Elements div = doc.getElementsByClass("b_cernet");
        Elements trs = div.get(0).getElementsByTag("tr");
        return trs.get(1).text();
    }

    private boolean isSuccessUp(String message) {
        if (message.contains("has been sent")) {
            return true;
        } else if (message.contains("成功")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.namePassword:
                final View view = getLayoutInflater().inflate(R.layout.test, null);
                builder.setTitle(resources.getString(R.string.meau_setAccPsw));
                builder.setView(view);
                final EditText nameEditText = (EditText) view.findViewById(R.id.name);
                final EditText passwordEditText = (EditText) view.findViewById(R.id.password);
                nameEditText.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_USERNAME, ""));
                passwordEditText.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_PASSWORD, ""));
                builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                        if (((CheckBox) view.findViewById(R.id.rememberAccount)).isChecked()) {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_USERNAME, name);
                        } else {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_USERNAME, "");
                        }
                        if (((CheckBox) view.findViewById(R.id.rememberPassword)).isChecked()) {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_PASSWORD, password);
                        } else {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_PASSWORD, "");
                        }
                        showMessage();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.about:
                builder.setTitle(resources.getString(R.string.meau_about));
                builder.setMessage(resources.getString(R.string.description_about));
                builder.setPositiveButton(resources.getString(R.string.mengmeng_done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;

//            case R.id.chat:
//                startActivity(new Intent(MainActivity.this, RootChatActivity.class));
//                break;
            case R.id.grade:
                id = R.id.grade;
            case R.id.this_grade:
                //id = R.id.this_grade;
                AlertDialog.Builder builderr = new AlertDialog.Builder(this);
                final View vieww = getLayoutInflater().inflate(R.layout.test, null);
                builderr.setTitle(resources.getString(R.string.login_jwc));
                builderr.setView(vieww);
                final EditText nameEditText2 = (EditText) vieww.findViewById(R.id.name);
                final EditText passwordEditText2 = (EditText) vieww.findViewById(R.id.password);
                //恢复上一次的账号密码
                nameEditText2.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_JWC_STUID, ""));
                passwordEditText2.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_JWC_PSW, ""));
                builderr.setPositiveButton(resources.getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String name = nameEditText2.getText().toString().trim();
                            String password = passwordEditText2.getText().toString().trim();
                            if (((CheckBox)vieww.findViewById(R.id.rememberAccount)).isChecked()) {
                                PrefUtils.putString(MainActivity.this, Config.SDNU_JWC_STUID, name);
                            } else {
                                PrefUtils.putString(MainActivity.this, Config.SDNU_JWC_STUID, "");
                            }
                            if (((CheckBox)vieww.findViewById(R.id.rememberPassword)).isChecked()) {
                                PrefUtils.putString(MainActivity.this, Config.SDNU_JWC_PSW, password);
                            } else {
                                PrefUtils.putString(MainActivity.this, Config.SDNU_JWC_PSW, "");
                            }
                            startAct(name, password);
                        } catch (NullPointerException e) {
                            new SnackBar(MainActivity.this,
                                    resources.getString(R.string.null_description)).show();
                            return;
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, resources.getString
                                    (R.string.exception_description), Toast.LENGTH_SHORT).show();
                            //PgyCrashManager.reportCaughtException(MainActivity.this, e);
                            e.printStackTrace();
                            return;
                        }
                        dialog.dismiss();
                    }
                });
                builderr.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderr.create().show();
                break;

            case R.id.checkNew:
                upNew();
                break;

            case R.id.help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                break;

            default:
                return false;
        }

        return true;
    }

    private void startAct(String name, String password) {
        Intent in = new Intent();
        if (id == R.id.grade) {
            in.setClass(MainActivity.this, GradeActivity.class);
            Toast.makeText(MainActivity.this, "绩点", Toast.LENGTH_SHORT).show();
        } else {
            in.setClass(MainActivity.this, ThisGradeActivity.class);
            Toast.makeText(MainActivity.this, "本学期成绩", Toast.LENGTH_SHORT).show();
        }
        id = R.id.this_grade;
        in.putExtra("name", name);
        in.putExtra("password", password);
        MainActivity.this.startActivity(in);
    }

    private String getWifiName() {
        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        String wifiName = manager.getConnectionInfo().getSSID();
        if (wifiName.equals("<unknown ssid>")) {
            wifiName = "未连接WIFi";
            tvWifiName.setTextColor(Color.RED);
        } else {
            wifiName = wifiName.substring(1, wifiName.length() - 1);
            tvWifiName.setTextColor(Color.BLUE);
        }
        return wifiName;
    }

    private String getUserName() {
        String userName = PrefUtils.getString(MainActivity.this, Config.SDNU_USERNAME,
                null);
        if (userName != null) {
            tvUserName.setTextColor(Color.BLUE);
            return userName;
        } else {
            tvUserName.setTextColor(Color.RED);
            return resources.getString(R.string.point_set_account);
        }
    }

    private void showMessage() {
        tvWifiName.setText(getWifiName());
        tvUserName.setText(getUserName());
    }

    private void initView() {
        tvWifiName = (TextView) findViewById(R.id.wifiName);
        tvUserName = (TextView) findViewById(R.id.userName);
        tvWifiStatus = (TextView) findViewById(R.id.wifiStatus);
        tvSDNUMessage = (TextView) findViewById(R.id.sdnuMessage);

        ButtonRectangle btRefresh = (ButtonRectangle) findViewById(R.id.refresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage();
            }
        });

        ButtonRectangle btRefreshWifiStatus = (ButtonRectangle) findViewById(R.id.refreshWiFiStatus);
        btRefreshWifiStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internet();
            }
        });

        ButtonRectangle btLendToSDNU = (ButtonRectangle) findViewById(R.id.lendToSDNU);
        btLendToSDNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getWifiName().equals(Config.WIFI_NAME)) {
                    lendToSDNU();
                } else {
                    tvSDNUMessage.setText(R.string.no_network);
                    Toast.makeText(MainActivity.this, R.string.not_link_sdnu, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void lendToSDNU() {
        Map<String, String> map = getSDNU();
        if (null == map) {
            Toast.makeText(MainActivity.this, R.string.point_right_account, Toast.LENGTH_SHORT).show();
            return;
        }
        post = new NetWorkPost(map, Config.URL, handler);
        post.start();
    }

    public void internet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                    HttpGet get = new HttpGet(getString(R.string.test_host));
                    HttpResponse response = client.execute(get);
                    if (200 == response.getStatusLine().getStatusCode()) {
                        message.what = Config.SUCCESSTOLEADSDNU;
                    } else {
                        message.what = Config.FAILTOLEADSDNU;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = Config.FAILTOLEADSDNU;
                }
                internetHandler.sendMessage(message);
            }
        }).start();
    }

    Handler internetHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                tvWifiStatus.setTextColor(Color.BLUE);
                tvWifiStatus.setText(R.string.normal_network);
            } else {
                tvWifiStatus.setTextColor(Color.RED);
                tvWifiStatus.setText(R.string.not_network);
            }
            return true;
        }
    });

    static int count = 0;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            String html = (String) msg.obj;

            if (1 == what) {
                count = 0;
                String message = getMessage(html);
                if (isSuccessUp(message)) {
                    tvWifiStatus.setText(R.string.normal_network);
                    tvWifiStatus.setTextColor(Color.BLUE);
                    tvSDNUMessage.setText(R.string.link_success);
                    new SnackBar(MainActivity.this,
                            getResources().getString(R.string.link_success)).show();
                } else {
                    tvWifiStatus.setText(R.string.not_network);
                    tvSDNUMessage.setText(message);
                }
            } else if (Config.FAILTOLEADSDNU == what) {
                tvSDNUMessage.setText(R.string.link_failed);
                Toast.makeText(MainActivity.this, R.string.link_failed_all, Toast.LENGTH_SHORT).show();
                if (post.getCount() < 10) {
                    count++;
                    Toast.makeText(MainActivity.this, R.string.link_again, Toast.LENGTH_SHORT).show();
                    post.start();
                    return true;
                }
            } else {
                if (post.getCount() < 10) {
                    count++;
                    Toast.makeText(MainActivity.this, R.string.link_again, Toast.LENGTH_SHORT).show();
                    post.start();
                    return true;
                }
                tvSDNUMessage.setText(R.string.link_failed_text);
                Toast.makeText(MainActivity.this, R.string.link_failed_all, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });

    private Map<String, String> getSDNU() {
        Map<String, String> map = new HashMap<>();
        String name = PrefUtils.getString(MainActivity.this,
                Config.SDNU_USERNAME, null);
        String password = PrefUtils.getString(MainActivity.this,
                Config.SDNU_PASSWORD, null);

        if (name == null || password == null) {
            return null;
        }
        map.put("id", "2000");
        map.put("strAccount", name);
        map.put("strPassword", password);
        map.put("savePWD", "0");
        return map;
    }
}
