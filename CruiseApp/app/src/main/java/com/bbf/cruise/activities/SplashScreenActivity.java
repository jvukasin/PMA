package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    Animation sideAnim, bottAnim;
    ImageView image;
    TextView large, small;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        bottAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = (ImageView) findViewById(R.id.cruiseSplash);
        large = (TextView) findViewById(R.id.splashText);
        small = (TextView) findViewById(R.id.splashUnderText);

        image.setAnimation(sideAnim);
        large.setAnimation(bottAnim);
        small.setAnimation(bottAnim);

        int SPLASH_TIME_OUT = 2500;

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {

                  if(firebaseUser == null) {
                      Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                      Pair[] pairs = new Pair[2];
                      pairs[0] = new Pair<View, String>(image, "logo_image");
                      pairs[1] = new Pair<View, String>(large, "logo_text");

                      ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);

                      startActivity(intent, options.toBundle());

                      finish();
                  } else {
                      startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                      finish();
                  }

              }
          }, SPLASH_TIME_OUT);

    }
}
