package com.codegreed_devs.pambio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by Augustine on 10/13/2017.
 */

public class AboutUsAdapter extends ArrayAdapter<AboutUsItems> {
    AboutUsAdapter(Context context, int resource, List<AboutUsItems> objects) {
        super(context, resource, objects);

    }
    @NonNull
    @Override

    public View getView(int position, View convertView, @NonNull ViewGroup parent) {



        // Get the data item for this position

        AboutUsItems myitm = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.about_single, parent, false);

        // Lookup view for data population

        TextView title = convertView.findViewById(R.id.abt_title);
        TextView desc=convertView.findViewById(R.id.abt_desc);



        // Populate the data into the template view using the data object


        assert myitm != null;
        final String my_title=myitm.title;

        switch (my_title) {
            case "Our History":

                title.setText(myitm.title);
                desc.setText(myitm.desc);


                break;
            case "Our Mission":

                title.setText(myitm.title);
                desc.setText(myitm.desc);
                break;
            case "Our Vision":

                title.setText(myitm.title);
                desc.setText(myitm.desc);
                break;
        }



        // Return the completed view to render on screen

        return convertView;


    }
}
