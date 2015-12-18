package com.jimbo.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private ButtonRectangle btRefreshWifiStatus;
    private TextView tvWifiName;
    private TextView tvUserName;
    private ButtonRectangle btRefresh;
    private ButtonRectangle btLendToSDNU;

    private NetWorkPost post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_newmain);
//
//        final ProgressLayout p = (ProgressLayout) findViewById(R.id.progressLayout);
//        p.start();
//        p.setCurrentProgress(10);
//        p.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                p.stop();
//            }
//        });

        //PgyUpdateManager.register(this);

        //初始化控件
        initView();
        //显示信息
        showMessage();
        //连接
        internet();
//        /upNew();
    }

    private void upNew() {
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {

                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("更新")
                                .setMessage("-")
                                .setNegativeButton(
                                        "确定",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(
                                                        MainActivity.this,
                                                        appBean.getDownloadURL());
                                            }
                                        }).create().show();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        MainActivity.this.finish();
                    }
                });
    }

    private String getMessage(String html) {
        Document doc = Jsoup.parse(html);
        Elements div = doc.getElementsByClass("b_cernet");
        Elements trs = div.get(0).getElementsByTag("tr");
        String message = trs.get(1).text();
        return message;
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
        final View view = getLayoutInflater().inflate(R.layout.test, null);
        switch (item.getItemId()) {
            case R.id.namePassword:
                builder.setTitle("设置账号");
                builder.setView(view);
                final EditText nameEditText = (EditText) view.findViewById(R.id.name);
                final EditText passwordEditText = (EditText) view.findViewById(R.id.password);
                nameEditText.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_USERNAME, ""));
                passwordEditText.setText(PrefUtils.getString(MainActivity.this,
                        Config.SDNU_PASSWORD, ""));
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                        if (((CheckBox)view.findViewById(R.id.rememberAccount)).isChecked()) {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_USERNAME, name);
                        } else {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_USERNAME, "");
                        }
                        if (((CheckBox)view.findViewById(R.id.rememberPassword)).isChecked()) {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_PASSWORD, password);
                        } else {
                            PrefUtils.putString(MainActivity.this, Config.SDNU_PASSWORD, "");
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.about:
                builder.setTitle("关于");
                builder.setMessage("该APP为了让同学们更好更方便的使用SDNU网络。保证不会收集同学们的账号，请放心。" +
                        "有任何问题请联系QQ：965735056。");
                builder.setPositiveButton("好哒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;

            case R.id.chat:
                startActivity(new Intent(MainActivity.this, RootChatActivity.class));
                break;
            case R.id.grade:
                AlertDialog.Builder builderr = new AlertDialog.Builder(this);
                final View vieww = getLayoutInflater().inflate(R.layout.test, null);
                builderr.setTitle("登入教务处");
                builderr.setView(vieww);
                System.out.println("1");
                builderr.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            String name = ((EditText) vieww.findViewById(R.id.name)).getText().toString().trim();
                            String password = ((EditText) vieww.findViewById(R.id.password)).getText().toString().trim();
                            Intent in = new Intent(MainActivity.this, GradeActivity.class);
                            in.putExtra("name", name);
                            in.putExtra("password", password);
                            MainActivity.this.startActivity(in);
                        } catch (NullPointerException e) {
                            Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "这里", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dialog.dismiss();
                    }
                });
                builderr.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderr.create().show();
                break;
            //case R.id.start:
                //startActivity(new Intent(this, StartActivity.class));
            default:
                return false;
        }

        return true;
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
            return "未设置账号";
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

        btRefresh = (ButtonRectangle) findViewById(R.id.refresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage();
            }
        });

        btRefreshWifiStatus = (ButtonRectangle) findViewById(R.id.refreshWiFiStatus);
        btRefreshWifiStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internet();
            }
        });

        btLendToSDNU = (ButtonRectangle) findViewById(R.id.lendToSDNU);
        btLendToSDNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getWifiName().equals(Config.WIFI_NAME)) {
                    lendToSDNU();
                } else {
                    tvSDNUMessage.setText("未知");
                    Toast.makeText(MainActivity.this, "尚未连接到sdnu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void lendToSDNU() {
        Map<String, String> map = getSDNU();
        if (null == map) {
            Toast.makeText(MainActivity.this, "请设置正确的账号密码！", Toast.LENGTH_SHORT).show();
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
                    HttpGet get = new HttpGet("https://www.baidu.com");
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
                tvWifiStatus.setText("网络畅通");
            } else {
                tvWifiStatus.setTextColor(Color.RED);
                tvWifiStatus.setText("网络不可用");
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
                    tvWifiStatus.setText("网络畅通");
                    tvWifiStatus.setTextColor(Color.BLUE);
                    tvSDNUMessage.setText("连接成功");
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                } else {
                    tvWifiStatus.setText("网络不可用");
                    tvSDNUMessage.setText(message);
                }
            } else if (Config.FAILTOLEADSDNU == what) {
                tvSDNUMessage.setText("没有连接到sdnu或者sdnu网络异常");
                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                if (post.getCount() > 10) {
                    Toast.makeText(MainActivity.this, "连接失败 正在重新尝试", Toast.LENGTH_SHORT).show();
                    post.start();
                    return true;
                }
            } else {
                if (post.getCount() > 10) {
                    Toast.makeText(MainActivity.this, "连接失败 正在重新尝试", Toast.LENGTH_SHORT).show();
                    post.start();
                    return true;
                }
                tvSDNUMessage.setText("sdnu可能很忙 现在你不能Up他");
                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
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
