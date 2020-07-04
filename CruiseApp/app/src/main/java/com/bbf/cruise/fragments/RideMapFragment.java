package com.bbf.cruise.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bbf.cruise.MainActivity;
import com.bbf.cruise.R;
import com.bbf.cruise.activities.CarDetailActivity;
import com.bbf.cruise.activities.NearbyCarsActivity;
import com.bbf.cruise.adapters.FavoriteCarsAdapter;
import com.bbf.cruise.dialogs.LocationDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.Car;
import model.CarItem;
import model.LocationObject;
import model.RideHistory;

import static android.content.Context.MODE_PRIVATE;

public class RideMapFragment extends Fragment implements OnMapReadyCallback {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static String RIDE_FINISHED_ACTION = "RIDE_FINISHED_ACTION";
    private static String SAVE_RIDE_HISTORY_ACTION = "SAVE_RIDE_HISTORY_ACTION";
    private static String ADD_CAR_MARKER_ACTION = "addCarMarker";
    private static double sum = 0;

    private List<LatLng> route = new ArrayList<>();
    private LocationManager locationManager;
    private String provider;
    private SupportMapFragment mMapFragment;
    private AlertDialog dialog;
    private Marker home, car;
    private GoogleMap map;
    private DatabaseReference ridHistoryReference;
    private DatabaseReference userReference;
    private DatabaseReference carReference;
    private DatabaseReference rentReference;
    private LatLng startLocation;
    private LatLng lastLocation;

    private static long UPDATE_INTERVAL = 1200;
    private static long FASTEST_INTERVAL = 1200;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private SharedPreferences sharedPreferences;
    private String plates;
    private ValueEventListener locLis;


    public static RideMapFragment newInstance() {
        RideMapFragment mpf = new RideMapFragment();
        return mpf;
    }

    /**
     * Prilikom kreidanja fragmenta preuzimamo sistemski servis za rad sa lokacijama
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sacuvace stanje fragmenta prilikom promene konfiguracije, npr: promena orijentacije ekrana
        setRetainInstance(true);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
//                route.add(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
//                addCarMarker(locationResult.getLastLocation());
//                updateDistance();
            }
        };

        ridHistoryReference = FirebaseDatabase.getInstance().getReference("RideHistory");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        carReference = FirebaseDatabase.getInstance().getReference("cars");
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter rideFinishedFilter = new IntentFilter(RIDE_FINISHED_ACTION);
        requireActivity().registerReceiver(rideFinishedReciever, rideFinishedFilter);

        IntentFilter saveRideHistoryFilter = new IntentFilter(SAVE_RIDE_HISTORY_ACTION);
        requireActivity().registerReceiver(saveRideHistoryReciever, saveRideHistoryFilter);

        IntentFilter addMarkerFilter = new IntentFilter(ADD_CAR_MARKER_ACTION);
        requireActivity().registerReceiver(addMarkerReceiver, addMarkerFilter);

    }

    /**
     * Kada zelmo da dobijamo informacije o lokaciji potrebno je da specificiramo
     * po kom kriterijumu zelimo da dobijamo informacije GSP, MOBILNO(WIFI, MObilni internet), GPS+MOBILNO
     * **/
    private void createMapFragmentAndInflate() {
        //specificiramo krijterijum da dobijamo informacije sa svih izvora
        //ako korisnik to dopusti
        Criteria criteria = new Criteria();

        //sistemskom servisu prosledjujemo taj kriterijum da bi
        //mogli da dobijamo informacje sa tog izvora
        provider = locationManager.getBestProvider(criteria, true);

        //kreiramo novu instancu fragmenta
        mMapFragment = SupportMapFragment.newInstance();

        //i vrsimo zamenu trenutnog prikaza sa prikazom mape
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.rideMapContainer, mMapFragment).commit();

