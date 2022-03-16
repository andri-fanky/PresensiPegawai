package com.example.presensipegawai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.presensipegawai.helper.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Login activity.
 * Activity Login digunakan untuk menampilkan halaman login aplikasi.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The Edt email.
     * edtEmail adalah inisialisasi variable dari activity untuk input password dari user.
     */
    EditText edtEmail,
    /**
     * The Edt password.
     * edtPassword adalah inisialisasi variable dari activity untuk input password dari user.
     */
    edtPassword;
    /**
     * The Btn sign in.
     * btnSignIn digunakan untuk
     */
    Button btnSignIn;
    /**
     * The Email.
     * variable email digunakan untuk menyimpan input email dari user.
     */
    String email,
    /**
     * The Password.
     * variable password digunakan untuk menyimpan input password dari user.
     */
    password;
    /**
     * The Status.
     * variable status digunakan untuk menyimpan response dari API, apakah proses login sukses / gagal.
     */
    boolean status;

    // Deklarasi variable url_login digunakan untuk mengakses url API untuk fungsi login
    private static String url_login = Api.url+"/auth/login.php";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi id dari activity/view ke dalam variable yang sudah di deklarasikan diatas
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Event saat button sign in diklik / ditekan
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProcess();
            }
        });
    }

    /**
     * Open main activity.
     * Method openMainActivity digunakan untuk membuka halaman utama.
     */
    public void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish(); // Menutup LoginActivity
    }

    /**
     * Login process.
     * Method loginProcess digunakan untuk proses login.
     */
    public void loginProcess() {
        // Mengambil teks pada EditText Email & Password
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) { // Cek apabila email atau password kosong
            Toast.makeText(getApplicationContext(), "Harap lengkapi data!", Toast.LENGTH_SHORT).show(); // Menampilkan pesan toast
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext()); // Deklarasi variable RequestQueue

            // Proses akses ke API
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response.toString()); // Menampilkan response kedalam Log
                    try {
                        JSONObject jsonObject = new JSONObject(response); // Deklarasi response kedalam JSONObject
                        status = jsonObject.getBoolean(TAG_STATUS); // Set status dari response API
                        // Cek apakah status berhasil atau gagal
                        if (status) {
                            JSONObject jsonObject2 = new JSONObject(jsonObject.getString("data")); // Mendapatkan data dan menyimpannya kedalam JSONObject
                            SharedPreferences sp = getSharedPreferences("Session", MODE_PRIVATE); // Deklarasi Session yang nantinya digunakan untuk menyimpan Session Login
                            SharedPreferences.Editor editor = sp.edit(); // Deklarasi variable edit pada SharedPreferences
                            // Proses menambahkan value kedalam editor
                            editor.putString("id_user", jsonObject2.getString("id_user"));
                            editor.putString("nama", jsonObject2.getString("nama"));
                            editor.putString("email", jsonObject2.getString("email"));
                            editor.putString("nama_unit_kerja", jsonObject2.getString("nama_unit_kerja"));
                            editor.apply(); // Proses apply menyimpan SharedPreferences
                            Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show(); // Menampilkan toast informasi Login berhasil
                            openMainActivity(); // Membuka halaman MainActivity
                        } else {
                            Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show(); // Menampilkan toast informasi Login gagal
                        }
                    } catch (JSONException e) {
                        e.printStackTrace(); // Menampilkan error pada blok try
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Menampilkan error response
                    Log.e(TAG,"Error : " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Inisialisasi parameter
                    Map<String, String> params = new HashMap<>();
                    params.put("email",email);
                    params.put("password",password);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }
}