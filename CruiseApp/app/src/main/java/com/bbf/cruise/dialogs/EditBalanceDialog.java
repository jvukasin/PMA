package com.bbf.cruise.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bbf.cruise.R;
import com.bbf.cruise.activities.WalletActivity;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditBalanceDialog extends AppCompatDialogFragment {

    private static int minBalance = 25;
    private EditText addFundsEditText;
    SharedPreferences sharedPreferences;
    FirebaseAuth auth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_edit_balance, null);
        auth = FirebaseAuth.getInstance();
        builder.setView(view)
                .setTitle("Add funds")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!NetworkUtil.isConnected(getActivity())){
                            return;
                        }
                        addFundsEditText = view.findViewById(R.id.addFundsEdit);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(auth.getCurrentUser().getUid()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Double balance = dataSnapshot.getValue(Double.class);
                                String funds = addFundsEditText.getText().toString();

                                if(!funds.equals("")){
                                    double addedFunds = Double.parseDouble(funds);
                                    balance += addedFunds;
                                }

                                if(balance < minBalance){
                                    Toast.makeText(getActivity(), "Balance must be greater than 25 EUR.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String firebaseUserUID = auth.getCurrentUser().getUid();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseUserUID).child("wallet").setValue(balance);
                                TextView walletBalance = getActivity().findViewById(R.id.walletBalance);
                                walletBalance.setText(String.valueOf(balance));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                });
        return builder.create();
    }

}
