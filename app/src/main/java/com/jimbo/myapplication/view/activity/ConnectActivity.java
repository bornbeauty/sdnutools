package com.jimbo.myapplication.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.GsonBuilder;
import com.jimbo.myapplication.Config;
import com.jimbo.myapplication.R;
import com.jimbo.myapplication.presenter.ConnectToNetPresenter;
import com.jimbo.myapplication.utils.AnimatorUtils;
import com.jimbo.myapplication.utils.InfoPostUtils;
import com.jimbo.myapplication.utils.SDNUUtils;
import com.jimbo.myapplication.utils.WIFIUtils;
import com.jimbo.myapplication.view.IConnectToNetView;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Stack;

import at.markushi.ui.CircleButton;
import cn.edu.sdnu.i.util.oauth.AppSDNU;
import cn.edu.sdnu.i.util.oauth.Oauth;

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

    AnimatorUtils animatorUtils = new AnimatorUtils(this);

    private CircleButton circleButton;
    private CircleButton centerImg;
    RippleBackground rippleBackground;
//等待上传的对象，方便存储
    private JSONObject toPostWifiInfo;

    //权限申请
    private static final int REQUEST_PERMISSION_CODE = 0x001;

    private static final int RIPP_CONNECTING = 1;
    private static final int RIPP_SUCCESS = 0;
    private static final int RIPP_FAIL = -1;

    private static final String CACHE_LENGTH_KEY = "length";
    private static final String CACHE_NAME = "wifiCache";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMyApplicationPermission();
        initView();
        initData();
        showStatement();
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
        circleButton = (CircleButton) findViewById(R.id.circleButton);
        rippleBackground = (RippleBackground) findViewById(R.id.rippleBackground);

        centerImg = (CircleButton) findViewById(R.id.centerImage);
        centerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lendToSdnu();
            }
        });

        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float targetX = centerImg.getX() - centerImg.getWidth() + ConnectActivity.dip2px(ConnectActivity.this, 16);//-(circleButton.getWidth()-img.getWidth())/2-img.getWidth();//-(img.getWidth()/2)/3;
                float targetY = centerImg.getY() - centerImg.getHeight() + ConnectActivity.dip2px(ConnectActivity.this, 16);//-(circleButton.getHeight()-img.getHeight())/2-img.getHeight();//-(img.getWidth()/2)/3;
                float x = circleButton.getX();
                float y = circleButton.getY();
                //隐藏文字
                findViewById(R.id.mainTip).setVisibility(View.GONE);
                //变小
                ObjectAnimator toSmallX = ObjectAnimator.ofFloat(circleButton, "scaleX", 1.0f, 0.33f);
                ObjectAnimator toSmallY = ObjectAnimator.ofFloat(circleButton, "scaleY", 1.0f, 0.33f);
                //通过下移达到类似以最低点不变缩小的效果
                final ObjectAnimator toEndY = ObjectAnimator.ofFloat(circleButton, "y", y, y + circleButton.getHeight() - centerImg.getHeight() * 2);
                //向上移动
                ObjectAnimator toTopX = ObjectAnimator.ofFloat(circleButton, "x", x, targetX);
                ObjectAnimator toTopY = ObjectAnimator.ofFloat(circleButton, "y", y + circleButton.getHeight() - centerImg.getHeight() * 2, targetY);
                //旋转
                ObjectAnimator radio = ObjectAnimator.ofFloat(circleButton, "rotation", 0.0f, 45.0f);
                AnimatorSet animSet = new AnimatorSet();
                animSet.setDuration(600);
                animSet.setInterpolator(new DecelerateInterpolator());
                //两个动画同时执行
                animSet.play(toSmallX).with(toSmallY).with(toEndY);
                animSet.play(toTopX).with(toTopY).with(radio).after(toSmallX);
