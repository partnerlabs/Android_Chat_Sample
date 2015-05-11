package so.partner.partnerchatsample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;

import so.partner.lib.android.mqtt.MqttReceiver;
import so.partner.partnerchatsample.activity.Chat;
import so.partner.partnerchatsample.bean.PushedTalkMessage;

/**
 * Created by 120801RF3 on 2015-05-11.
 */
public class MqttBroadcastReceiver extends MqttReceiver {
    private static final String TAG = MqttBroadcastReceiver.class
            .getSimpleName();

    public static final String ACTION_MESSAGE_ARRIVED = "MESSAGE_ARRIVED";
    public static final String EXTRA_ROOM_ID = "roomId";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_CONTENT = "content";

    public static String preTag = null;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    protected void onMessage(Context context, String name, String topic,
                             byte[] payload) {
        if (ChatManager.CONNECTION_NAME.equals(name)) {
                try {
                    DBManager dbManager = new DBManager(context);
                    PushedTalkMessage message = new Gson().fromJson(new String(
                            payload), PushedTalkMessage.class);

                    String talkId = message.text.talkId;
                    String roomId = message.text.roomId;
                    String userId = message.text.who;
                    String content = message.text.text;
                    long sendTime = mSimpleDateFormat.parse(message.date.replace("Z", "+0000")).getTime();
                    if (!dbManager.addTalkMessage(talkId, roomId, userId,
                            content, sendTime)) {
                        dbManager.updateMessage(talkId, sendTime);
                    }

                    ActivityManager am = (ActivityManager) context
                            .getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
                    if (taskList.size() > 0) {
                        if (taskList.get(0).topActivity.getClassName().equals(
                                Chat.class.getName())) {
                            Intent intent = new Intent(ACTION_MESSAGE_ARRIVED);
                            intent.addCategory(context.getPackageName());
                            intent.putExtra(EXTRA_ROOM_ID, roomId);
                            intent.putExtra(EXTRA_USER_ID, userId);
                            intent.putExtra(EXTRA_CONTENT, content);
                            context.sendBroadcast(intent);
                            return;
                        }
                    }

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
