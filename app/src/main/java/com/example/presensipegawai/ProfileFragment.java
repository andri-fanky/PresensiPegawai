package com.example.presensipegawai;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Deklarasi variable
    TextView tvNama, tvUnitKerja, tvNamaBiodata, tvEmailBiodata, tvUnitKerjaBiodata;
    Button btnSignOut;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi variable dengan id yang sesuai
        tvNama = rootView.findViewById(R.id.tvNama);
        tvUnitKerja = rootView.findViewById(R.id.tvUnitKerja);
        tvNamaBiodata = rootView.findViewById(R.id.tvNamaBiodata);
        tvEmailBiodata = rootView.findViewById(R.id.tvEmailBiodata);
        tvUnitKerjaBiodata = rootView.findViewById(R.id.tvUnitKerjaBiodata);
        btnSignOut = rootView.findViewById(R.id.btnSignOut);

        SharedPreferences sp = this.getActivity().getSharedPreferences("Session", this.getActivity().MODE_APPEND);
        // Mengambil value dari session
        String nama = sp.getString("nama", "-");
        String unitKerja = sp.getString("nama_unit_kerja", "-");
        String email = sp.getString("email", "-");
        // Set value yang didapatkan dari session ke TextView
        tvNama.setText(nama);
        tvUnitKerja.setText(unitKerja);
        tvNamaBiodata.setText(nama);
        tvEmailBiodata.setText(email);
        tvUnitKerjaBiodata.setText(unitKerja);

        // Event saat button sign out diklik
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menampilkan Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_sign_out);
                builder.setMessage(R.string.dialog_sign_out)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sp = getContext().getSharedPreferences("Session", getContext().MODE_PRIVATE);
                            sp.edit().clear().apply();
                            Toast.makeText(getContext(), "Berhasil sign out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
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

        return rootView;
    }
}