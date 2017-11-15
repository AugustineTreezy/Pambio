package com.codegreed_devs.pambio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Augustine on 10/13/2017.
 */

public class UpcomingEventsItems  {
    public Integer event_id;
    public String event_name;
    public String venue;
    public String date;
    public String time;
    public String desc;
    public String image;

    public UpcomingEventsItems(JSONObject object){
        try {
            this.event_id=object.getInt("id");
            this.event_name=object.getString("event_name");
            this.venue=object.getString("venue");
            this.date=object.getString("date");
            this.time=object.getString("time");
            this.desc=object.getString("description");
            this.image=object.getString("image");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<UpcomingEventsItems> fromJson(JSONArray jsonArray){
        ArrayList<UpcomingEventsItems> upcoming_events_items = new ArrayList<>();

        for (int i = 0; i <jsonArray.length(); i++) {

            try {

                upcoming_events_items.add(new UpcomingEventsItems(jsonArray.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
        return upcoming_events_items;
    }
}
