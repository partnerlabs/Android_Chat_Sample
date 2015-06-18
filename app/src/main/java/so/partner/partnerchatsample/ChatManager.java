package so.partner.partnerchatsample;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

import so.partner.lib.android.mqtt.MqttConnection;
import so.partner.lib.android.mqtt.MqttManager;

public class ChatManager {

    private static final String TAG = ChatManager.class.getSimpleName();

    public static final String APP_ID = "android_partner_chat_sample";
    private static final String MQTT_BROKER_URI = "tcp://partnerinserver.iptime.org:1888";
    private static final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJweCI6IjIiLCJhaSI6ImFuZHJvaWRfcGFydG5lcl9jaGF0X3NhbXBsZSIsInBmIjoiMSIsImR0IjoxNDMyODA1ODA5MjcxfQ.M5QeC0gX5IbIPZn7-c_-x0SzUYiubNecbZhZcC9SBkc";
    private static final String CLIENT_ID = getDeviceId();
    private static final String TOPIC = "partner_chat_example";
    private static final int ALIVE_INTERVAL = 60;

    public static void connect() {
        MqttManager.connect(MyApplication.getInstance(), APP_ID, MQTT_BROKER_URI, API_KEY, CLIENT_ID, new String[]{TOPIC}, ALIVE_INTERVAL);
    }

    public static void reconnect() {
        disconnect();
        connect();
    }

    public static void disconnect() {
        MqttManager.disconnect(MyApplication.getInstance(), APP_ID);
    }

    public static String getClientId() {
        MqttConnection connection = MqttManager.getConnections(MyApplication.getInstance()).get(APP_ID);
        if (connection != null) {
            return connection.getClientId();
        }
        return "";
    }

    public static void publish(String message) {
        MqttManager.publish(MyApplication.getInstance(), APP_ID, TOPIC, message);
    }

    private static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = tm.getDeviceId();
        String androidId = Settings.Secure.getString(MyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), tmDevice.hashCode());
        return deviceUuid.toString();
    }
}