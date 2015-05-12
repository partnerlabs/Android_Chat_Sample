package so.partner.partnerchatsample;

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
    private static final String CLIENT_ID = "client";
    private static final String ROOM_ID = "roomId";
    private static final int ALIVE_INTERVAL = 60;

    public static final String THIRD_PARTY_RESPONSE_CODE_SUCCEED = "SUCCEED";

    public static void login() {
        MqttManager.connect(MyApplication.getInstance(), CONNECTION_NAME, MQTT_BROKER_URI, API_KEY, CLIENT_ID, new String[]{ROOM_ID}, ALIVE_INTERVAL);
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
        connection.publish(message);
    }
}