package com.jimbo.myapplication.date;

/**
 * Created by Administrator on 2015/9/16.
 */

public class ListItem {
    public String article;
    public String source;
    public String icon;
    public String trainnum;
    public String start;
    public String terminal;
    public String starttime;
    public String endtime;
    public String detailurl;
    public String name;
    public String info;


    @Override
    public String toString() {
        return "-"+article+source+icon+trainnum
                +start+terminal+starttime+endtime+detailurl+"-";
    }
}

