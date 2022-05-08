package com.example.cinemaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;

public class TicketsActivity extends AppCompatActivity {
    private static final String LOG_TAG = TicketsActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<Ticket> mTicketsData;
    private TicketsAdapter mAdapter;

    private FirebaseFirestore mFirtestore;
    private CollectionReference mTickets;

    private SharedPreferences preferences;

    private TextView noTickets;

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView mClosestCinema;
    int PERMISSION_ID = 44;

    private int requestCounter = 0;

    private Location cinemaLocation = new Location("Cinema");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerViewTickets);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, 1));

        mTicketsData = new ArrayList<>();
        mAdapter = new TicketsAdapter(this, mTicketsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirtestore = FirebaseFirestore.getInstance();
        mTickets = mFirtestore.collection("Tickets");

        mClosestCinema = findViewById(R.id.closestCinema);

        cinemaLocation.setLatitude(46.246909);
        cinemaLocation.setLongitude(20.147029);

        getLastLocation();
        queryData();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        mClosestCinema.setText("The cinema is in: " + (int) location.distanceTo(cinemaLocation));
                        System.out.println("The cinema is in: " + (int) location.distanceTo(cinemaLocation));
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            if (requestCounter < 10)
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            System.out.println("The cinema is in: " + (int) mLastLocation.distanceTo(cinemaLocation));
            mClosestCinema.setText("The cinema is in: " + (int) mLastLocation.distanceTo(cinemaLocation));
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        requestCounter++;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private void queryData() {
        mTicketsData.clear();

        mTickets.orderBy("when", Query.Direction.ASCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                Ticket ticket = document.toObject(Ticket.class);
                if (ticket.getUserId().equals(user.getUid())) {
                    ticket.setId(document.getId());
                    mTicketsData.add(ticket);
                }
            }

            if (mTicketsData.size() == 0) {
                noTickets = findViewById(R.id.noTickets);
                noTickets.setText("You don't have any tickets");
            }
//            ArrayList<String> forInit = new ArrayList<String>();
//            forInit.add("1 1");
//            mTickets.add(new Ticket("zTzGdMJQHGiNIt4QkGKM", "Ambulance", new Date(), forInit));

            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteTicket(Ticket ticket) {
        DocumentReference reference = mTickets.document(ticket._getId());

        reference.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Ticket is successfully deleted: " + ticket._getId());
            Toast.makeText(this, "Ticket successfully deleted", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Ticket " + ticket._getId() + " cannot be deleted", Toast.LENGTH_LONG).show();
        });

        queryData();
    }

    public void editTicket(Ticket ticket, String selectedVal) {
        DocumentReference reference = mTickets.document(ticket._getId());

        Date updateDate = ticket.getWhen();
        updateDate.setHours(Integer.parseInt(selectedVal.split(":")[0]));
        updateDate.setMinutes(Integer.parseInt(selectedVal.split(":")[1]));
        ticket.setWhen(updateDate);

        reference.set(ticket).addOnSuccessListener(unused -> Toast.makeText(TicketsActivity.this, "Ticket has been updated..", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(TicketsActivity.this, "Ticket update failed" + e, Toast.LENGTH_SHORT).show());

        queryData();
    }
}
