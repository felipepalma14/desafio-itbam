package com.felipe.palma.desafioitbam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        progressBar = findViewById(R.id.progressBar);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));

        }, Config.SPLASH_TIME);

    }

}