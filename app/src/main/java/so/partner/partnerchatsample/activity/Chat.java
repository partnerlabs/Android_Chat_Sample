package so.partner.partnerchatsample.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.MqttBroadcastReceiver;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean mIsAbleBack;

    @Override
    public void onBackPressed() {
        if (!mIsAbleBack) {
            mIsAbleBack = true;
            Toast.makeText(this, "한번 더 누르시면 종료됩니다", Toast.LENGTH_LONG).show();
            new CountDownTimer(2000, 1) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    mIsAbleBack = false;
                }
            }.start();
        } else {
            ChatManager.logout();
            super.onBackPressed();
        }
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
        ChatMessage item = new ChatMessage();
        item.userId = ChatManager.getClientId();
        item.content = message;
        item.date = System.currentTimeMillis();

        mChatAdapter.add(item);
        mChatAdapter.notifyDataSetChanged();
        mEtMessage.setText("");
        ChatManager.publish(message);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(MqttBroadcastReceiver.EXTRA_CHAT_MESSAGE);
            mChatAdapter.add(chatMessage);
            mChatAdapter.notifyDataSetChanged();
        }
    };
}