//                animSet.playTogether(toSmallX,toSmallY,toTopX,toTopY,radio);
                animSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        circleButton.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        circleButton.setVisibility(View.GONE);
                        //wifi图标显示
                        centerImg.setVisibility(View.VISIBLE);
                        ObjectAnimator.ofFloat(centerImg, "alpha", 0.0f, 1.0f).setDuration(300).start();

                        lendToSdnu();

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animSet.start();
            }
        });

        ButtonRectangle btRefresh = (ButtonRectangle) findViewById(R.id.refresh);
        ButtonRectangle btRefreshWifiStatus = (ButtonRectangle) findViewById(R.id.refreshWiFiStatus);
        ButtonRectangle btLendToSDNU = (ButtonRectangle) findViewById(R.id.lendToSDNU);

        assert btLendToSDNU != null;
        btLendToSDNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (WIFIUtils.getWifiName() == null) {
                if (WIFIUtils.getWifiName() != "sdnu") {
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

    private void showStatement(){
        SharedPreferences sharedPreferences = getSharedPreferences("statement",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isAgree = sharedPreferences.getBoolean("isAgree",false);
        if (!isAgree){

            final AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(getResources().getString(R.string.meau_statement));
            aboutBuilder.setMessage(getResources().getString(R.string.description_statement));
            aboutBuilder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("isAgree",true);
                    editor.commit();
                    dialog.dismiss();
                }
            });
            aboutBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ConnectActivity.this.finish();
                }
            });
            aboutBuilder.create().show();
        }else {
            return;
        }
    }
    private void lendToSdnu() {
        View wifiStateLayout = findViewById(R.id.wifiStateLayout);
        View passwordStateLayout = findViewById(R.id.passwordStateLayout);
        View connectStateLayout = findViewById(R.id.connectStateLayout);
        TextView wifiState = (TextView) findViewById(R.id.wifiState);
        TextView passwordState = (TextView) findViewById(R.id.passwordState);
        TextView connectState = (TextView) findViewById(R.id.connectState);
        ImageView wifiStateImg = (ImageView) findViewById(R.id.wifiStateImg);
        ImageView passwordStateImg = (ImageView) findViewById(R.id.passwordStateImg);


        playGif(RIPP_CONNECTING);

        if (!WIFIUtils.getWifiName().trim().equals("sdnu")) {
            Toast.makeText(ConnectActivity.this, "请先连接SDNU", Toast.LENGTH_SHORT).show();
            wifiState.setText("未连接SDNU");
            wifiStateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_fail));
            wifiStateImg.setVisibility(View.VISIBLE);
            animatorUtils.addAnimator(wifiStateLayout);
            animatorUtils.start();
            playGif(RIPP_FAIL);
            return;
        } else {
            wifiStateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_success));
            wifiState.setText("已连接SDNU");
            animatorUtils.addAnimator(wifiStateLayout);
        }

        if (SDNUUtils.getSDNU() == null ||
                SDNUUtils.getSDNU().get("strAccount") == null || SDNUUtils.getSDNU().get("strAccount").equals("") ||
                SDNUUtils.getSDNU().get("strPassword") == null || SDNUUtils.getSDNU().get("strPassword").equals("")) {

            passwordState.setText("未设置用户名或密码");
            passwordStateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_fail));
            passwordStateImg.setVisibility(View.VISIBLE);
            animatorUtils.addAnimator(passwordStateLayout);
            Toast.makeText(ConnectActivity.this, "请设置用户名与密码!", Toast.LENGTH_SHORT).show();
            animatorUtils.start();
            playGif(RIPP_FAIL);
            showDialog();
            return;
        } else {
            passwordStateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_success));
            ((TextView) findViewById(R.id.passwordState)).setText("用户名:" + SDNUUtils.getSDNU().get("strAccount"));
            animatorUtils.addAnimator(passwordStateLayout);
        }
        Stack<String> urls = new Stack<>();
        urls.add(Config.URL);
        urls.add(Config.URL_OLD);
