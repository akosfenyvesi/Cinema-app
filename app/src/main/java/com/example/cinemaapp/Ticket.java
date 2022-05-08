package com.example.cinemaapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Ticket {
    private String id;
    private String userId;
    private String movieTitle;
    private Date when;
    private ArrayList<String> seats;

    public Ticket() {
    }

    public Ticket(String userId, String movieTitle, Date when, ArrayList<String> seats) {
        this.userId = userId;
        this.movieTitle = movieTitle;
        this.when = when;
        this.seats = seats;
    }

    public String _getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieTitle() { return movieTitle; }

    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public Date getWhen() { return when; }

    public void setWhen(Date when) { this.when = when; }

    public ArrayList<String> getSeats() { return seats; }

    public void setSeats(ArrayList<String> seats) { this.seats = seats; }
}