        //pozivamo ucitavnje mape.
        //VODITI RACUNA OVO JE ASINHRONA OPERACIJA
        //LOKACIJE MOGU DA SE DOBIJU PRE MAPE I OBRATNO
        mMapFragment.getMapAsync(this);
    }

    private void showLocatonDialog() {
        if (dialog == null) {
            dialog = new LocationDialog(getActivity()).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        createMapFragmentAndInflate();

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps && !wifi) {
            showLocatonDialog();
        } else {
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                } else if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    //Request location updates:
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            }
        }

        rentReference = FirebaseDatabase.getInstance().getReference("Rent").child(plates).child("location");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.ride_map_layout, vg, false);
        startLocation = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
        plates = getArguments().getString("plates");

        return view;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Allow user location")
                        .setMessage("To continue working we need your locations....Allow now?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }

                } else if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }

                }
                return;
            }

        }
    }

    /**
     * KAda je mapa spremna mozemo da radimo sa njom.
     * Mozemo reagovati na razne dogadjaje dodavanje markera, pomeranje markera,klik na mapu,...
     * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        addMarker(startLocation);

        rentReference = FirebaseDatabase.getInstance().getReference("Rent").child(plates).child("location");
        rentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationObject location = dataSnapshot.getValue(LocationObject.class);
                addCarMarker(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // ako zelmo da reagujemo na klik markera koristimo marker click listener
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().equals("YOUR_POSITION")) {
                    Toast.makeText(getActivity(), "You are here", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // ako je potrebno da reagujemo na pomeranje markera koristimo marker drag listener
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Toast.makeText(getActivity(), "Drag started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Toast.makeText(getActivity(), "Dragging", Toast.LENGTH_SHORT).show();
                map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(getActivity(), "Drag ended", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addMarker(LatLng loc) {
        if (home != null) {
            home.remove();
        }

        int height = 100;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.start_pin);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        home = map.addMarker(new MarkerOptions()
                .title("YOUR_POSITION")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .position(loc));
        home.setFlat(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addCarMarker(LatLng loc) {
        if(car != null){
            car.remove();
        }

        int height = 100;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.car_map_pin_no_border_yellow);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        car = map.addMarker(new MarkerOptions()
                .title("")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .position(loc));
        car.setFlat(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(rideFinishedReciever);
        requireActivity().unregisterReceiver(saveRideHistoryReciever);
        requireActivity().unregisterReceiver(addMarkerReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sum = 0;
        route.clear();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private final BroadcastReceiver rideFinishedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RIDE_FINISHED_ACTION)){
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                route = intent.getParcelableArrayListExtra("route");
                System.out.println("ROUTE");
                System.out.println(route.size());
                if(route.size() >= 1){
                    Polyline routePoly = map.addPolyline(new PolylineOptions()
                            .width(5)
                            .color(Color.BLUE)
                            .geodesic(true));
                    routePoly.setPoints(route);
                    lastLocation = route.get(route.size()-1);

                    int height = 100;
                    int width = 70;
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.finish_pin);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    map.addMarker(new MarkerOptions()
                            .title("FINISH")
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                            .position(lastLocation))
                            .setFlat(true);
                }
            }
            if(route.isEmpty()){
                lastLocation = startLocation;
            }
            sum = 0;
            route.clear();
        }
    };

    private final BroadcastReceiver addMarkerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ADD_CAR_MARKER_ACTION)) {
                LatLng ll = new LatLng(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lon", 0));
                addCarMarker(ll);
            }
        }
    };

//    private void updateCar(LatLng lastLocation, final RideHistory rideHistory) {
//        carReference.child(plates).child("occupied").setValue(false);
//        carReference.child(plates).child("location").setValue(new LocationObject(lastLocation.latitude, lastLocation.longitude));
//
//        carReference.child(plates).child("no_of_rides").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Integer count = dataSnapshot.getValue(Integer.class);
//                carReference.child(plates).child("no_of_rides").setValue(count+1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        carReference.child(plates).child("fuel_distance").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Double fuelDistance = dataSnapshot.getValue(Double.class);
//                if(fuelDistance == 0){
//                    return;
//                }
//                DecimalFormat df = new DecimalFormat("#.#");
//                double temp = fuelDistance - rideHistory.getDistance();
//                double rounded = Double.valueOf(df.format(temp));
//                carReference.child(plates).child("fuel_distance").setValue(rounded);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        carReference.child(plates).child("mileage").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Double mileage = dataSnapshot.getValue(Double.class);
//                double temp = mileage + rideHistory.getDistance();
//                DecimalFormat df = new DecimalFormat("#.#");
//                double rounded = Double.valueOf(df.format(temp));
//                carReference.child(plates).child("mileage").setValue(rounded);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private final BroadcastReceiver saveRideHistoryReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SAVE_RIDE_HISTORY_ACTION)){
                final String path = "";
                final boolean flagBonusPoints = intent.getBooleanExtra("flagBonusPoints", false);
                RideHistory rideHistory = (RideHistory) intent.getSerializableExtra("rideHistory");
                final RideHistory temp = rideHistory;
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        uploadFile(bitmap, temp, flagBonusPoints);
                    }
                };
                map.snapshot(callback);
                //updateCar(lastLocation, rideHistory);
            }
        }
    };

    private void uploadFile(Bitmap bitmap, final RideHistory rideHistory, final boolean flagBonusPoints) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cruise-e99e3.appspot.com/");
        StorageReference mountainImagesRef = storageRef.child("rideHistory/" + rideHistory.getUserId() + "/" + rideHistory.getStartDate()  +  ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        final byte[] data = baos.toByteArray();
        final String path = "";
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(flagBonusPoints == false){
                            pay(uri, rideHistory);
                        }else{
                            payWithBP(uri, rideHistory);
                        }
                    }
                });
            }
        });

    }

    private void payWithBP(Uri uri, RideHistory rideHistory) {
        String url = uri.toString();
        RideHistory rdh = new RideHistory(rideHistory);
        final String userId = rdh.getUserId();
        final double distance = rdh.getDistance();
        final double price = rdh.getPrice();
        rdh.setImage(url);

        ridHistoryReference.child(rdh.getUserId()).push().setValue(rdh);
        userReference.child(rdh.getUserId()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                DecimalFormat df = new DecimalFormat("#.#");
                double rounded = Double.parseDouble(df.format((value - price)));
                userReference.child(userId).child("wallet").setValue(rounded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userReference.child(rdh.getUserId()).child("bonusPoints").setValue(0);

        userReference.child(rdh.getUserId()).child("totalRides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer rides = dataSnapshot.getValue(Integer.class);
                userReference.child(userId).child("totalRides").setValue(rides+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userReference.child(rdh.getUserId()).child("totalDistance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double totalDistance = dataSnapshot.getValue(Double.class);
                DecimalFormat df = new DecimalFormat("#.#");
                double rounded = Double.parseDouble(df.format((totalDistance + distance)));
                userReference.child(userId).child("totalDistance").setValue(rounded);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pay(Uri uri, RideHistory rideHistory) {
        String url = uri.toString();
        RideHistory rdh = new RideHistory(rideHistory);
        final String userId = rdh.getUserId();
        final double distance = rdh.getDistance();
        final double price = rdh.getPrice();
        final int points = rdh.getPoints();
        rdh.setImage(url);

        ridHistoryReference.child(rdh.getUserId()).push().setValue(rdh);
        userReference.child(rdh.getUserId()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double value = dataSnapshot.getValue(Double.class);
                DecimalFormat df = new DecimalFormat("#.#");
                double rounded = Double.parseDouble(df.format((value - price)));
                userReference.child(userId).child("wallet").setValue(rounded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userReference.child(rdh.getUserId()).child("bonusPoints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currPoints = dataSnapshot.getValue(Integer.class);
                userReference.child(userId).child("bonusPoints").setValue(currPoints + points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userReference.child(rdh.getUserId()).child("totalRides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer rides = dataSnapshot.getValue(Integer.class);
                userReference.child(userId).child("totalRides").setValue(rides+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userReference.child(rdh.getUserId()).child("totalDistance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double totalDistance = dataSnapshot.getValue(Double.class);
                DecimalFormat df = new DecimalFormat("#.#");
                double rounded = Double.parseDouble(df.format((totalDistance + distance)));
                userReference.child(userId).child("totalDistance").setValue(rounded);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}