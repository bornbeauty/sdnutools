package com.jimbo.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/9/23.
 */
public class GradeActivity extends Activity {
    TextView textView = null;

    private String html = "";

    private String text = "";




    private String content = "";

    List<Course> courses = new ArrayList<>();

    List<Course> xianCourser = new ArrayList<>();
    List<Course> xuanCourser = new ArrayList<>();

    HttpClient httpClient = new DefaultHttpClient();

    private String name = "";
    private String password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        textView = (TextView) findViewById(R.id.text);

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BEST_MATCH);

        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");

        if (null == name || null == password) {
            Toast.makeText(GradeActivity.this, "输入错误", Toast.LENGTH_SHORT).show();
            return;
        }

        textView.setText("加载中......");

        if (name.equals("201311010345") || name.equals("201311010120")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("友情提示");
            builder.setMessage("oh,撒子你好");
            builder.setPositiveButton("Yes,I am fool", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    builder.setMessage("真棒!");
                    builder.setTitle("友情提示");
                    builder.setPositiveButton("yeah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
            builder.create().show();
        }



        login();

    }

    private void login() {
        new Thread(new Runnable() {
            Message message = new Message();

            @Override
            public void run() {
                try {
                    HttpPost post = new HttpPost(Config.JWC_LOGIN_URL);

                    List<NameValuePair> nvps = new ArrayList<>();
                    nvps.add(new BasicNameValuePair("stuid", name));
                    nvps.add(new BasicNameValuePair("pwd", password));
                    post.setEntity(new UrlEncodedFormEntity(nvps));

                    HttpResponse respones = httpClient.execute(post);
                    if (HttpStatus.SC_OK == respones.getStatusLine().getStatusCode()) {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 1001;
                    } else {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 1000;
                    }
                } catch (Exception e) {
                    message.obj = "异常-" + e.toString();
                    e.printStackTrace();
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void getGrade() {
        new Thread(new Runnable() {
            Message message = new Message();

            @Override
            public void run() {
                try {
                    HttpGet get = new HttpGet(Config.JWC_GRADE_ALL);
                    HttpResponse respones = httpClient.execute(get);
                    if (HttpStatus.SC_OK == respones.getStatusLine().getStatusCode()) {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 1;
                    } else {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 0;
                    }
                } catch (Exception e) {
                    message.obj = "异常-" + e.toString();
                    e.printStackTrace();
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void getBenGrade() {
        new Thread(new Runnable() {
            Message message = new Message();

            @Override
            public void run() {
                try {
                    HttpGet get = new HttpGet(Config.JWC_GRADE_BEN);
                    HttpResponse respones = httpClient.execute(get);
                    if (HttpStatus.SC_OK == respones.getStatusLine().getStatusCode()) {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 2001;
                    } else {
                        HttpEntity entity = respones.getEntity();
                        message.obj = EntityUtils.toString(entity);
                        message.what = 2000;
                    }
                } catch (Exception e) {
                    message.obj = "异常-" + e.toString();
                    e.printStackTrace();
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {


            if (1001 == msg.what) {
                content += msg.obj.toString();
                getGrade();
                textView.setText(msg.obj.toString());
                return true;
            } else if (1000 == msg.what) {
                textView.setText("登入失败 请检查账号密码.");
                return true;
            }

            if (2001 == msg.what) {
                String html = (String) msg.obj;
                try {
                    System.out.println(html);
                    parseBenGrade(html);
                } catch (Exception e) {
                    e.printStackTrace();
                    textView.setText("账号密码错误,请重试..." + msg.obj.toString());
                }
                //textView.setText(msg.obj.toString());
                calc();
                return true;
            } else if (2000 == msg.what) {
                textView.setText("登入失败 请检查账号密码.");
                return true;
            }

            if (1 == msg.what) {
                getBenGrade();
                System.out.println(msg.obj);
                html = msg.obj.toString();
                content += html;
                try {
                    parseHtmlToGrade(html);
                } catch (Exception e) {
                    textView.setText("出现错误，请重试");
                }
                return true;
            }
            return true;
        }


    });

    private void parseBenGrade(String html) throws Exception{
        Document doc = Jsoup.parse(html);
        Elements e = doc.getElementsByAttribute("bgcolor");
        String cc = "";
        try {
            Elements trs = e.get(0).getElementsByTag("TR");
            int flag = 0;
            for (Element tr : trs) {
                if (2 >= flag) {
                    flag++;
                    continue;
                }

                try {
                    Course course = new Course();
                    course.name = tr.getElementsByTag("td").get(2).text();
                    course.grade = tr.getElementsByTag("td").get(6).text();
                    course.weight = tr.getElementsByTag("td").get(4).text();
                    String c = tr.getElementsByTag("td").get(7).text();
                    if (c.contains("必修")) {
                        if (!courses.contains(course)) {
                            courses.add(course);
                        }
                    } else if (c.contains("限选")) {
                        if (!xianCourser.contains(course)) {
                            xianCourser.add(course);
                        }
                    } else {
                        if (!xuanCourser.contains(course)) {
                            xuanCourser.add(course);
                        }
                    }
                    cc += c;

                } catch (Exception ee) {
                }
                    textView.setText(cc);
            }
        } catch (Exception ee) {
        }

    }

    private void parseHtmlToGrade(String html) throws Exception{
        Document doc = null;

        Elements e = null;
        try {
            doc = Jsoup.parse(html);
            e = doc.getElementsByTag("TABLE").get(1).getElementsByTag("table");
        } catch (Exception eq) {
            eq.printStackTrace();
            textView.setText("登入失败,请检查您的账号密码或者待会重试.");
            return;
        }
        int flag = 0;

        Elements trs = e.get(0).getElementsByTag("TR");
        int n = 0;
        for (Element tr : trs) {
            if (2 >= flag) {
                flag++;
                continue;
            }
            try {
                Course course = new Course();
                if (tr.text().contains("课程号")) {
                    n++;
                }
                course.name = tr.getElementsByTag("td").get(1).text();
                course.grade = tr.getElementsByTag("td").get(5).text();
                course.weight = tr.getElementsByTag("td").get(3).text();
                if (0 == n) {
                    courses.add(course);
                } else if (1 == n) {
                    xianCourser.add(course);
                } else {
                    xuanCourser.add(course);
                }
            } catch (Exception ex) {
            }
        }
    }

    private void calc() {
        //计算成绩
        int xuefenSum = 0;
        float chengjiSum = 0;
        for (Course c : courses) {
            try {
                if (c.name.contains("军事教育") ||
                        c.name.contains("大学体育") ||
                        c.name.contains("劳动") ||
                        c.name.contains("形势与政策")) {
                    //if (c.name.contains("军事教育")) {

                } else {
                    Integer.parseInt(c.grade);
                    int xue = Integer.parseInt(c.weight);
                    xuefenSum += xue;
                    chengjiSum += getXF(c)*xue;
                    c.isSum = true;
                }
            } catch (Exception exx) {
                exx.printStackTrace();
            }
        }

        for (Course c : xianCourser) {
            try {
                Integer.parseInt(c.grade);
                int xue = Integer.parseInt(c.weight);
                xuefenSum += xue;
                chengjiSum += getXF(c)*xue;
                c.isSum = true;
            } catch (Exception exxx) {
                exxx.printStackTrace();
            }
        }

        float pj = chengjiSum / xuefenSum;

        text += name + "同学,你共选修了" + courses.size() + "门必修课和"
                + xianCourser.size() + "门限选课\n";

        text += "绩点和为:" + chengjiSum + " 学分和为" + xuefenSum + "\n";
        text += "平均绩点为:" + pj + " (下面带\"*\"的表示参与计算平均绩点的课程)\n\n";


        text += "必修课(共" + courses.size() + "门):\n";
        for (Course c : courses) {
            text += c.toString();
        }
        text += "限选课(共" + xianCourser.size() + "门):\n";
        if (0 == xianCourser.size()) {
            text += "您暂时没有限选课成绩.\n";
        } else {
            for (Course c : xianCourser) {
                text += c.toString();
            }
        }
        text += "公选课(共" + xuanCourser.size() + "门):\n";
        if (0 == xuanCourser.size()) {
            text += "您暂时没有公选课成绩.\n";
        } else {
            for (Course c : xuanCourser) {
                text += c.toString();
            }
        }
        textView.setText(text);
    }


    private float getXF(Course c) {

        try {
            int g = Integer.parseInt(c.grade);
            if (100 == g) {
                return 5;
            } else if (g >= 95) {
                return 4.5f;
            } else if (g >= 90) {
                return 4.0f;
            } else if (g >= 85) {
                return 3.5f;
            } else if (g >= 80) {
                return 3.0f;
            } else if (g >= 75) {
                return 2.5f;
            } else if (g >= 70) {
                return 2.0f;
            } else if (g >= 65) {
                return 1.5f;
            } else if (g >= 60) {
                return 1.0f;
            } else {
                return 0;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return 0;
    }
}


class Course {
    public String name;
    public String weight;
    public String grade;
    public boolean isSum;

    public Course() {
        isSum = false;
    }

    @Override
    public String toString() {
        String text = "课程名:" + name
                + "  学分:" + weight
                + "  成绩:" + grade;
        if (isSum) {
            text += " *";
        }
        text += "\n";
        return text;
    }

    @Override
    public boolean equals(Object o) {
        Course course = (Course) o;
        if (course.name.equals(this.name)) {
            return true;
        } else {
            return false;
        }
    }
}


