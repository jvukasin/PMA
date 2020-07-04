package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.dialogs.LoadingDialog;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class LoginActivity extends AppCompatActivity {

    private Button log_in;
    private Button forgot_pass;
    private Button new_user;
    private ImageView image;
    private TextView logoText, sloganText;
    private TextInputEditText email, password;
    private Button main_btn, bottom_btn;

    private FirebaseAuth auth;
    private User usr;
    SharedPreferences sharedPreferences;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        activity = this;

        log_in = (Button) findViewById(R.id.log_in);
        log_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString();
                String str_pass = password.getText().toString();
                if(NetworkUtil.isConnected(activity)) {
                    if(!checkFields(str_email, str_pass)) {
                        loginUser(str_email, str_pass);
                    }
                }
            }
        });

        forgot_pass = (Button) findViewById(R.id.forgot_password);
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        new_user = (Button) findViewById(R.id.new_user);
        image = (ImageView) findViewById(R.id.logo_image);
        logoText = (TextView) findViewById(R.id.logo_name);
        sloganText = (TextView) findViewById(R.id.login_slogan_name);
        email = (TextInputEditText) findViewById(R.id.enter_usrnm);
        email.setText(sharedPreferences.getString("email", ""));
        password = (TextInputEditText) findViewById(R.id.enter_pass);
        main_btn = (Button) findViewById(R.id.log_in);
        bottom_btn = (Button) findViewById(R.id.new_user);
        new_user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(email, "email_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(main_btn, "main_button_tran");
                pairs[6] = new Pair<View, String>(bottom_btn, "bottom_button_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

    }

    private boolean checkFields(String str_email, String str_pass) {
        boolean hasErr = false;
        if(TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Email field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password field cannot be empty");
            hasErr = true;
        }

        return hasErr;
    }

    private void loginUser(String str_email, String str_pass) {
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startLoadingDialog();
        auth.signInWithEmailAndPassword(str_email, str_pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String firebaseUserUID = auth.getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserUID);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            for(DataSnapshot snap: dataSnapshot.getChildren()){
                                switch (snap.getKey()) {
                                    case "firstName":
                                        editor.putString("firstName", snap.getValue(String.class));
                                        break;
                                    case "lastName":
                                        editor.putString("lastName", snap.getValue(String.class));
                                        break;
                                    case "phoneNumber":
                                        editor.putString("phone", snap.getValue(String.class));
                                        break;
                                    case "wallet":
                                        editor.putFloat("wallet", snap.getValue(Float.class));
                                        break;
                                }
                            }
                            editor.putString("email", email.getText().toString());
                            editor.putInt("radius", 30);
                            editor.commit();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    loadingDialog.dismissDialog();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
