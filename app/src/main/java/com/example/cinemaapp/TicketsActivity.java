package com.example.cinemaapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        queryData();
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
