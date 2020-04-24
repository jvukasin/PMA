package com.bbf.cruise.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
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

        int SPLASH_TIME_OUT = 2500;

        new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  Intent intent = new Intent(SplashScreen.this, Login.class);
                  Pair[] pairs = new Pair[2];
                  pairs[0] = new Pair<View, String>(image, "logo_image");
                  pairs[1] = new Pair<View, String>(large, "logo_text");

                  ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);

                  startActivity(intent, options.toBundle());
              }
          }, SPLASH_TIME_OUT);

    }
}
