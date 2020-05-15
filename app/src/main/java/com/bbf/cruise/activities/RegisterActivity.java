package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.tools.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import model.User;

public class RegisterActivity extends AppCompatActivity {

    private Button has_account;
    private Button signup;
    private TextInputEditText firstName, lastName, email, password, repPassword, phoneNo;
    private FirebaseAuth auth;

    SharedPreferences sharedPreferences;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        firstName = (TextInputEditText) findViewById(R.id.register_firstname);
        lastName = (TextInputEditText) findViewById(R.id.register_lastname);
        email = (TextInputEditText) findViewById(R.id.register_email);
        password = (TextInputEditText) findViewById(R.id.register_password);
        repPassword = (TextInputEditText) findViewById(R.id.register_password_repeat);
        phoneNo = (TextInputEditText) findViewById(R.id.register_phone);

        auth = FirebaseAuth.getInstance();

        has_account = (Button) findViewById(R.id.has_acc);
        has_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup = (Button) findViewById(R.id.sign_up_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkFields()) {
                    registerUser();
                }
            }
        });
    }

    private boolean checkFields() {
        boolean hasErr = false;
        if(TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError("First name field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError("Last name field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Email field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(phoneNo.getText().toString())) {
            phoneNo.setError("Phone number field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password field cannot be empty");
            hasErr = true;
        }
        if(TextUtils.isEmpty(repPassword.getText().toString())) {
            repPassword.setError("Password field cannot be empty");
            hasErr = true;
        }
        if(!password.getText().toString().equals(repPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords must match",
                    Toast.LENGTH_SHORT).show();
            hasErr = true;
        }

        return hasErr;
    }

    private void registerUser() {
        String str_email = email.getText().toString();
        String str_pass = password.getText().toString();
        final LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
        loadingDialog.startLoadingDialog();
        auth.createUserWithEmailAndPassword(str_email, str_pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("firstName", firstName.getText().toString());
                    editor.putString("lastName", lastName.getText().toString());
                    editor.putString("email", email.getText().toString());
                    editor.putString("phone", phoneNo.getText().toString());
                    editor.putFloat("wallet", 0);
                    editor.putString("distanceMode", "km");
                    editor.commit();

                    //adding User to database for info
                    User user = new User(firstName.getText().toString(), lastName.getText().toString(), password.getText().toString(), email.getText().toString(), phoneNo.getText().toString(), 0);
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference();
                    reference.child("Users").child(phoneNo.getText().toString()).setValue(user);

                    loadingDialog.dismissDialog();
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    loadingDialog.dismissDialog();
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
