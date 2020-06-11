package com.bbf.cruise.activities;

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


public class WalletActivity extends AppCompatActivity {

    private Button button;
    private TextView walletBalance;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle("Wallet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button = (Button) findViewById(R.id.editBalanceButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        // postavi vrednost za balance
        walletBalance = (TextView) findViewById(R.id.walletBalance);
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        float balance = sharedPreferences.getFloat("wallet", 0);
        walletBalance = findViewById(R.id.walletBalance);
        walletBalance.setText(String.valueOf(balance));
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
