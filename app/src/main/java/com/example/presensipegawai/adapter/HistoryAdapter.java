package com.example.presensipegawai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.presensipegawai.R;
import com.example.presensipegawai.models.History;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<History> {
    public HistoryAdapter(@NonNull Context context, ArrayList<History> resource) {
        super(context, 0, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        History history = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_history, parent, false);
        }
        TextView tvItemTanggal = convertView.findViewById(R.id.item_tanggal);
        TextView tvItemJamMasuk = convertView.findViewById(R.id.item_jam_masuk);
        TextView tvItemJamKeluar = convertView.findViewById(R.id.item_jam_keluar);
        tvItemTanggal.setText(history.datetime);
        tvItemJamMasuk.setText(history.time_in);
        tvItemJamKeluar.setText(history.time_out);

        return convertView;
    }
}