package com.bbf.cruise.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbf.cruise.CruiseApplication;
import com.bbf.cruise.R;
import com.bbf.cruise.constants.FirebasePaths;
import com.bbf.cruise.dialogs.ConfirmRentDialog;
import com.bbf.cruise.dialogs.EditBalanceDialog;
import com.bbf.cruise.fragments.MapFragment;
import com.bbf.cruise.helpers.QRScanFeedback;
import com.bbf.cruise.service.QRProcessingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import model.Car;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Context context = CruiseApplication.getAppContext();
    private boolean resumeCamera = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    50); }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setTitle(R.string.qrScan);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
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

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        processFeedback(QRProcessingService.processScannedQR(rawResult));

    }

    private void processFeedback(QRScanFeedback feedback) {
        if (!feedback.isSuccess()) {
            Toast.makeText(context, "Feedback is Success FALSE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference carReference = FirebaseDatabase.getInstance().getReference(FirebasePaths.CARS_PATH).child(feedback.getParsed());


        carReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Car car = dataSnapshot.getValue(Car.class);

                DatabaseReference referenceReservations = FirebaseDatabase.getInstance().getReference("Reservations");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                String hasReservation = sharedPreferences.getString("reservation", "");
                if(car != null && !car.isOccupied()) {
                    openConfirmRentDialog(car);
                    mScannerView.stopCamera();
                } else if (car.isOccupied() && hasReservation.equals(car.getReg_number())) {
                    openConfirmRentDialog(car);
                    mScannerView.stopCamera();
                } else if (car.isOccupied() && hasReservation.equals("none")) {
                    Toast.makeText(context, "This car is occupied or reserved.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error fetching info if car is reserved", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void openConfirmRentDialog(Car car) {
        ConfirmRentDialog dialog = new ConfirmRentDialog(car, this);
        dialog.show(getSupportFragmentManager(), "Confirm Rent");
    }


}