package com.example.presensipegawai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.presensipegawai.adapter.HistoryAdapter;
import com.example.presensipegawai.helper.Api;
import com.example.presensipegawai.models.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type History activity.
 * Activity History digunakan untuk menampilkan riwayat presensi dari user.
 */
public class HistoryActivity extends AppCompatActivity {

    /**
     * The Spin month.
     * spinMonth adalah inisialisasi variable dari activity untuk dropdown filter berdasarkan bulan.
     */
    Spinner spinMonth,
    /**
     * The Spin year.
     * spinYear adalah inisialisasi variable dari activity untuk dropdown filter berdasarkan tahun.
     */
    spinYear;
    /**
     * The List view history.
     * listViewHistory adalah inisialisasi variable dari activity untuk menampilkan daftar riwayat presensi.
     */
    ListView listViewHistory;
    /**
     * The Tv total jam.
     * tvTotalJam adalah inisialisasi variable dari activity untuk menampilkan total / jumlah akumulasi presensi masuk dari user selama sebulan.
     */
    TextView tvTotalJam;

    private static String url_monthYear = Api.url+"/presensi/getMonthYear.php";
    private static String url_history = Api.url+"/presensi/getHistory.php";
    private static final String TAG = HistoryActivity.class.getSimpleName();

    /**
     * The Month.
     * Variable month digunakan untuk menyimpan daftar bulan yang digunakan untuk filter riwayat.
     */
    List<String> month = new ArrayList<String>();
    /**
     * The Year.
     * Variable year digunakan untuk menyimpan daftar tahun yang digunakan untuk filter riwayat.
     */
    List<String> year = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinMonth = findViewById(R.id.spinnerBulan);
        spinYear = findViewById(R.id.spinnerTahun);
        tvTotalJam = findViewById(R.id.tvTotalJam);
        listViewHistory = findViewById(R.id.listViewHistory);

        getMonthYear(); // Mendapatkan List Bulan dan Tahun yang digunakan pada menu dropdown Filter

        // Event Dropdown bulan saat diklik
        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getHistory();
                Toast.makeText(getApplicationContext(), spinMonth.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Event Dropdown tahun saat diklik
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getHistory();
                Toast.makeText(getApplicationContext(), spinYear.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Gets history.
     * Method getHistory digunakan untuk mendapatkan data riwayat presensi.
     */
    public void getHistory() {
        // Deklarasi variable ArrayList
        ArrayList<History> listHistory = new ArrayList<History>();

        HistoryAdapter adapter = new HistoryAdapter(this, listHistory);
        listViewHistory.setAdapter(adapter); // Set adapter ke ListView History

        SharedPreferences sp = this.getSharedPreferences("Session", this.MODE_APPEND);
        String idUser = sp.getString("id_user", "-");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_history, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                adapter.clear();
                Log.d(TAG, "Response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                        String id_presensi = jsonObject1.getString("id_presensi");
                        String id_user = jsonObject1.getString("id_user");
                        String datetime = jsonObject1.getString("datetime");
                        String time_in = jsonObject1.getString("time_in");
                        String time_out = jsonObject1.getString("time_out");
                        String location_in = jsonObject1.getString("location_in");
                        String location_out = jsonObject1.getString("location_out");
                        History newHistory = new History(id_presensi, id_user, datetime, time_in, time_out, location_in, location_out);
                        adapter.addAll(newHistory);
                    }
                    tvTotalJam.setText(jsonObject.getString("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user",idUser);
                params.put("month",spinMonth.getSelectedItem().toString());
                params.put("year",spinYear.getSelectedItem().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Gets month year.
     * Method getMonthYear digunakan untuk mendapatkan data bulan dan tahun untuk filter riwayat presensi.
     */
    public void getMonthYear() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_monthYear, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("month"));
                    for (int i=1; i <= jsonObject1.length(); i++) {
                        String name = jsonObject1.getString(String.valueOf(i));
                        month.add(name);
                    }
                    JSONArray jsonArray1 = jsonObject.getJSONArray("year");
                    for (int i=0; i < jsonArray1.length(); i++) {
                        String name = jsonArray1.getString(i);
                        year.add(name);
                    }
                    setAdapter();
                    getHistory();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Error : " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * Sets adapter.
     * Method setAdapter digunakan untuk mengeset adapter yang terdapat di layout dengan variable yang sudah di deklarasikan.
     */
    public void setAdapter() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, year);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(adapter2);
    }
}