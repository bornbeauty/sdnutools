package com.jimbo.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jimbo.myapplication.adapter.ChatListviewAdapter;
import com.jimbo.myapplication.utils.HttpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/9/16.
 */
public class RootChatActivity extends Activity {

    private ListView mListView;
    private ChatListviewAdapter mAdapter;
    private List<HttpResponesMessage> mList = new ArrayList<>();

    private Button mSend;
    private EditText mEditText;

    private String ask = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_chat);

        initView();
        initDate();
        initListener();
    }

    private void initListener() {

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAsk = mEditText.getText().toString();
                if (userAsk == null) {
                    return;
                }
                ask = userAsk;
                getAnswer();
                HttpResponesMessage message = new HttpResponesMessage();
                message.code = "100000";
                message.date = new Date();
                message.text = ask;
                message.type = HttpResponesMessage.Type.ASK;
                mList.add(message);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mList.size());

                mEditText.setText("");
            }
        });

    }

    private void getAnswer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtils.doGet(ask);
                Message message = new Message();
                message.obj = result;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private void initDate() {

        HttpResponesMessage message = new HttpResponesMessage();
        message.date = new Date();
        message.text = "你好,欢迎来到here";
        message.type = HttpResponesMessage.Type.SEND;
        message.code = "100000";
        mList.add(message);

        mAdapter = new ChatListviewAdapter(this, mList);
        mListView.setAdapter(mAdapter);

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.chat_listview);
        mAdapter = new ChatListviewAdapter(RootChatActivity.this, mList);
        mSend = (Button) findViewById(R.id.chat_button_send);
        mEditText = (EditText) findViewById(R.id.chat_edittext_message);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            HttpResponesMessage entity = new HttpResponesMessage();
            entity.type = HttpResponesMessage.Type.SEND;
            String httpRespones = (String) msg.obj;
            try {
                entity.httpStauts = 1;
                Gson gson = new Gson();
                entity = gson.fromJson(httpRespones, new TypeToken<HttpResponesMessage>() {
                }.getType());
                entity.type = HttpResponesMessage.Type.SEND;
                mList.add(entity);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mList.size());
            } catch (Exception e) {
                entity.httpStauts = 0;
                e.printStackTrace();
            }
            return true;
        }
    });
}
