package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    Animation sideAnim, bottAnim;
    ImageView image;
    TextView large, small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        bottAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = (ImageView) findViewById(R.id.cruiseSplash);
        large = (TextView) findViewById(R.id.splashText);
        small = (TextView) findViewById(R.id.splashUnderText);

        image.setAnimation(sideAnim);
        large.setAnimation(bottAnim);
        small.setAnimation(bottAnim);

        int SPLASH_TIME_OUT = 3500;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish(); // da nebi mogao da ode back na splash
            }
        }, SPLASH_TIME_OUT);
    }
}
