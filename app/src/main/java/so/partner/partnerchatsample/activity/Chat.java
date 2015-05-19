package so.partner.partnerchatsample.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.receiver.MqttBroadcastReceiver;
import so.partner.partnerchatsample.R;
import so.partner.partnerchatsample.adapter.ChatAdapter;
import so.partner.partnerchatsample.bean.ChatMessage;

public class Chat extends AppCompatActivity implements View.OnClickListener {

    private ListView mLvTalk;
    private EditText mEtMessage;
    private ChatAdapter mChatAdapter;

    private List<ChatMessage> mTalkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(MqttBroadcastReceiver.ACTION_MESSAGE_ARRIVED);
        filter.addCategory(getPackageName());
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

        setContentView(R.layout.a_chat);

        mLvTalk = (ListView) findViewById(R.id.a_chat_lv_talk);
        mEtMessage = (EditText) findViewById(R.id.a_chat_et_message);

        mChatAdapter = new ChatAdapter(this, mTalkList);

        mLvTalk.setAdapter(mChatAdapter);

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
            case R.id.a_chat_btn_send: {
                send();
                break;
            }
        }
    }

    private void send() {
        String message = mEtMessage.getText().toString();
        if (message == null || message.length() == 0) {
            return;
        }
        ChatManager.publish(message);

        ChatMessage item = new ChatMessage();
        item.userId = ChatManager.getClientId();
        item.content = message;
        item.date = System.currentTimeMillis();

        mChatAdapter.add(item);
        mChatAdapter.notifyDataSetChanged();
        mEtMessage.setText("");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (MqttReceiver.ACTION_MESSAGE_ARRIVED.equals(action)) {
                ChatMessage item = (ChatMessage) intent.getSerializableExtra(MqttBroadcastReceiver.EXTRA_CHAT_MESSAGE);
                if (item != null) {
                    mChatAdapter.add(item);
                    mChatAdapter.notifyDataSetChanged();
                }
            }
        }
    };
}