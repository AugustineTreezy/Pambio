package com.codegreed_devs.pambio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Augustine on 10/13/2017.
 */

public class UpcomingEventsAdapter extends ArrayAdapter<UpcomingEventsItems> {
    public UpcomingEventsAdapter(Context context, int resource, List<UpcomingEventsItems> objects) {
        super(context, resource, objects);

    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        UpcomingEventsItems event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_single, parent, false);

        }

        // Lookup view for data population

        TextView event_name = convertView.findViewById(R.id.event_name);
        TextView event_time=convertView.findViewById(R.id.event_time);
        TextView event_venue=convertView.findViewById(R.id.event_venue);
        TextView event_date=convertView.findViewById(R.id.event_date);



        // Populate the data into the template view using the data object
        event_name.setText(event.event_name);
        event_time.setText(event.time);
        event_date.setText(event.date);
        event_venue.setText(event.venue);




        // Return the completed view to render on screen


        return convertView;


    }
}
