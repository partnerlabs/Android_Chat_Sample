package so.partner.partnerchatsample.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.bean.ChatMessage;

public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class.getSimpleName();

    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    public static String preTag = null;

    @Override
    protected void onMessage(Context context, String name, String topic, byte[] payload) {
        try {
            JSONObject jsonObject = new JSONObject(new String(payload));
            if (jsonObject != null) {
                String sId = jsonObject.getString("sId");

                if (!ChatManager.getClientId().equals(sId)) {
                    if (ChatManager.CONNECTION_NAME.equals(name)) {
                        String text = jsonObject.getString("text");
                        long date = jsonObject.getLong("date");

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.userId = sId;
                        chatMessage.content = text;
                        chatMessage.date = date;

                        Intent intent = new Intent(MqttReceiver.ACTION_MESSAGE_ARRIVED);
                        intent.addCategory(context.getPackageName());
                        intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
