package so.partner.partnerchatsample;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import so.partner.lib.android.partnerpush.PartnerPushManager;

public class ChatManager {

    private static final String APP_ID = "baa9753a5940b2c5dd8b3a58c52ed2d4";
    private static final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJweCI6IjEzIiwicGYiOiIyIiwiZHQiOjE0MzYzMjEzMTQyMjN9.gg8vD91C8k6fc8AjuK_2VsIm2wLuF14kpaQcwn7lZwE";
    private static final String CLIENT_ID = getDeviceId();
    private static final String TOPIC = "PPChat";
    private static final int ALIVE_INTERVAL = 60;

    public static void connect() {
        PartnerPushManager.connect(MyApplication.getInstance(), APP_ID, API_KEY, CLIENT_ID, null, ALIVE_INTERVAL);
    }

    public static void reconnect() {
        disconnect();
        connect();
    }

    public static void disconnect() {
        PartnerPushManager.disconnect(MyApplication.getInstance(), APP_ID);
    }

    public static void subscribe() {
        PartnerPushManager.addTopic(MyApplication.getInstance(), APP_ID, TOPIC);
        Toast.makeText(MyApplication.getInstance(), TOPIC + "을 구독하였습니다.", Toast.LENGTH_LONG).show();
    }

    public static void unsubscribe() {
        PartnerPushManager.removeTopic(MyApplication.getInstance(), APP_ID, new String[]{TOPIC});
        Toast.makeText(MyApplication.getInstance(), TOPIC + "을 구독 해제하였습니다.", Toast.LENGTH_LONG).show();
    }

    public static void publish(String message) {
        JSONObject json = new JSONObject();
        try {
            json.put("nickname", "안드로이드야");
            json.put("text", message);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                json.put("nickname", "");
                json.put("text", "");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        PartnerPushManager.sendMessage(MyApplication.getInstance(), APP_ID, TOPIC, json);
    }

    private static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = tm.getDeviceId();
        String androidId = Settings.Secure.getString(MyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), tmDevice.hashCode());
        return deviceUuid.toString();
    }
}