//        connectToNetPresenter.connect(SDNUUtils.getSDNU(), Config.URL);
        connectToNetPresenter.connect(SDNUUtils.getSDNU(), urls);

    }

    private void playGif(int flag) {
        switch (flag) {
            case RIPP_CONNECTING:
                findViewById(R.id.retryHint).setVisibility(View.INVISIBLE);
                findViewById(R.id.connectStateLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.wifiStateLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.passwordStateLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.netStateLayout).setVisibility(View.INVISIBLE);
                rippleBackground.startRippleAnimation();
                centerImg.setColor(getResources().getColor(R.color.RippConnecting));
                centerImg.setEnabled(false);
                break;
            case RIPP_FAIL:
                findViewById(R.id.retryHint).setVisibility(View.VISIBLE);
                rippleBackground.stopRippleAnimation();
                centerImg.setColor(getResources().getColor(R.color.RippFail));
                centerImg.setImageDrawable(getResources().getDrawable(R.mipmap.ripp_fail));
                centerImg.setEnabled(true);
                break;
            case RIPP_SUCCESS:
                rippleBackground.stopRippleAnimation();
                centerImg.setColor(getResources().getColor(R.color.RippSuccess));
                centerImg.setImageDrawable(getResources().getDrawable(R.mipmap.ripp_success));
                centerImg.setEnabled(true);
                break;
            default:
                break;
        }
    }

    private void saveCache(JSONObject jsonObject) {
        SharedPreferences.Editor editor = getSharedPreferences(CACHE_NAME, MODE_PRIVATE).edit();
        SharedPreferences sharedPreferences = getSharedPreferences("wifiData", MODE_PRIVATE);
        int length = sharedPreferences.getInt(CACHE_LENGTH_KEY, -1) + 1;
        editor.putInt(CACHE_LENGTH_KEY, length);
        editor.putString(jsonObject.toString(), "");
        editor.commit();
    }


    private JSONArray getCache() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        SharedPreferences sharedPreferences = getSharedPreferences(CACHE_NAME,MODE_PRIVATE);
        int length = sharedPreferences.getInt(CACHE_LENGTH_KEY,0);
        for (int i = 0;i<length;i++){
            String str = sharedPreferences.getString(""+i,"");
            JSONObject jsonObject = new JSONObject(str);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


    //    将dp转换为px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void accountStatus() {
        tvWifiName.setText(WIFIUtils.getWifiName() == null ?
                "" : WIFIUtils.getWifiName());
        tvUserName.setText(WIFIUtils.getWifiAccountName() == null ?
                "" : WIFIUtils.getWifiAccountName());
    }

    @Override
    public void success() {
        ((TextView) findViewById(R.id.connectState)).setText("登陆成功");
        ImageView connectSatateImg = (ImageView) findViewById(R.id.connectStateImg);
        connectSatateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_success));
        connectSatateImg.setVisibility(View.VISIBLE);
        tvSDNUMessage.setText("连接成功~");
        isConnectedNet(true);
        tvSDNUMessage.setTextColor(Color.BLUE);
    }

    @Override
    public void trying() {
        playGif(RIPP_CONNECTING);
        centerImg.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
        ((TextView) findViewById(R.id.connectState)).setText("登陆中...");
        findViewById(R.id.connectStateImg).setVisibility(View.INVISIBLE);
        animatorUtils.addAnimator(findViewById(R.id.connectStateLayout));
        animatorUtils.start();
        tvSDNUMessage.setText("连接中~");
        tvSDNUMessage.setTextColor(Color.GREEN);
    }

    @Override
    public void failed(String message) {
        if (message.contains("超时")) {
            try {
                JSONObject jsonObject = WIFIUtils.getWifiInfo();
                saveCache(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ((TextView) findViewById(R.id.connectState)).setText("登陆失败:" + message);
        ImageView connectSatateImg = (ImageView) findViewById(R.id.connectStateImg);
        connectSatateImg.setImageDrawable(getResources().getDrawable(R.mipmap.hint_fail));
        connectSatateImg.setVisibility(View.VISIBLE);
        playGif(RIPP_FAIL);
        tvSDNUMessage.setText("连接失败~");
        isConnectedNet(false);
        tvSDNUMessage.setTextColor(Color.RED);
    }

    @Override
    public void isConnectedNet(boolean is) {
        if (is) {
            playGif(RIPP_SUCCESS);
            ((TextView) findViewById(R.id.netState)).setText("网络畅通");
            animatorUtils.addAnimator(findViewById(R.id.netStateLayout));
            animatorUtils.start();


            tvNetStatus.setText("网络畅通~");
            tvNetStatus.setTextColor(Color.BLUE);
        } else {
            ((TextView) findViewById(R.id.netState)).setText("网络断开");
            animatorUtils.addAnimator(findViewById(R.id.netStateLayout));
            tvNetStatus.setText("网络断开~");
            tvNetStatus.setTextColor(Color.RED);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            System.out.println("" + msg.obj);
            switch (msg.what){
                case 1:
                    String result = (String) msg.obj;
                    if (result.contains("true")){
                        SharedPreferences.Editor editor = getSharedPreferences(CACHE_NAME,MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        return true;
                    }else if (result.contains("error")){
                        if (toPostWifiInfo != null){
                            saveCache(toPostWifiInfo);
                            toPostWifiInfo = null;
                        }
                    }
                    break;
            }

            return false;
        }
    });




    @Override
    public void isCheckingNet() {
        tvNetStatus.setText("正在判断~");
    }

    @Override
    public void update(String versionName, String versionDescription) {
        String[] descriptions = versionDescription.split("-");
        String message = "最新版:" + versionName;
        for (String d : descriptions) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showDialog() {
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

//            case R.id.checkNew:
//                requestMyApplicationPermission();
//                break;

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
                showDialog();
                break;
            default:
        }
        return true;
    }


}
