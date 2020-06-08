package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bbf.cruise.R;
import com.bbf.cruise.dialogs.LoadingDialog;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Button sendBtn;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.enter_email_fp);
        activity = this;
        sendBtn = findViewById(R.id.send_email);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                if(NetworkUtil.isConnected(activity)) {
                    changePassEmail(emailTxt);
                }
            }
        });

    }

    private void changePassEmail(String emailTxt) {
        final LoadingDialog loadingDialog = new LoadingDialog(ForgotPasswordActivity.this);
        loadingDialog.startLoadingDialog();
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailTxt)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismissDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
