package com.example.marishwaran.project01;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
public class SplashScreenActivity extends AppCompatActivity {
    public final static int SPLASH_TIMEOUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashScreenActivity.this, SignInActivity.class);
                startActivity(main);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
