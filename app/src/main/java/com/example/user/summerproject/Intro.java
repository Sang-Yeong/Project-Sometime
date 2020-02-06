package com.example.user.summerproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Intro extends Activity {
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        handler = new Handler();
        handler.postDelayed(runnable, 2000); //인트로 화면을 몇초동안 띄울 것 인지 (2초)
    }

    private final Runnable runnable = new Runnable(){
        @Override
        public void run(){
            Intent intent = new Intent(Intro.this, MainActivity.class);
            startActivity(intent);
            finish();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };
}
