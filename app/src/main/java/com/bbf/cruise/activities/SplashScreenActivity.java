package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.R;

public class SplashScreenActivity extends AppCompatActivity {

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

        int SPLASH_TIME_OUT = 2500;

        new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                  Pair[] pairs = new Pair[2];
                  pairs[0] = new Pair<View, String>(image, "logo_image");
                  pairs[1] = new Pair<View, String>(large, "logo_text");

                  ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

                  startActivity(intent, options.toBundle());

                  finish();
              }
          }, SPLASH_TIME_OUT);

    }
}
