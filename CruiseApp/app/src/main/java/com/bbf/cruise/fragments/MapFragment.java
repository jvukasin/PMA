package com.bbf.cruise.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.Car;
import model.CarItem;

import static android.content.Context.MODE_PRIVATE;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private LocationManager locationManager;
    private String provider;
    private SupportMapFragment mMapFragment;
    private AlertDialog dialog;
    private Marker home;
    private GoogleMap map;
    private DatabaseReference databaseReference;
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<Marker> carMarkers = new ArrayList<>();
    private Button centreButton;
    private Button nearbyCarsButton;
    private Button heartButton;
    private Button refreshButton;
    private Dialog mDialog;
    private SharedPreferences sharedPreferences;
    private TextView name, model, carRating, carPlate, carRides, carFuel, carDistance;

    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    private static long UPDATE_INTERVAL = 500;
    private static long FASTEST_INTERVAL = 500;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private DatabaseReference referencePlates;
    private DatabaseReference referenceCars;
    private DatabaseReference referenceReservations;
    private ArrayList<Car> favoriteCars = new ArrayList<>();
    private static boolean result = false;

    public static MapFragment newInstance() {

        MapFragment mpf = new MapFragment();

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
        databaseReference = FirebaseDatabase.getInstance().getReference("cars");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referencePlates = FirebaseDatabase.getInstance().getReference("Favorites").child(firebaseUser.getUid());
        referenceCars = FirebaseDatabase.getInstance().getReference("cars");
        referenceReservations = FirebaseDatabase.getInstance().getReference("Reservations");

        mDialog = new Dialog(getActivity());
        initializeCarInfoDialog();
        sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                if (home != null) {
                    home.remove();
                }
                LatLng loc = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                home = map.addMarker(new MarkerOptions()
                        .title("YOUR_POSITION")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(loc));
                home.setFlat(true);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cars.clear();
                        for(DataSnapshot carSnapshot: dataSnapshot.getChildren()){
                            cars.add(carSnapshot.getValue(Car.class));
                        }
                        if(heartButton.getVisibility() != View.INVISIBLE){
                            positionCarMarkers();
                        }else{
                            positionFavorites();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cars.clear();
                for(DataSnapshot carSnapshot: dataSnapshot.getChildren()){
                    cars.add(carSnapshot.getValue(Car.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeCarInfoDialog() {
        mDialog.setContentView(R.layout.map_marker_dialog);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        name = (TextView) mDialog.findViewById(R.id.carName);
        model = (TextView) mDialog.findViewById(R.id.carModel);
        carRating = (TextView) mDialog.findViewById(R.id.carRatingDialog);
        carPlate = (TextView) mDialog.findViewById(R.id.carPlate);
        carRides = (TextView) mDialog.findViewById(R.id.carRides);
        carFuel = (TextView) mDialog.findViewById(R.id.carFuel);
        carDistance = (TextView) mDialog.findViewById(R.id.carDistance);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
        transaction.replace(R.id.map_container, mMapFragment).commit();

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

        referencePlates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteCars.clear();
                for(DataSnapshot carSnapshot: dataSnapshot.getChildren()) {
                    String plts = carSnapshot.getKey();
                    referenceCars.child(plts).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Car car = dataSnapshot.getValue(Car.class);
                            favoriteCars.add(car);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.map_layout, vg, false);
        centreButton = view.findViewById(R.id.centreButton);
        nearbyCarsButton = view.findViewById(R.id.mapButton);
        heartButton = view.findViewById(R.id.heartButton);
        refreshButton = view.findViewById(R.id.refreshButton);
        centreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(home.getPosition()).zoom(14).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        nearbyCarsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearbyCarsActivity.class);
                intent.putExtra("myLat", home.getPosition().latitude);
                intent.putExtra("myLng", home.getPosition().longitude);
                startActivity(intent);
            }
        });


        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sharedPreferences.getString("reservation", "none").equals("none")) {
                    Toast.makeText(getActivity(), "Could not position favourite cars, reservation is in progress.", Toast.LENGTH_LONG);
                    return;
                }

                map.clear();
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            if (home != null) {
                                home.remove();
                            }
                            home = map.addMarker(new MarkerOptions()
                                    .title("YOUR_POSITION")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                    .position(new LatLng(location.getLatitude(), location.getLongitude())));
                            home.setFlat(true);
                        }
                    }
                });
                positionFavorites();
                heartButton.setVisibility(View.INVISIBLE);
                refreshButton.setVisibility(View.VISIBLE);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            if (home != null) {
                                home.remove();
                            }
                            home = map.addMarker(new MarkerOptions()
                                    .title("YOUR_POSITION")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                    .position(new LatLng(location.getLatitude(), location.getLongitude())));
                            home.setFlat(true);
                        }
                    }
                });
                positionCarMarkers();
                refreshButton.setVisibility(View.INVISIBLE);
                heartButton.setVisibility(View.VISIBLE);
            }
        });

        refreshButton.setVisibility(View.INVISIBLE);

        return view;
    }

    /**
     * Svaki put kada uredjaj dobije novu informaciju o lokaciji ova metoda se poziva
     * i prosledjuje joj se nova informacija o kordinatamad
     * */
    @Override
    public void onLocationChanged(Location location) {
//        if (map != null) {
//            addMarker(location);
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
        Location myLocation = null;

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps && !wifi) {
            showLocatonDialog();
        } else {

            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //Request myLocation updates:
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                addMarker(location);
                                Toast.makeText(getActivity(), "Finding cars...", Toast.LENGTH_SHORT).show();
                                positionCarMarkers();
                            }
                        }
                    });
                } else if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //Request myLocation updates:
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                addMarker(location);
                                Toast.makeText(getActivity(), "Finding cars...", Toast.LENGTH_SHORT).show();
                                positionCarMarkers();
                            }
                        }
                    });
                }
            }

            // ako zelimo da rucno postavljamo markere to radimo
            // dodavajuci click listener
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    map.addMarker(new MarkerOptions()
                            .title("YOUR_POSITION")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng))
                            .setFlat(true);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(14).build();

                    map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

            // ako zelmo da reagujemo na klik markera koristimo marker click listener
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if(marker.getTitle().equals("YOUR_POSITION")) {
                        Toast.makeText(getActivity(), "You are here", Toast.LENGTH_SHORT).show();
                    } else {
                        getCarDialog(getActivity(), marker.getTitle());
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
    }

    private void getCarDialog(final FragmentActivity activity, final String markerTitle) {
        carPlate.setText(markerTitle);

        Car car = new Car();
        for(Car c : cars) {
            if(c.getReg_number().equals(markerTitle)) {
                car = c;
            }
        }

        name.setText(car.getBrand());
        model.setText(car.getModel());
        carRating.setText(Double.toString(car.getRating()));
        carRides.setText(Integer.toString(car.getNo_of_rides()));
        carFuel.setText(Double.toString(car.getFuel_distance()).concat(" km"));
        DecimalFormat df = new DecimalFormat("#.#");
        String check = df.format(calculateDistance(home.getPosition().latitude, home.getPosition().longitude, car.getLocation().getLatitude(), car.getLocation().getLongitude()));
        final double dist;
        if(check.contains(",")) {
            check = check.replace(',','.');
            dist = Double.parseDouble(check);
        } else {
            dist = Double.parseDouble(check);
        }
        carDistance.setText(String.valueOf(dist).concat(" km"));

        Button viewBtn = (Button) mDialog.findViewById(R.id.markerViewBtn);
        final Car finalCar = car;
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CarDetailActivity.class);
                intent.putExtra("name", finalCar.getBrand() + " " + finalCar.getModel());
                intent.putExtra("mileage", String.format("%.1f", finalCar.getMileage()));
                intent.putExtra("distance_from_me", dist);
                intent.putExtra("fuel_distance", Double.toString(finalCar.getFuel_distance()));
                intent.putExtra("plate", markerTitle);
                intent.putExtra("no_of_rides", finalCar.getNo_of_rides());
                intent.putExtra("rating", finalCar.getRating());
                intent.putExtra("tp_fl", finalCar.getTp_fl());
                intent.putExtra("tp_fr", finalCar.getTp_fr());
                intent.putExtra("tp_rl", finalCar.getTp_rl());
                intent.putExtra("tp_rr", finalCar.getTp_rr());
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void addMarker(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

        if (home != null) {
            home.remove();
        }

        home = map.addMarker(new MarkerOptions()
                .title("YOUR_POSITION")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .position(loc));
        home.setFlat(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc).zoom(14).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void positionCarMarkers(){
        clearCarMarkers();

        if(!sharedPreferences.getString("reservation", "none").equals("none")) {
            String plates = sharedPreferences.getString("reservation", "none");
            for(Car car: cars){
                if (car.getReg_number().equals(plates)) {
                    addCarMarker(car);
                    break;
                }
            }
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        int radius = sharedPreferences.getInt("radius", 30);
        for(Car car: cars){
            if(car.isOccupied() && MapFragment.isReservedByMe(referenceReservations,car.getReg_number(), firebaseUser)){
                double distance = Math.round(MapFragment.calculateDistance(home.getPosition().latitude, home.getPosition().longitude,
                        car.getLocation().getLatitude(), car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    addCarMarker(car);
                }
            }else if(car.isOccupied() && !MapFragment.isReservedByMe(referenceReservations, car.getReg_number(), firebaseUser)){
                continue;
            }else{
                double distance = Math.round(MapFragment.calculateDistance(home.getPosition().latitude, home.getPosition().longitude, car.getLocation().getLatitude(),
                        car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    addCarMarker(car);
                }
            }
        }
    }

    private void positionFavorites() {
        clearCarMarkers();

        int radius = sharedPreferences.getInt("radius", 30);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        for(Car car: favoriteCars){
            if(car.isOccupied() && isReservedByMe(referenceReservations,car.getReg_number(), firebaseUser)){
                double distance = Math.round(MapFragment.calculateDistance(home.getPosition().latitude, home.getPosition().longitude,
                        car.getLocation().getLatitude(), car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    addCarMarker(car);
                }
            }else if(car.isOccupied() && !isReservedByMe(referenceReservations, car.getReg_number(), firebaseUser)){
                continue;
            }else{
                double distance = Math.round(MapFragment.calculateDistance(home.getPosition().latitude, home.getPosition().longitude, car.getLocation().getLatitude(),
                        car.getLocation().getLongitude()) * 10.0) / 10.0;
                if(distance <= radius){
                    addCarMarker(car);
                }
            }
        }
    }

    private void addCarMarker(Car car){
        LatLng loc = new LatLng(car.getLocation().getLatitude(), car.getLocation().getLongitude());
        int height = 100;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.car_map_pin_no_border_yellow);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        Marker marker = map.addMarker(new MarkerOptions()
                .title(car.getReg_number())
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .position(loc));
        marker.setFlat(true);
        carMarkers.add(marker);
    }

    private void clearCarMarkers(){
        for(Marker carMarker: carMarkers){
            carMarker.remove();
        }
    }


    /**
     *
     * Rad sa lokacja izuzetno trosi bateriju.Obavezno osloboditi kada vise ne koristmo
     * */
    @Override
    public void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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

    public static boolean isReservedByMe(DatabaseReference referenceReservations, String plates, final FirebaseUser firebaseUser){
        referenceReservations = referenceReservations.child(plates).child("user");
        referenceReservations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                String fId = firebaseUser.getUid();
                if(firebaseUser.getUid().equals(dataSnapshot.getValue(String.class))){
                    result = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return result;
    }

}