package so.partner.partnerchatsample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.bean.ChatMessage;
import so.partner.partnerchatsample.bean.PushedChatMessage;

/**
 * Created by 120801RF3 on 2015-05-11.
 */
public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class
            .getSimpleName();

    public static final String ACTION_MESSAGE_ARRIVED = "MESSAGE_ARRIVED";
    public static final String EXTRA_CHAT_MESSAGE = "chatMessage";

    public static final String EXTRA_ROOM_ID = "roomId";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_CONTENT = "content";

    public static String preTag = null;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    protected void onMessage(Context context, String name, String topic,
                             byte[] payload) {
        Log.i(TAG, "onMessage()");
        Log.i(TAG, "name = " + name);
        Log.i(TAG, "topic = " + topic);
        Log.i(TAG, "payload = " + new String(payload));
        if (ChatManager.CONNECTION_NAME.equals(name)) {
            try {
                PushedChatMessage message = new Gson().fromJson(new String(
                        payload), PushedChatMessage.class);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.userId = message.text.talkId;
                chatMessage.roomId = message.text.roomId;
                chatMessage.content = message.text.text;

                String talkId = message.text.talkId;
                String roomId = message.text.roomId;
                String userId = message.text.who;
                String content = message.text.text;
                long sendTime = mSimpleDateFormat.parse(message.date.replace("Z", "+0000")).getTime();

                Intent intent = new Intent(ACTION_MESSAGE_ARRIVED);
                intent.addCategory(context.getPackageName());
                intent.putExtra(EXTRA_CHAT_MESSAGE, chatMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

//					if (!userId.equals(ChatManager.getClientId())) {
//						if (preTag != null) {

//							NotificationUtils.cancelNotification(context,
//									preTag, 0);
//						}
//						Intent notiIntent = new Intent(context, Main.class);
//						notiIntent.setAction(ACTION_MESSAGE_ARRIVED);
//						notiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//						notiIntent.putExtra(Main.EXTRA_ROOM_ID, roomId);
//						NotificationUtils.notify(context, notiIntent, roomId,
//								0, Notification.DEFAULT_ALL, userId, content,
//								R.drawable.ic_launcher);
//						preTag = roomId;
//					}
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
