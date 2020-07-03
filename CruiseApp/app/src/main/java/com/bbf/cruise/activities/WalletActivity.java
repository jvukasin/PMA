package com.bbf.cruise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bbf.cruise.R;
import com.bbf.cruise.dialogs.EditBalanceDialog;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WalletActivity extends AppCompatActivity {

    private Button button;
    private TextView walletBalance;

    SharedPreferences sharedPreferences;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle("Wallet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();

        button = (Button) findViewById(R.id.editBalanceButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        boolean isConnected = NetworkUtil.isConnected(this);
        
        reference.child(auth.getCurrentUser().getUid()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double balance = dataSnapshot.getValue(Double.class);
                walletBalance = findViewById(R.id.walletBalance);
                walletBalance.setText(String.valueOf(balance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public void openDialog(){
        EditBalanceDialog editBalanceDialog = new EditBalanceDialog();
        editBalanceDialog.show(getSupportFragmentManager(), "Edit balance");
    }


}
