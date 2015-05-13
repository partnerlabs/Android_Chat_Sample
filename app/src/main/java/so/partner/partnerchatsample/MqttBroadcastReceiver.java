package so.partner.partnerchatsample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.bean.ChatMessage;

/**
 * Created by 120801RF3 on 2015-05-11.
 */
public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class
            .getSimpleName();

    public static final String ACTION_MESSAGE_ARRIVED = "MESSAGE_ARRIVED";
    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    public static String preTag = null;

    @Override
    protected void onMessage(Context context, String name, String sid, String topic,
                             byte[] payload) {
        if (ChatManager.CONNECTION_NAME.equals(name)) {
            try {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.id = name;
                chatMessage.userId = sid;
                chatMessage.content = new String(payload);

                Intent intent = new Intent(ACTION_MESSAGE_ARRIVED);
                intent.addCategory(context.getPackageName());
                intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
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
