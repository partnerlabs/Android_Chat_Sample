package so.partner.partnerchatsample.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import so.partner.lib.android.partnerpush.PartnerPushReceiver;
import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.R;
import so.partner.partnerchatsample.adapter.ChatAdapter;
import so.partner.partnerchatsample.bean.ChatMessage;
import so.partner.partnerchatsample.receiver.PartnerPushBroadcastReceiver;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtMessage;
    private ChatAdapter mChatAdapter;

    private List<ChatMessage> mTalkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityManager am = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            String topActivityName;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //For above Kitkat version
                List<ActivityManager.RunningAppProcessInfo> tasks = am
                        .getRunningAppProcesses();
                if (tasks.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    topActivityName = tasks.get(0).processName;
                    System.out.println("topActivityName1 : " + topActivityName);
                }
            } else {
                //noinspection deprecation
                topActivityName = am.getRunningTasks(1).get(0).topActivity
                        .getClassName();
                System.out.println("topActivityName2 : " + topActivityName);
            }
        }

        IntentFilter filter = new IntentFilter(PartnerPushReceiver.ACTION_MESSAGE_ARRIVED);
        filter.addCategory(getPackageName());
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        setContentView(R.layout.a_chat);

        ListView mLvTalk = (ListView) findViewById(R.id.a_chat_lv_talk);
        mEtMessage = (EditText) findViewById(R.id.a_chat_et_message);

        mChatAdapter = new ChatAdapter(this, mTalkList);

        mLvTalk.setAdapter(mChatAdapter);


        findViewById(R.id.a_chat_tv_subscribe).setOnClickListener(this);
        findViewById(R.id.a_chat_tv_unsubscribe).setOnClickListener(this);
        findViewById(R.id.a_chat_btn_send).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        ChatManager.disconnect();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_chat_tv_subscribe: {
                ChatManager.subscribe();
                break;
            }
            case R.id.a_chat_tv_unsubscribe: {
                ChatManager.unsubscribe();
                break;
            }
            case R.id.a_chat_btn_send: {
                send();
                break;
            }
        }
    }

    private void send() {
        String message = mEtMessage.getText().toString();
        if (message.length() == 0) {
            return;
        }
        ChatManager.publish(message);

        ChatMessage item = new ChatMessage();
        item.isMine = true;
        item.nickname = "";
        item.text = message;
        item.date = System.currentTimeMillis();

        mChatAdapter.add(item);
        mChatAdapter.notifyDataSetChanged();
        mEtMessage.setText("");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (PartnerPushReceiver.ACTION_MESSAGE_ARRIVED.equals(action)) {
                ChatMessage item = (ChatMessage) intent.getSerializableExtra(PartnerPushBroadcastReceiver.EXTRA_CHAT_MESSAGE);
                if (item != null) {
                    mChatAdapter.add(item);
                    mChatAdapter.notifyDataSetChanged();
                }
            }
        }
    };
}