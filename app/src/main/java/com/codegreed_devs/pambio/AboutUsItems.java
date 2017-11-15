package com.codegreed_devs.pambio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Augustine on 10/13/2017.
 */

public class AboutUsItems  {
    public String title;
    public String desc;
    public AboutUsItems(JSONObject object){
        try {
            this.title=object.getString("title");
            this.desc=object.getString("desc");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<AboutUsItems> fromJson(JSONArray jsonArray){
        ArrayList<AboutUsItems> aboutusitems = new ArrayList<>();

        for (int i = 0; i <jsonArray.length(); i++) {

            try {

                aboutusitems.add(new AboutUsItems(jsonArray.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
        return aboutusitems;
    }
}
