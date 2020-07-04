package com.bbf.cruise.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bbf.cruise.Mokap;
import com.bbf.cruise.R;
import com.bbf.cruise.tools.NetworkUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import model.RideHistory;
import model.RideHistoryItem;

public class RideHistoryAdapter extends BaseAdapter {

    private Activity activity;
    private List<RideHistoryItem> rideHistoryList;

    public RideHistoryAdapter(Activity activity, ArrayList<RideHistoryItem> list) {
        this.activity = activity;
        this.rideHistoryList = list;
    }

    @Override
    public int getCount() {
        return rideHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return rideHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        RideHistoryItem rideHistoryItem = rideHistoryList.get(position);

        if(convertView == null){
            vi = activity.getLayoutInflater().inflate(R.layout.ride_history_item, null);
        }

        TextView startDate = (TextView) vi.findViewById(R.id.startDate);
        TextView endDate = (TextView) vi.findViewById(R.id.endDate);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        TextView price = (TextView) vi.findViewById(R.id.price);
        TextView points = (TextView) vi.findViewById(R.id.points);

        price.setText(String.valueOf(rideHistoryItem.getPrice()) + " EUR");
        startDate.setText(rideHistoryItem.getStartDate());
        endDate.setText(rideHistoryItem.getEndDate());
        distance.setText(String.valueOf(rideHistoryItem.getDistance()) + " km");
        points.setText(String.valueOf(rideHistoryItem.getPoints()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("rideHistory").child(user.getUid()).child(rideHistoryItem.getStartDate() + ".jpg");

        final View finalVi = vi;

//                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                ImageView image = finalVi.findViewById(R.id.route);
//                Glide.with(activity.getApplicationContext())
//                        .load(uri.toString())
//                        .into(image);
//            }
//        });

        ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ImageView image = finalVi.findViewById(R.id.route);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(getRoundedCornerBitmap(bitmap, 40));
            }
        });

        return vi;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    private void scaleImage(ImageView view, Bitmap bitmap) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = view.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = activity.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

}
