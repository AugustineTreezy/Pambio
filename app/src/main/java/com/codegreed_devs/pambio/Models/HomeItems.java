package com.codegreed_devs.pambio.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Augustine on 10/13/2017.
 */

public class HomeItems  {
    public String title;
    public String desc;
    private HomeItems(JSONObject object){
        try {
            this.title=object.getString("title");
            this.desc=object.getString("desc");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<HomeItems> fromJson(JSONArray jsonArray){
        ArrayList<HomeItems> homeitems = new ArrayList<>();

        for (int i = 0; i <jsonArray.length(); i++) {

            try {

                homeitems.add(new HomeItems(jsonArray.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
        return homeitems;
    }
}
