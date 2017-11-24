package com.codegreed_devs.pambio.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codegreed_devs.pambio.Models.HomeItems;
import com.codegreed_devs.pambio.R;

import java.util.List;

/**
 * Created by Augustine on 10/13/2017.
 */

public class HomeItemsAdapter extends ArrayAdapter<HomeItems> {
    public HomeItemsAdapter(Context context, int resource, List<HomeItems> objects) {
        super(context, resource, objects);

    }
    @NonNull
    @Override

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {



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


        assert myitm != null;
        final String my_title=myitm.title;


        switch (my_title) {
            case "Our Mission":
                image.setImageResource(R.drawable.mission);
                title.setText(myitm.title);
                desc.setText(myitm.desc);


                break;
            case "Our Vision":
                image.setImageResource(R.drawable.vision);
                title.setText(myitm.title);
                desc.setText(myitm.desc);
                break;
            case "Our Core Values":
                image.setImageResource(R.drawable.core_values);
                title.setText(myitm.title);
                desc.setText(myitm.desc);

                break;
        }



        // Return the completed view to render on screen

        return convertView;


    }

}
