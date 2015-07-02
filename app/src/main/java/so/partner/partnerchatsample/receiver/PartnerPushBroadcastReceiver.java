package so.partner.partnerchatsample.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import so.partner.lib.android.partnerpush.PPMessage;
import so.partner.lib.android.partnerpush.PartnerPushReceiver;
import so.partner.partnerchatsample.bean.ChatMessage;

public class PartnerPushBroadcastReceiver extends PartnerPushReceiver {

    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    @Override
    protected void onPPReceived(Context context, String topic, PPMessage message) {
        if (message.isMine()) {
            return;
        }
        ChatMessage chatMessage = null;
        Log.d("RECEIVE", "message.getMsgType() = " + message.getMsgType());
        if (message.getMsgType() != PPMessage.BINARY) {
            JSONObject json = new JSONObject(message.getContent());
            try {
                chatMessage = new Gson().fromJson((String) json.get("text"), ChatMessage.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            chatMessage = new Gson().fromJson(new String(message.getBinary()), ChatMessage
                    .class);
        }
        if (chatMessage != null) {
            chatMessage.isMine = message.isMine();
            chatMessage.date = message.getSendDate();

            Intent intent = new Intent(PartnerPushReceiver.ACTION_MESSAGE_ARRIVED);
            intent.addCategory(context.getPackageName());
            intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    @Override
    protected void onPPFailed(Context context, String errorCode) {
        Toast.makeText(context, "Fail to connect: " + errorCode, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPPConnected(Context context) {
        Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
    }
}
