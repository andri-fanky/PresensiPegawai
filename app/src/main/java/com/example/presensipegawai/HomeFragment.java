package com.example.presensipegawai;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.presensipegawai.helper.Api;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tvNama, tvUnitKerja, tvJam, tvTanggal, tvStatus, tvTotal;
    Button btnMasuk, btnKeluar;
    String idUser;

    int status;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private static String url_lastPresence = Api.url+"/presensi/getLastPresence.php";
    private static String url_presensiMasuk = Api.url+"/presensi/setPresensiMasuk.php";
    private static String url_presensiKeluar = Api.url+"/presensi/setPresensiKeluar.php";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String TAG_STATUS = "status";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi variable
        tvNama = rootView.findViewById(R.id.tvNama);
        tvUnitKerja = rootView.findViewById(R.id.tvUnitKerja);
        tvJam = rootView.findViewById(R.id.tvJam);
        tvTanggal = rootView.findViewById(R.id.tvTanggal);
        tvStatus = rootView.findViewById(R.id.tvStatus);
        tvTotal = rootView.findViewById(R.id.tvTotal);
        btnMasuk = rootView.findViewById(R.id.btnMasuk);
        btnKeluar = rootView.findViewById(R.id.btnKeluar);

        // Inisialisasi LocationServices ke variable fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        clock(); // Memanggil method clock()

        // Deklarasi memanggil SharedPreferences
        SharedPreferences sp = this.getActivity().getSharedPreferences("Session", this.getActivity().MODE_APPEND);

        // Mengambil value dari SharedPreferences
        idUser = sp.getString("id_user", "");
        String nama = sp.getString("nama", "-");
        String unitKerja = sp.getString("nama_unit_kerja", "-");
        // Set value ke TextView
        tvNama.setText(nama);
        tvUnitKerja.setText(unitKerja);

        // Event saat button masuk diklik
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menampilkan Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_presensi_masuk);
                builder.setMessage(R.string.dialog_presensi_masuk)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            fetchLocation("Presensi Masuk");
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Event saat button keluar diklik
        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menampilkan Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_presensi_keluar);
                builder.setMessage(R.string.dialog_presensi_masuk)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            fetchLocation("Presensi Keluar");
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        getLastPresence(); // Mendapatkan data presensi terakhir

        return rootView;
    }

    private void clock() {
        final Handler hander = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        getTime(); // Memanggil method getTime()
                        clock();
                    }
                });
            }
        }).start();
    }

    void getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); // Mengambil Date Format
        tvJam.setText(dateFormat.format(new Date())); // Set value ke TextView Jam
    }

    public void getLastPresence() {
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_lastPresence, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    status = jsonObject.getInt(TAG_STATUS);
                    if (status == 1) {
                        // Tombol Masuk Aktif
                        btnMasuk.setVisibility(View.VISIBLE);
                        btnKeluar.setVisibility(View.GONE);
                    } else if (status == 2) {
                        // Tombol Keluar Aktif
                        btnMasuk.setVisibility(View.GONE);
                        btnKeluar.setVisibility(View.VISIBLE);
                    }
                    if (status == 1 || status == 2) {
                        tvTanggal.setText(jsonObject.getString("date"));
                        tvStatus.setText(jsonObject.getString("message"));
                        tvTotal.setText(jsonObject.getString("total"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Error : " + error.getMessage());
                Toast.makeText(getContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user",idUser);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void fetchLocation(String status) {
        if (ActivityCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    if (status.equals("Presensi Masuk")) {
                        presensiMasuk();
                    } else {
                        presensiKeluar();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal presensi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void presensiMasuk() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        boolean status = checkPosition(latLng);
        if (status) {
            RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_presensiMasuk, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response);
                    if (response.equals("true")) {
                        getLastPresence();
                        Toast.makeText(getContext(), "Berhasil presensi masuk", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Gagal presensi masuk", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"Error : " + error.getMessage());
                    Toast.makeText(getContext(), "Gagal presensi masuk", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_user",idUser);
                    params.put("location_in",latLng.latitude+","+latLng.longitude);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(this.getActivity(), "Anda di luar area jangkauan", Toast.LENGTH_SHORT).show();
        }
    }

    public void presensiKeluar() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        boolean status = checkPosition(latLng);
        if (status) {
            RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_presensiKeluar, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response);
                    if (response.equals("true")) {
                        getLastPresence();
                        Toast.makeText(getContext(), "Berhasil presensi keluar", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Gagal presensi keluar", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG,"Error : " + error.getMessage());
                    Toast.makeText(getContext(), "Gagal presensi keluar", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_user",idUser);
                    params.put("location_out",latLng.latitude+","+latLng.longitude);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(this.getActivity(), "Anda di luar area jangkauan", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPosition(LatLng position) {
        boolean isInside = false;
        double lat = position.latitude;
        double lng = position.longitude;
//        double pointLat[] = {
//            -7.8079055,-7.8081659,-7.8085007,-7.8085858,-7.8086921,-7.8089365,-7.8092023,-7.8095158,-7.8097444,-7.8100739,-7.8096487,-7.8095158,-7.8093777,-7.8092714,-7.8096115,-7.8093564,-7.8087665,-7.8086177,-7.8081712,-7.8077779,-7.8076982,-7.8079055
//        };
//        double pointLng[] = {
//            110.3205579,110.3210731,110.3217064,110.3220929,110.3224686,110.3226135,110.3225491,110.3224578,110.3224364,110.3224149,110.3216152,110.3212932,110.3210785,110.3207779,110.3206169,110.3200641,110.3198870,110.3198977,110.3199675,110.3200104,110.3200534,110.3205579
//        };
        double pointLat[] = {
            -7.8593113,-7.8593432,-7.8606929,-7.8606238,-7.8593113,
        };
        double pointLng[] = {
            110.3066631,110.3073877,110.3073608,110.3066202,110.3066631
        };
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0 ; i < pointLat.length; i++) {
            points.add(new LatLng(pointLat[i],pointLng[i]));
        }
        for (int i = 0, j = pointLat.length - 1; i < pointLat.length; j = i++) {
            if ((points.get(i).longitude > lng) != (points.get(j).longitude > lng) &&
                lat < (points.get(j).latitude - points.get(i).latitude) * (lng - points.get(i).longitude) /
                    (points.get(j).longitude - points.get(i).longitude) + points.get(i).latitude)
            {
                isInside = !isInside;
            }
        }
        return isInside;
    }
}