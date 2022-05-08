package com.example.cinemaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MovieSelectionActivity extends AppCompatActivity {
    private static final String LOG_TAG = MovieSelectionActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<Movie> mMovieList;
    private MovieAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mMovies;

    private int gridNumber = 1;

    private boolean viewRow = true;

    DatePickerDialog datePickerDialog;
    TextView datePickerTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_selection);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        Date today = new Date();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        datePickerTextView = findViewById(R.id.movieDatePickerTextView);
        datePickerTextView.setText(today.getDate() + "/" + (today.getMonth() + 1) + "/" + today.getYear());
        datePickerTextView.setInputType(InputType.TYPE_NULL);
        datePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(MovieSelectionActivity.this,
                        (view, year1, month1, dayOfMonth) -> {
                            datePickerTextView.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                            editor.putString("date", datePickerTextView.getText().toString());
                            editor.commit();
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mMovieList = new ArrayList<>();

        mAdapter = new MovieAdapter(this, mMovieList);

        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mMovies = mFirestore.collection("Movies");

        queryData();
    }

    private void queryData() {
        mMovieList.clear();

        // mMovies.whereEqualTo()...

        mMovies.orderBy("title").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Movie movie = document.toObject(Movie.class);
                mMovieList.add(movie);
            }

            if (mMovieList.size() == 0) {
                initalizeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    private void initalizeData() {
        String[] movieList = getResources().getStringArray(R.array.movie_titles);
        TypedArray movieImageResource = getResources().obtainTypedArray(R.array.movie_images);
        String[] movieDescription = getResources().getStringArray(R.array.movie_descriptions);
        String[] movieGenre = getResources().getStringArray(R.array.movie_genres);
        TypedArray ratingImageResource = getResources().obtainTypedArray(R.array.movie_ratings);
        String[] moviePlayTime = getResources().getStringArray(R.array.movie_platimes);
        String[] screenings = getResources().getStringArray(R.array.hours_array);

//        mMovieList.clear();

        for (int i = 0; i < movieList.length; i++) {
            mMovies.add(new Movie(
                    movieList[i],
                    movieImageResource.getResourceId(i, 0),
                    movieDescription[i],
                    movieGenre[i],
                    ratingImageResource.getResourceId(i, 0),
                    moviePlayTime[i],
                    screenings[i]));
        }

        movieImageResource.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cinema_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tickets_button:
                Log.d(LOG_TAG, "My Tickets clicked!");
                Intent intent = new Intent(this, TicketsActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}