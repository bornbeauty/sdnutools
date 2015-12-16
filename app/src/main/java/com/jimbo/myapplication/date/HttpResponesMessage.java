package com.jimbo.myapplication.date;

import java.util.List;

/**
 * 图灵机器人返回的数据
 * Created by Administrator on 2015/9/16.
 */
public class HttpResponesMessage {

    /**
     * 文字类
     * {
     * "code":100000,
     * "text":"恩恩，害羞ing……"
     * }
     * 连接类
     * {
     * "code":200000,
     * "text":"亲，已帮你打开图灵",
     * "url":"http://www.tuling123.com/openapi/"
     * }
     * 新闻类
     * {"code":302000,
        "text":"********",
        "list":[{
        "article":"",
        "source":"",
        "detailurl":"",
        "icon":""
        }]
        }
        列车类
        {
            "code":305000,
            "text":"********",
            "list":[{
            "trainnum":"",
            "start":""
            "terminal":"",
            "starttime":""
            "endtime":""
            "detailurl":""
            "icon":""
        }]
      菜谱类
        {
            "code":308000,
            "text":"********",
            "list":[{
            "name":"",
            "info":"",
            "detailurl":""
            "icon":""
            }]
        }
     */

    public String code;
    public String text;
    public String url;
    public List<ListItem> list;
    public int httpStauts;
    public Type type;
    public java.util.Date date;

    public enum Type {
        SEND, ASK
    }

}
