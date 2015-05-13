package so.partner.partnerchatsample;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

import so.partner.lib.android.mqtt.MqttConnection;
import so.partner.lib.android.mqtt.MqttManager;

/**
 * Created by Administrator on 2015-05-09.
 */
public class ChatManager {

    public static final String CONNECTION_NAME = "hardcoding";

    private static final String TAG = ChatManager.class.getSimpleName();

    private static final String MQTT_BROKER_URI = "tcp://partnerinserver.iptime.org:1888";
    private static final String API_KEY = "5pHwTuACxqPPx2PRzIoWR07t35c8anEh4r7TcZFb3P8cz";
    private static final String CLIENT_ID = getDeviceId();
    private static final String TOPIC = "partner_chat_example";
    private static final int ALIVE_INTERVAL = 60;

    public static void login() {
        MqttManager.connect(MyApplication.getInstance(), CONNECTION_NAME, MQTT_BROKER_URI, API_KEY, CLIENT_ID, new String[]{TOPIC}, ALIVE_INTERVAL);
    }

    public static void logout() {
        MqttManager.disconnect(MyApplication.getInstance(), CONNECTION_NAME);
    }

    public static String getClientId() {
        MqttConnection connection = MqttManager.getConnections(MyApplication.getInstance()).get(CONNECTION_NAME);
        if (connection != null) {
            return connection.getClientId();
        }
        return null;
    }

    public static void publish(String message) {
        MqttConnection connection = MqttManager.getConnections(MyApplication.getInstance()).get(CONNECTION_NAME);
        if (connection != null) {
            connection.publish(message);
        }
    }

    private static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = tm.getDeviceId();
        String androidId = Settings.Secure.getString(MyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), tmDevice.hashCode());
        return deviceUuid.toString();
    }
}