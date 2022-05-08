package com.example.cinemaapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Movie {
    private String id;
    private String title;
    private int movieImageResource;
    private String description;
    private String genre;
    private int ratingImageResource;
    private String playTime;
    private String screenings;

    public Movie() {
    }

//    public Movie(String title, int movieImageResource, String description, String genre, int rating, String playTime) {
//        this.title = title;
//        this.movieImageResource = movieImageResource;
//        this.description = description;
//        this.genre = genre;
//        this.ratingImageResource = rating;
//        this.playTime = playTime;
//    }

    public Movie(String title, int movieImageResource, String description, String genre, int rating, String playTime, String screenings) {
        this.title = title;
        this.movieImageResource = movieImageResource;
        this.description = description;
        this.genre = genre;
        this.ratingImageResource = rating;
        this.playTime = playTime;
        this.screenings = screenings;
    }

    public String _getId() { return id; }
    public void setId(String id) { this.id = id;}
    public String getTitle() { return title; }
    public int getMovieImageResource() { return movieImageResource; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public int getRatingImageResource() { return ratingImageResource; }
    public String getPlayTime() { return playTime; }
    public String getScreenings() { return screenings; }
}
