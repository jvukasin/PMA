package com.bbf.cruise.dialogs;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bbf.cruise.activities.CarDetailActivity;
import com.bbf.cruise.activities.RideActivity;
import com.bbf.cruise.service.RentService;
import com.bbf.cruise.service.ReservationService;
import com.bbf.cruise.tools.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Date;

import model.Car;
import model.LocationObject;
import model.Rent;

public class ConfirmRentDialog extends AppCompatDialogFragment {

    SharedPreferences sharedPreferences;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth auth;
    Car carForRent;
    Context context;

    public ConfirmRentDialog(Car carForRent, Context context) {
        this.carForRent = carForRent;
        this.context = context;
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
                        if(NetworkUtil.isConnected(getActivity())) {
                            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                                if (ReservationService.class.getName().equals(service.service.getClassName())) {
                                    Intent intent = new Intent(context, ReservationService.class);
                                    intent.putExtra("plates", carForRent.getReg_number());
                                    intent.setAction("STOP_FOREGROUND");
                                    context.startService(intent);
                                    break;
                                }
                            }

                            FirebaseDatabase.getInstance().getReference().child("cars").child(carForRent.getReg_number()).child("occupied").setValue(true);
                            FirebaseDatabase.getInstance().getReference("Rent").child(carForRent.getReg_number()).child("active").setValue("started");
                            LocationObject loc = new LocationObject(carForRent.getLocation().getLatitude(), carForRent.getLocation().getLongitude());
                            FirebaseDatabase.getInstance().getReference("Rent").child(carForRent.getReg_number()).child("location").setValue(loc);

                            Intent intent = new Intent(context, RideActivity.class);
                            intent.putExtra("plates", carForRent.getReg_number());
                            intent.putExtra("lat", carForRent.getLocation().getLatitude());
                            intent.putExtra("lng", carForRent.getLocation().getLongitude());
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
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
