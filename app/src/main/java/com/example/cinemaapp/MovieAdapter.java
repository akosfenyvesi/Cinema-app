package com.example.cinemaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements Filterable {
    private ArrayList<Movie> mMovieData = new ArrayList<>();
    private ArrayList<Movie> mMovieDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    MovieAdapter(Context mContext, ArrayList<Movie> movieData) {
        this.mMovieData = movieData;
        this.mMovieDataAll = movieData;
        this.mContext = mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder((LayoutInflater.from(mContext).inflate(R.layout.movie, parent, false)));
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        Movie currentMovie = mMovieData.get(position);

        holder.bindTo(currentMovie);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() { return mMovieData.size(); }

    @Override
    public Filter getFilter() { return movieFilter; }

    private Filter movieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Movie> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mMovieDataAll.size();
                results.values = mMovieDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Movie movie: mMovieDataAll) {
                    if (movie.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(movie);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mMovieData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mMovieTitle;
        private ImageView mMovieImage;
        private ImageView mRatingImage;
        private TextView mMovieGenreTextView;
        private TextView mMoviePlaytimeTextView;
        private TextView mMovieDescriptionTextView;
        private Spinner mHoursSpinner;
        private TextView mDatePickerTextView;
        private SharedPreferences preferences;

        public ViewHolder(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movieTitle);
            mMovieImage = itemView.findViewById(R.id.movieImage);
            mRatingImage = itemView.findViewById(R.id.ratingImage);
            mMovieGenreTextView = itemView.findViewById(R.id.movieGenreTextView);
            mMoviePlaytimeTextView = itemView.findViewById(R.id.moviePlaytimeTextView);
            mMovieDescriptionTextView = itemView.findViewById(R.id.movieDescriptionTextView);
//            mDatePickerTextView = itemView.findViewById(R.id.movieDatePickerTextView);
            preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String date = preferences.getString("date", "");

            mHoursSpinner = itemView.findViewById(R.id.chooseHourSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.hours_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mHoursSpinner.setAdapter(adapter);

            itemView.findViewById(R.id.choose_seats).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SeatSelectionActivity.class);
                    intent.putExtra("hour", mHoursSpinner.getSelectedItem().toString());
                    intent.putExtra("movie", mMovieTitle.getText().toString());
//                    intent.putExtra("date", mDatePickerTextView.getText());
                    mContext.startActivity(intent);
                }
            });
        }


        void bindTo(Movie currentMovie) {
            mMovieTitle.setText(currentMovie.getTitle());

            Glide.with(mContext).load(currentMovie.getMovieImageResource()).into(mMovieImage);
            Glide.with(mContext).load(currentMovie.getRatingImageResource()).into(mRatingImage);

            String[] hours = currentMovie.getScreenings().split(",");
            mHoursSpinner = itemView.findViewById(R.id.chooseHourSpinner);
            mHoursSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, hours));

            mMovieGenreTextView.setText(currentMovie.getGenre());
            mMoviePlaytimeTextView.setText(currentMovie.getPlayTime());
            mMovieDescriptionTextView.setText(currentMovie.getDescription());
        }
    }
}
