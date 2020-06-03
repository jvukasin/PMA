package com.bbf.cruise.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bbf.cruise.R;
import com.bbf.cruise.activities.WalletActivity;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditBalanceDialog extends AppCompatDialogFragment {

    private EditText addFundsEditText;
    SharedPreferences sharedPreferences;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
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
                        String funds = addFundsEditText.getText().toString();
                        // azuriraj stanje za shared
                        sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("wallet", Float.parseFloat(funds));
                        editor.apply();
                        // azuriraj u bazi
                        String firebaseUserUID = auth.getCurrentUser().getUid();
                        rootNode = FirebaseDatabase.getInstance();
                        reference = rootNode.getReference("Users");
                        reference.child(firebaseUserUID).child("wallet").setValue(Float.parseFloat(funds));

                        TextView walletBalance = getActivity().findViewById(R.id.walletBalance);
                        walletBalance.setText(String.valueOf(Float.parseFloat(funds)));
                    }
                });
        return builder.create();
    }

}
