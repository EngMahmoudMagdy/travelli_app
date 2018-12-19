package com.magdy.travelli.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.magdy.travelli.R;

import java.net.CookieHandler;
import java.net.CookieManager;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    ImageView imageView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView)findViewById(R.id.splash_img);
        try {
            new Thread()
            {
                public void run()
                {

                    try {
                        sleep(SPLASH_TIME_OUT);
                        Intent i = new Intent(getBaseContext(),MainActivity.class);
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
