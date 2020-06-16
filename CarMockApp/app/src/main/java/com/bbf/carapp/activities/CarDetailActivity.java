package com.bbf.carapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bbf.carapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class CarDetailActivity extends AppCompatActivity {

    private ImageView QRCode;
    private String qrCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        setTitle(R.string.detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        QRCode = (ImageView) findViewById(R.id.imageView);
        String plates = getIntent().getStringExtra("plate");
        qrCodeText = "{\"car\":\"" + plates + "\"}";
        generateQRCode();
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
