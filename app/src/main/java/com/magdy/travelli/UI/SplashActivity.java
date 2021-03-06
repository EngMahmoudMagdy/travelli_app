package com.magdy.travelli.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.magdy.travelli.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            new Thread() {
                public void run() {

                    try {
                        sleep(SPLASH_TIME_OUT);
                        Intent i;
                        if (FirebaseAuth.getInstance().getUid() == null) {
                            i = new Intent(getBaseContext(), SignInActivity.class);
                        } else {
                            i = new Intent(getBaseContext(), MainActivity.class);
                        }
                        startActivity(i);
                        finish();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
