package com.bbf.cruise.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bbf.cruise.CruiseApplication;
import com.bbf.cruise.constants.FirebasePaths;
import com.bbf.cruise.dialogs.ConfirmRentDialog;
import com.bbf.cruise.dialogs.EditBalanceDialog;
import com.bbf.cruise.helpers.QRScanFeedback;
import com.bbf.cruise.service.CarService;
import com.bbf.cruise.service.QRProcessingService;
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
            // TODO show error dialog
            finish();
            return;
        }

        DatabaseReference carReference = FirebaseDatabase.getInstance().getReference(FirebasePaths.CARS_PATH).child(feedback.getParsed());

        carReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Car car = dataSnapshot.getValue(Car.class);
                CarService carService = new CarService();

                if (car == null) {
                    // TODO show error dialog
                    finish();
                    return;
                }

                if (!carService.isCarAvailableForRent(car)) {
                    // TODO show car not available for rent dialog
                    finish();
                    return;
                }

                openConfirmRentDialog(car);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }

    private void openConfirmRentDialog(Car car) {
        ConfirmRentDialog dialog = new ConfirmRentDialog(car);
        dialog.show(getSupportFragmentManager(), "Confirm Rent");
    }


}