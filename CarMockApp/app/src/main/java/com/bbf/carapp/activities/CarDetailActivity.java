package com.bbf.carapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bbf.carapp.R;
import com.bbf.carapp.model.Rent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CarDetailActivity extends AppCompatActivity {

    private ImageView QRCode;
    private String qrCodeText;
    private DatabaseReference rentReference;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        setTitle(R.string.detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rentReference = FirebaseDatabase.getInstance().getReference("Rent");
        QRCode = (ImageView) findViewById(R.id.imageView);
        final String plates = getIntent().getStringExtra("plate");
        qrCodeText = "{\"car\":\"" + plates + "\"}";
        generateQRCode();

        Rent rent = new Rent();
        rent.setActive("none");
        rentReference.child(plates).setValue(rent);

        listener = rentReference.child(plates).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Rent rent = dataSnapshot.getValue(Rent.class);
                if(rent.getActive() != null){
                    if(rent.getActive().equals("started")){
                        Intent intent = new Intent(CarDetailActivity.this, RideActivity.class);
                        intent.putExtra("plates", plates);
                        intent.putExtra("lat", rent.getLocation().getLatitude());
                        intent.putExtra("lng", rent.getLocation().getLongitude());
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        rentReference.removeEventListener(listener);
    }

    private void generateQRCode() {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x<300; x++){
                for (int y=0; y<300; y++){
                    bitmap.setPixel(x,y,bitMatrix.get(x,y)? Color.BLACK : Color.WHITE);
                }
            }
            QRCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
