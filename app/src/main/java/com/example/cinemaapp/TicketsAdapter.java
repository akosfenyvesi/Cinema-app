package com.example.cinemaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {
    private ArrayList<Ticket> mTicketData = new ArrayList<>();
    private ArrayList<Ticket> mTicketDataAll = new ArrayList<>();
    private Context mContext;

    TicketsAdapter(Context context, ArrayList<Ticket> ticketData) {
        this.mTicketData = ticketData;
        this.mTicketDataAll = ticketData;
        this.mContext = context;
    }

    @Override
    public TicketsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.my_tickets, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ticket currentTicket = mTicketData.get(position);

        holder.bindTo(currentTicket);
    }

    @Override
    public int getItemCount() {
        return mTicketData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mMovieTitle;
        private TextView mWhen;
        private TextView mSeats;
        private Spinner mHoursSpinner;
        public String selectedVal;

        ViewHolder(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.movieTitleTicket);
            mWhen = itemView.findViewById(R.id.movieWhenTicket);
            mSeats = itemView.findViewById(R.id.seatsTicket);
        }

        void bindTo(Ticket currentTicket) {
            mMovieTitle.setText(currentTicket.getMovieTitle());
            mWhen.setText(currentTicket.getWhen().toString());

            mHoursSpinner = itemView.findViewById(R.id.hoursSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.hours_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            String[] hours = {"11:15", "12:30"};
            selectedVal = hours[0];
            mHoursSpinner.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, hours));

            mHoursSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVal = mHoursSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            StringBuilder seats = new StringBuilder();
            for (String seat: currentTicket.getSeats()) {
                String row = seat.split(" ")[0];
                String col = seat.split(" ")[1];
                seats.append(Integer.parseInt(row) + 1).append(". row, ").append(Integer.parseInt(col) + 1).append(". seat.\n");
            }
            mSeats.setText(seats);

            itemView.findViewById(R.id.deleteTicket).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TicketsActivity)mContext).deleteTicket(currentTicket);
                }
            });

            itemView.findViewById(R.id.setTicket).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TicketsActivity)mContext).editTicket(currentTicket, mHoursSpinner.getSelectedItem().toString());
                }
            });
        }
    }
}
