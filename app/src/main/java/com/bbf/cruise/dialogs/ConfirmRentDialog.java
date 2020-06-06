package com.bbf.cruise.dialogs;

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
import com.bbf.cruise.service.RentService;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import model.Car;
import model.Rent;

public class ConfirmRentDialog extends AppCompatDialogFragment {

    SharedPreferences sharedPreferences;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth auth;
    Car carForRent;

    public ConfirmRentDialog(Car carForRent) {
        this.carForRent = carForRent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_confirm_rent, null);
        initTextFields(view);
        auth = FirebaseAuth.getInstance();
        builder.setView(view)
                .setTitle("Confirm Rent")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!NetworkUtil.isConnected(getActivity())) {
                            return;
                        }

                        RentService rentService = new RentService();
                        Rent r = rentService.createRent(carForRent.getReg_number(), auth.getUid());
                        System.out.println(r);
                    }
                });
        return builder.create();
    }

    private void initTextFields(View view) {
        TextView carName =  view.findViewById(R.id.carName);
        carName.setText(carForRent.getBrand() + carForRent.getModel());

        TextView carPlate = view.findViewById(R.id.carRegNumber);
        carPlate.setText(carForRent.getReg_number());
    }
}
