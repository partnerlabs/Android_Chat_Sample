package so.partner.partnerchatsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import so.partner.partnerchatsample.ChatManager;
import so.partner.partnerchatsample.R;

/**
 * Created by 120801RF3 on 2015-05-06.
 */
public class Init extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_init);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ChatManager.login();
                goToChat();
            }
        });
    }

    private void goToChat() {
        startActivity(new Intent(this, Chat.class));
        finish();
    }
}
