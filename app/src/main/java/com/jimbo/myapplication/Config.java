package com.jimbo.myapplication;

/**
 *
 * Created by Administrator on 2015/9/2.
 */
public class Config {

    public static final String WIFI_NAME = "sdnu";
    public static final String WIFI_STATUS_RECORD = "wifi_status";
    public static final String SDNU_USERNAME = "SDNU_USERNAME";
    public static final String SDNU_PASSWORD = "SDNU_PASSWORD";
    public static final String URL = "http://192.168.255.195:8080/Control";


    public static final String SDNU_JWC_STUID = "SDNU_JWC_STUID";
    public static final String SDNU_JWC_PSW = "SDNU_JWC_PSW";

    public static final int SUCCESSTOLEADSDNU = 1;
    public static final int FAILTOLEADSDNU = 0;
    public static final int OUTTIMETOLEADSDNU = -1;

    public static final String JWC_LOGIN_URL = "" +
            "http://210.44.2.167:7777/pls/wwwbks/bks_login2.login";

    public static final String JWC_GRADE_BEN = "" +
            "http://210.44.2.167:7777/pls/wwwbks/bkscjcx.curscopre?jym2005=11051.764756334376";

    public static final String JWC_GRADE_ALL = "http://210.44.2.167:7777/pls/" +
            "wwwbks/bkscjcx.yxkc?jym2005=11487.321590731215";
}
