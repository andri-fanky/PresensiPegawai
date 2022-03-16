package com.example.presensipegawai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * The type Splash screen activity.
 * Activity Splash Screen digunakan untuk menampilkan sebuah halaman awal sebelum masuk ke halaman utama aplikasi, dan hanya tampil beberapa detik saja.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Fungsi delay untuk SplashScreen, durasi delay 2000 miliseconds atau sama dengan 2 detik
        new Handler().postDelayed(new Runnable() {
            /**
             * Method run dijalankan pertama kali setelah 2 detik aplikasi dibuka.
             * Method run berisi fungsi untuk mengecek session user apakah ada atau tidak.
             * Jika ada session, maka masuk ke halaman utama.
             * Jika tidak ada session, maka masuk ke halaman login.
             */
            @Override
            public void run() {
                // Deklarasi variable SharedPreferences untuk mengambil Session id_user
                SharedPreferences sp = getSharedPreferences("Session", MODE_APPEND);
                String idUser = sp.getString("id_user", "");
                Intent intent;
                // Cek apakah session id_user ada
                if (!idUser.isEmpty()) {
                    // Apabila ada, maka langsung masuk ke halaman MainActivity
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                } else {
                    // Apabila tidak ada, maka masuk ke halaman LoginActivity
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(intent); // Proses membuka activity
                finish(); // Menutup SplashScreenActivity
            }
        }, 2000);
    }
}