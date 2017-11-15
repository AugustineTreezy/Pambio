package com.codegreed_devs.pambio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Augustine on 10/13/2017.
 */

public class HomeItemsAdapter extends ArrayAdapter<HomeItems> {
    public HomeItemsAdapter(Context context, int resource, List<HomeItems> objects) {
        super(context, resource, objects);

    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {



        // Get the data item for this position

        HomeItems myitm = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_single, parent, false);

        }

        // Lookup view for data population

        TextView title = convertView.findViewById(R.id.title);
        ImageView image=convertView.findViewById(R.id.image);
        TextView desc=convertView.findViewById(R.id.desc);



        // Populate the data into the template view using the data object


        final String my_title=myitm.title;


        if (my_title.equals("Our Mission")){
            image.setImageResource(R.drawable.mission);
            title.setText(myitm.title);
            desc.setText(myitm.desc);


        }else if(my_title.equals("Our Vision")){
            image.setImageResource(R.drawable.vision);
            title.setText(myitm.title);
            desc.setText(myitm.desc);
        }else if(my_title.equals("Our Core Values")){
            image.setImageResource(R.drawable.core_values);
            title.setText(myitm.title);
            desc.setText(myitm.desc);

        }



        // Return the completed view to render on screen

        return convertView;


    }

}
