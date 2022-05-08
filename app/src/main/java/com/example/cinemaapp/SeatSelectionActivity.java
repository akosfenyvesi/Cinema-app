package com.example.cinemaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = MainActivity.class.getName();

    ViewGroup layout;

    private FirebaseFirestore mFirtestore;
    private FirebaseUser user;
    private CollectionReference mTickets;

    List<TextView> seatViewList = new ArrayList<>();
    /*
    A - available
    U - unavailable
    S - selected
    _ - aisle
     */

    int seatSize = 100;
    int seatGaping = 10;

    static final int STATUS_AVAILABLE = 1;
    static final int STATUS_UNAVAILABLE = 2;
    static final int STATUS_SELECTED = 3;

    ArrayList<ArrayList<String>> seatsArrayList = new ArrayList<ArrayList<String>>();
    ArrayList<String> finalSelection = new ArrayList<>();
    int rows = 10;
    int seats = 11;

    Bundle extras;

    private SharedPreferences preferences;
    private String date;

    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        layout = findViewById(R.id.layoutSeat);

        mFirtestore = FirebaseFirestore.getInstance();
        mTickets = mFirtestore.collection("Tickets");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString("date", "");


        mNotificationHandler = new NotificationHandler(this);

        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);
        layout.addView(layoutSeat);

        LinearLayout layout = null;

        extras = getIntent().getExtras();

        ArrayList<String> options = new ArrayList<String>();
        options.add("A");
        options.add("U");

        for (int i = 0; i < rows; i++) {
            ArrayList<String> temp = new ArrayList<String>();
            for (int j = 0; j < seats; j++) {
                if (j == seats / 2) {
                    temp.add("_");
                } else {
                    Collections.shuffle(options);
                    temp.add(options.get(0));
                }
            }
            seatsArrayList.add(temp);
        }

        int count = 0;

        for (int i = 0; i < seatsArrayList.size(); i++) {
            layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layoutSeat.addView(layout);
            for (int j = 0; j < seatsArrayList.get(i).size(); j++) {
                switch (seatsArrayList.get(i).get(j)) {
                    case "A":
                        count++;
                        TextView viewAvailable = new TextView(this);
                        LinearLayout.LayoutParams layoutParamsAvailable = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParamsAvailable.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        viewAvailable.setLayoutParams(layoutParamsAvailable);
                        viewAvailable.setPadding(0, 0, 0, 2 * seatGaping);
                        viewAvailable.setId(count);
                        viewAvailable.setGravity(Gravity.CENTER);
                        viewAvailable.setBackgroundResource(R.drawable.ic_seats_book);
                        viewAvailable.setTag(STATUS_AVAILABLE);
                        viewAvailable.setTextColor(Color.BLACK);
                        viewAvailable.setText((count % (seats - 1) != 0 ? count % (seats - 1) : seats - 1) + "");
                        viewAvailable.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        layout.addView(viewAvailable);
                        seatViewList.add(viewAvailable);
                        viewAvailable.setOnClickListener(this);
                        break;
                    case "U":
                        count++;
                        TextView viewUnavailable = new TextView(this);
                        LinearLayout.LayoutParams layoutParamsUnavailable = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParamsUnavailable.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        viewUnavailable.setLayoutParams(layoutParamsUnavailable);
                        viewUnavailable.setPadding(0, 0, 0, 2 * seatGaping);
                        viewUnavailable.setId(count);
                        viewUnavailable.setGravity(Gravity.CENTER);
                        viewUnavailable.setBackgroundResource(R.drawable.ic_seats_booked);
                        viewUnavailable.setTag(STATUS_UNAVAILABLE);
                        viewUnavailable.setTextColor(Color.WHITE);
                        viewUnavailable.setText((count % (seats - 1) != 0 ? count % (seats - 1) : seats - 1) + "");
                        viewUnavailable.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        layout.addView(viewUnavailable);
                        seatViewList.add(viewUnavailable);
                        viewUnavailable.setOnClickListener(this);
                        break;
                    case "S":
                        count++;
                        TextView viewSelected = new TextView(this);
                        LinearLayout.LayoutParams layoutParamsSelected = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParamsSelected.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        viewSelected.setLayoutParams(layoutParamsSelected);
                        viewSelected.setPadding(0, 0, 0, 2 * seatGaping);
                        viewSelected.setId(count);
                        viewSelected.setGravity(Gravity.CENTER);
                        viewSelected.setBackgroundResource(R.drawable.ic_seats_reserved);
                        viewSelected.setTag(STATUS_SELECTED);
                        viewSelected.setTextColor(Color.WHITE);
                        viewSelected.setText((count % (seats - 1) != 0 ? count % (seats - 1) : seats - 1) + "");
                        viewSelected.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                        layout.addView(viewSelected);
                        seatViewList.add(viewSelected);
                        viewSelected.setOnClickListener(this);
                        break;
                    case "_":
                        TextView view = new TextView(this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                        layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                        view.setLayoutParams(layoutParams);
                        view.setBackgroundColor(Color.TRANSPARENT);
                        view.setText("");
                        layout.addView(view);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId() % (seats - 1) != 0 ? view.getId() / (seats - 1) : view.getId() / (seats - 1) - 1;
        int j = ((view.getId() % (seats - 1)) != 0 ? (view.getId() % (seats - 1) - 1) : seats - 2);
        if ((int)view.getTag() == STATUS_AVAILABLE) {
            view.setBackgroundResource(R.drawable.ic_seats_reserved);
            view.setTag(STATUS_SELECTED);
            seatsArrayList.get(i).set(j, "S");
            System.out.println(i + " " + j);
        } else if ((int)view.getTag() == STATUS_SELECTED) {
            view.setBackgroundResource(R.drawable.ic_seats_book);
            view.setTag(STATUS_AVAILABLE);
            seatsArrayList.get(i).set(j, "A");
        }
    }

    public void book(View view) {
        String uid = user.getUid();
        String title = extras.getString("movie");
        String hour = extras.getString("hour");
        String[] hours = hour.split(":");
        Date dateParse = new Date();

        try {
            dateParse = new SimpleDateFormat("dd/MM/yyy").parse(date);
            dateParse.setHours(Integer.parseInt(hours[0]));
            dateParse.setMinutes(Integer.parseInt(hours[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < seats; j++) {
                if (seatsArrayList.get(i).get(j).equals("S")) {
                    finalSelection.add(i + " " + j);
                }
            }
        }

        mTickets.add(new Ticket(uid, title, dateParse, finalSelection)).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Successful booking!", Toast.LENGTH_LONG).show();
            mNotificationHandler.send("Successful booking for " + title + ". See you soon!");
            Intent intent = new Intent(this, TicketsActivity.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to book: " + e, Toast.LENGTH_LONG).show();
        });
    }
}