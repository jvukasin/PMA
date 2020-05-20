package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bbf.cruise.R;
import com.bbf.cruise.tools.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPass, newPass, repeatPass;
    private Button changePassBtn;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle(R.string.changePassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentPass = findViewById(R.id.enter_currPass);
        newPass = findViewById(R.id.enter_newPass);
        repeatPass = findViewById(R.id.enter_repeatNewPass);

        changePassBtn = findViewById(R.id.changePassBtn);
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassOK()) {
                    resetPassword();
                }
            }
        });
    }

    private void resetPassword() {
        //TODO DA LI U ASYNCTASK?
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            final LoadingDialog loadingDialog = new LoadingDialog(ChangePasswordActivity.this);
            loadingDialog.startLoadingDialog();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass.getText().toString());
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(ChangePasswordActivity.this, "Password successfully changed.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ChangePass", "Error while changing password");
                    }
                }
            });
        } else {
            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
            finish();
        }
    }

    private boolean checkPassOK() {
        String nPass = newPass.getText().toString();
        String rPass = repeatPass.getText().toString();
        boolean isOk = true;
        if(TextUtils.isEmpty(currentPass.getText().toString())) {
            currentPass.setError("Field cannot be empty");
            isOk = false;
        }
        if(TextUtils.isEmpty(newPass.getText().toString())) {
            newPass.setError("Field cannot be empty");
            isOk = false;
        }
        if(TextUtils.isEmpty(repeatPass.getText().toString())) {
            repeatPass.setError("Field cannot be empty");
            isOk = false;
        }
        if(!nPass.equals(rPass)) {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
            isOk = false;
        }
        return isOk;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
