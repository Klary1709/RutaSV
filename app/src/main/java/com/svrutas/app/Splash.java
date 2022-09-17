package com.svrutas.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Splash extends AppCompatActivity {
    private ImageView LogoRutaSV;
    private LottieAnimationView Splash;
    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.t = new Timer();
        this.LogoRutaSV= findViewById(R.id.imgBus);
        this.Splash= findViewById(R.id.imageViewSplash);

        LogoRutaSV.setImageResource(R.drawable.ic_logorutasv2);
        //circulos
        Splash.setAnimation(R.raw.lf30_editor_jryok1vk);
        //Gotas
        //Splash.setAnimation(R.raw.lf30_editor_xi4pmywj);


        long duracion = TimeUnit.SECONDS.toSeconds(600);
        new CountDownTimer(duracion, 1000) {
            @Override
            public void onTick(long l) {
                LogoRutaSV.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {
                LogoRutaSV.setVisibility(View.VISIBLE);
            }
        }.start();

        long duracionSplash = TimeUnit.SECONDS.toSeconds(2500);
        new CountDownTimer(duracionSplash, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                Intent i = new Intent(Splash.this,HomeActivity.class);
                startActivity(i);
                finishAffinity();
            }
        }.start();


    }
}