package com.jimbo.myapplication.adapter;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jimbo.myapplication.R;
import com.jimbo.myapplication.date.HttpResponesMessage;
import com.jimbo.myapplication.date.ListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/9/16.
 */
public class ChatListviewAdapter extends BaseAdapter {

    private List<HttpResponesMessage> list = new ArrayList<>();
    private LayoutInflater inflater;

    public ChatListviewAdapter(Context context, List<HttpResponesMessage> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        HttpResponesMessage.Type type = list.get(position).type;
        if (type == type.SEND) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HttpResponesMessage message = list.get(position);
        HolderView holderView;

        System.out.println("--start");
        
        if (null == convertView) {
            System.out.println("--1");
            holderView = new HolderView();
            if (HttpResponesMessage.Type.SEND == message.type) {
                System.out.println("--2");
                convertView = inflater.inflate(R.layout.listview_item_from, null);
                holderView.content = (TextView) convertView.findViewById(R.id.listview_item_from_text);
                holderView.time = (TextView) convertView.findViewById(R.id.chat_from_date);
                System.out.println("--3");
            } else {
                System.out.println("--4");
                try {
                    convertView = inflater.inflate(R.layout.listview_item_send, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("-----");
                holderView.content = (TextView) convertView.findViewById(R.id.listview_item_send_text);
                holderView.time = (TextView) convertView.findViewById(R.id.chat_send_date);
                System.out.println("--5");
            }
            convertView.setTag(holderView);
            System.out.println("--6");
        } else {
            holderView = (HolderView) convertView.getTag();
            System.out.println("--7");
        }
        System.out.println("--8");
        SimpleDateFormat format = null;
        try {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holderView.time.setText(format.format(message.date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            setContent(message, holderView.content);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("jiushita ");
        }
        System.out.println("--9");
        return convertView;
    }

    private class HolderView {
        TextView content;
        TextView time;
    }

    private void setContent(HttpResponesMessage message, TextView textView) {

        String content = message.text;

        switch (message.code) {
            case "100000":
                break;
            case "200000":
                content = " 详细点击查看: " + message.url;
                textView.setAutoLinkMask(Linkify.ALL);
                break;
            case "302000":
                List<ListItem> news = message.list;
                for (int i = 0; i < news.size(); i++) {
                    ListItem item = news.get(i);
                    content += " 标题: " + item.article +
                            " 来源: " + item.source + " 点击查看:"
                            + item.detailurl + "\n";
                }
                textView.setAutoLinkMask(Linkify.ALL);
                break;
            case "305000":
                List<ListItem> cars = message.list;
                for (int i = 0; i < cars.size(); i++) {
                    ListItem item = cars.get(i);
                    content += " 车次 " + item.trainnum + " 发车时间 "
                            + item.starttime + " 到站时间 " + item.endtime +
                            " 起始站 " + item.start + " 终点站 " + item.terminal
                            + " 详细地址 " + item.detailurl + "\n";
                }
                textView.setAutoLinkMask(Linkify.ALL);
                break;
            case "308000":
                List<ListItem> foods = message.list;
                for (int i = 0; i < foods.size(); i++) {
                    ListItem item = foods.get(i);
                    content += " 名字: " + item.name + " 详细: " + item.info
                    + " 详细地址 " + item.detailurl + "\n";
                }
                textView.setAutoLinkMask(Linkify.ALL);
                break;
            default:
        }
        System.out.println(content+"----");
        textView.setText(content);
    }
}
