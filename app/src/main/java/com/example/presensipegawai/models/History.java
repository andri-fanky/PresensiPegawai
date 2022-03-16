package com.example.presensipegawai.models;

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
}