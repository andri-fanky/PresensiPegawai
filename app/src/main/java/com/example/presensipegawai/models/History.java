package com.example.presensipegawai.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class History {
    public String id_presensi;
    public String id_user;
    public String datetime;
    public String time_in;
    public String time_out;
    public String location_in;
    public String location_out;

    public History(String id_presensi, String id_user, String datetime, String time_in, String time_out, String location_in, String location_out) {
        this.id_presensi = id_presensi;
        this.id_user = id_user;
        this.datetime = datetime;
        this.time_in = time_in;
        this.time_out = time_out;
        this.location_in = location_in;
        this.location_out = location_out;
    }

//    public History(JSONObject object){
//        try {
//            this.id_presensi = object.getString("id_presensi");
//            this.id_user = object.getString("id_user");
//            this.datetime = object.getString("datetime");
//            this.time_in = object.getString("time_in");
//            this.time_out = object.getString("time_out");
//            this.location_in = object.getString("location_in");
//            this.location_out = object.getString("location_out");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static ArrayList<History> fromJson(JSONArray jsonObjects) {
//        ArrayList<History> history = new ArrayList<History>();
//        for (int i = 0; i < jsonObjects.length(); i++) {
//            try {
//                history.add(new History(jsonObjects.getJSONObject(i)));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return history;
//    }
}