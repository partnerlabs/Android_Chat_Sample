package so.partner.partnerchatsample;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import so.partner.lib.android.partnerpush.PartnerPushManager;

public class ChatManager {

    public static final String APP_ID = "788b7892fa7bb38dca1db1e56ab5a591";
    private static final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJweCI6IjEwIiwicGYiOiIyIiwiZHQiOjE0MzUwMjMxODMzMDh9.fAYgmsEP4lHFhYPqXLH8_u22wAtxyXuiH_FLIN40BpY";
    private static final String CLIENT_ID = getDeviceId();
    private static final String TOPIC = "PPChat";
    private static final int ALIVE_INTERVAL = 60;

    public static void connect() {
        PartnerPushManager.connect(MyApplication.getInstance(), APP_ID, API_KEY, CLIENT_ID, new
                String[]{TOPIC}, ALIVE_INTERVAL);
    }

    public static void reconnect() {
        disconnect();
        connect();
    }

    public static void disconnect() {
        PartnerPushManager.disconnect(MyApplication.getInstance(), APP_ID);
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