package so.partner.partnerchatsample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.bean.ChatMessage;
import so.partner.partnerchatsample.bean.PushedChatMessage;

/**
 * Created by 120801RF3 on 2015-05-11.
 */
public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class.getSimpleName();

    public static final String ACTION_MESSAGE_ARRIVED = "MESSAGE_ARRIVED";
    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    public static String preTag = null;

    @Override
    protected void onMessage(Context context, String name, String topic, byte[] payload) {
        PushedChatMessage message = new Gson().fromJson(new String(payload), PushedChatMessage.class);

        if (message != null) {
            if (!ChatManager.getClientId().equals(message.sId)) {
                if (ChatManager.CONNECTION_NAME.equals(name)) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.userId = message.sId;
                    chatMessage.content = message.text;
                    chatMessage.date = message.date;

                    Intent intent = new Intent(ACTION_MESSAGE_ARRIVED);
                    intent.addCategory(context.getPackageName());
                    intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }
    }

    @Override
    protected void onError(Context context, String name, String errorCode) {
    }

    @Override
    protected void onConnected(Context context, String name) {
    }

    @Override
    protected void connectionLost(Context context, String name) {
    }
}
