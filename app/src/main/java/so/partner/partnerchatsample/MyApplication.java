package so.partner.partnerchatsample;

import android.app.Application;
import android.content.Intent;

import so.partner.lib.android.mqtt.MqttService;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    public MyApplication() {
        super();
        mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, MqttService.class));
        ChatManager.connect();
    }
}
