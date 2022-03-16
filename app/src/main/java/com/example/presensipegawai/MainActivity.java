package com.example.presensipegawai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * The type Main activity.
 * Activity Main digunakan untuk menampilkan halaman utama aplikasi.
 */
public class MainActivity extends AppCompatActivity {

    // Deklarasi variable
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCoverageArea;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Proses set main_menu kedalam MainActivity
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Mendapatkan Item Id yang dipilih
        // Proses cek id menu yang dipilih
        if (id == R.id.menu_history) {
            // Membuka HistoryActivity
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        fabCoverageArea = findViewById(R.id.fabCoverageArea);

        // Inisialisasi Fragment Manager di activity_main kedalam MainActivity
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameMain, new HomeFragment()).commit();

        // Event saat FAB Area Jangkauan diklik
        fabCoverageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Membuka Activity Area Jangkauan Peta
                Intent intent = new Intent(getApplicationContext(), MapCoverageAreaActivity.class);
                startActivity(intent);
            }
        });

        // Event saat Bottom Navigation diklik
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_home:
                        // Set HomeFragment / menampilkan halaman Home Fragment
                        fragment = new HomeFragment();
                        break;
                    case R.id.menu_profile:
                        // Set ProfileFragment / menampilkan halaman Profile Fragment
                        fragment = new ProfileFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameMain, fragment).commit(); // Commit fragment yang dipilih ke activity fragment
                return true;
            }
        });
    }
}