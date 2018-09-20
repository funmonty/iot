package com.anoram.homecontrol;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent myIntent = new Intent(SplashScreen.this, LoginActivity.class);

                SplashScreen.this.startActivity(myIntent);

            }
        }, 3000);
    }
}
