package com.bbf.cruise.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private Button log_in;
    private Button forgot_pass;
    private Button new_user;

    ImageView image;
    TextView logoText, sloganText;
    TextInputEditText email, password;
    Button main_btn, bottom_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        log_in = (Button) findViewById(R.id.log_in);
        log_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        new_user = (Button) findViewById(R.id.new_user);
        image = (ImageView) findViewById(R.id.logo_image);
        logoText = (TextView) findViewById(R.id.logo_name);
        sloganText = (TextView) findViewById(R.id.login_slogan_name);
        email = (TextInputEditText) findViewById(R.id.enter_usrnm);
        password = (TextInputEditText) findViewById(R.id.enter_pass);
        main_btn = (Button) findViewById(R.id.log_in);
        bottom_btn = (Button) findViewById(R.id.new_user);
        new_user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(email, "email_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(main_btn, "main_button_tran");
                pairs[6] = new Pair<View, String>(bottom_btn, "bottom_button_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

    }
}
