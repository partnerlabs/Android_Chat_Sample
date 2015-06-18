package so.partner.partnerchatsample.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import so.partner.lib.android.mqtt.MqttPayload;
import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.MyApplication;
import so.partner.partnerchatsample.bean.ChatMessage;

public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class.getSimpleName();

    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    public static String preTag = null;

    private ActivityManager am = (ActivityManager) MyApplication.getInstance().getSystemService(Context
            .ACTIVITY_SERVICE);
    private List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);

    @Override
    protected void onMessage(Context context, String name, String topic, MqttPayload payload) {
        if (payload.isMine()) {
            return;
        }
        try {
            JSONObject jsonObject;
            if (payload.getTextType() != MqttPayload.BINARY) {
                jsonObject = new JSONObject((String) payload.getMessage());
            } else {
                jsonObject = new JSONObject(new String((byte[]) payload.getMessage()));
            }
            if (jsonObject != null) {
                String sId = jsonObject.getString("sId");

                if (ChatManager.APP_ID.equals(name)) {
                    String text = jsonObject.getString("text");

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.userId = sId;
                    chatMessage.content = text;
                    chatMessage.date = payload.getSendDate();

                    Intent intent = new Intent(MqttReceiver.ACTION_MESSAGE_ARRIVED);
                    intent.addCategory(context.getPackageName());
                    intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onError(Context context, String name, String errorCode) {
        Toast.makeText(context, "Fail to connect: " + name + " " + errorCode, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onConnected(Context context, String name) {
    }

    @Override
    protected void connectionLost(Context context, String name) {
        ChatManager.reconnect();
    }
}
