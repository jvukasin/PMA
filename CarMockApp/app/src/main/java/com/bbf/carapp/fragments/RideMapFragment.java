package com.bbf.carapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bbf.carapp.MainActivity;
import com.bbf.carapp.R;
import com.bbf.carapp.activities.RideActivity;
import com.bbf.carapp.model.LocationObject;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RideMapFragment extends Fragment implements OnMapReadyCallback {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private LocationManager locationManager;
    private String provider;
    private SupportMapFragment mMapFragment;
    private AlertDialog dialog;
    private Marker home, car;
    private GoogleMap map;
    private DatabaseReference carReference;
    private DatabaseReference rentReference;
    private LatLng startLocation;
    private LatLng lastLocation;
    private List<LatLng> route = new ArrayList<>();
    private static double sum = 0;

    private static long UPDATE_INTERVAL = 1200;
    private static long FASTEST_INTERVAL = 1200;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private String plates;
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    private ValueEventListener rentFinishedListener;

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
                updateRent(locationResult.getLastLocation());
                route.add(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
                addCarMarker(locationResult.getLastLocation());
                updateDistance();
            }
        };
        carReference = FirebaseDatabase.getInstance().getReference("cars");
    }

    private void updateRent(Location lastLocation) {
        rentReference.child(plates).child("location").setValue(new LocationObject(lastLocation.getLatitude(), lastLocation.getLongitude()));
    }

    @Override
    public void onStart() {
        super.onStart();
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
         //   dialog = new LocationDialog(getActivity()).prepareDialog();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.ride_map_layout, vg, false);
        startLocation = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
        plates = getArguments().getString("plates");

        rentReference = FirebaseDatabase.getInstance().getReference("Rent");
        rentReference.child(plates).child("active").setValue("active");
        rentFinishedListener = rentReference.child(plates).child("active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String active = dataSnapshot.getValue(String.class);
                if(active.equals("finished")){
                    updateCar(lastLocation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


        //addMarker(startLocation);

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


    private void addCarMarker(Location lastLocation) {
        LatLng loc = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        if(car != null){
            car.remove();
        }
        int height = 100;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.car_map_pin);
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

    private void updateDistance(){
        if(route.size() >= 2){
            LatLng pos_prev = route.get(route.size()-2);
            LatLng pos_curr = route.get(route.size()-1);
            double distance = calculateDistance(pos_prev.latitude, pos_prev.longitude, pos_curr.latitude, pos_curr.longitude);
            sum += Math.round(distance * 10.0) / 10.0;
            lastLocation = route.get(route.size()-1);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     *
     * Rad sa lokacja izuzetno trosi bateriju.Obavezno osloboditi kada vise ne koristmo
     * */
    @Override
    public void onPause() {
        super.onPause();
        rentReference.removeEventListener(rentFinishedListener);
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    private void updateCar(LatLng lastLocation) {
        carReference.child(plates).child("occupied").setValue(false);
        carReference.child(plates).child("location").setValue(new LocationObject(lastLocation.latitude, lastLocation.longitude));

        carReference.child(plates).child("no_of_rides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer count = dataSnapshot.getValue(Integer.class);
                carReference.child(plates).child("no_of_rides").setValue(count+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        carReference.child(plates).child("fuel_distance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double fuelDistance = dataSnapshot.getValue(Double.class);
                if(fuelDistance == 0){
                    return;
                }
                DecimalFormat df = new DecimalFormat("#.#");
                double temp = fuelDistance - sum;
                double rounded = Double.valueOf(df.format(temp));
                carReference.child(plates).child("fuel_distance").setValue(rounded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        carReference.child(plates).child("mileage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double mileage = dataSnapshot.getValue(Double.class);
                double temp = mileage + sum;
                DecimalFormat df = new DecimalFormat("#.#");
                double rounded = Double.valueOf(df.format(temp));
                carReference.child(plates).child("mileage").setValue(rounded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static double calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (AVERAGE_RADIUS_OF_EARTH * c);

    }

}