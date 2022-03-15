package com.example.presensipegawai;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapCoverageAreaActivity extends FragmentActivity implements OnMapReadyCallback {

    // Deklarasi variable
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_coverage_area);
        // Set LocationService ke variable fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Memanggil method fetchLocation()
        fetchLocation();
    }

    private void fetchLocation() {
        // Cek permission lokasi kepada pengguna
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation(); // Mengambil lokasi terbaru
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location; // Set lokasi ke variable currentLocation
//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    // Inisialisasi MapFragment kedalam id mapCoverageArea
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCoverageArea);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapCoverageAreaActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Deklarasi LatLng dengan lokasi pengguna yang sudah di dapat
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Posisi Anda"); // Menambahkan marker lokasi pengguna
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); // Animasi kamera ke lokasi pengguna
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17)); // Animasi kamera zoom
        googleMap.addMarker(markerOptions); // Menambahkan marker kedalam Google Map

        boolean status = checkPosition(latLng); // Cek prosisi apakah diluar atau di dalam area jangkauan

        // Cek status didalam atau diluar area jangkauan
        if (status) {
            Toast.makeText(this, "Anda di dalam area jangkauan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Anda di luar area jangkauan", Toast.LENGTH_SHORT).show();
        }

        // Deklarasi Polygon untuk radius lokasi area jangkauan
        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
            .clickable(true)
            .add(
                new LatLng(-7.8079055, 110.3205579),
                new LatLng(-7.8081659, 110.3210731),
                new LatLng(-7.8085007, 110.3217064),
                new LatLng(-7.8085858, 110.3220929),
                new LatLng(-7.8086921, 110.3224686),
                new LatLng(-7.8089365, 110.3226135),
                new LatLng(-7.8092023, 110.3225491),
                new LatLng(-7.8095158, 110.3224578),
                new LatLng(-7.8097444, 110.3224364),
                new LatLng(-7.8100739, 110.3224149),
                new LatLng(-7.8096487, 110.3216152),
                new LatLng(-7.8095158, 110.3212932),
                new LatLng(-7.8093777, 110.3210785),
                new LatLng(-7.8092714, 110.3207779),
                new LatLng(-7.8096115, 110.3206169),
                new LatLng(-7.8093564, 110.3200641),
                new LatLng(-7.8087665, 110.3198870),
                new LatLng(-7.8086177, 110.3198977),
                new LatLng(-7.8081712, 110.3199675),
                new LatLng(-7.8077779, 110.3200104),
                new LatLng(-7.8076982, 110.3200534),
                new LatLng(-7.8079055, 110.3205579)
            )
        );
        polygon1.setTag("radius");
        Polygon polygon2 = googleMap.addPolygon(new PolygonOptions()
            .clickable(true)
            .add(
                new LatLng(-7.8593113, 110.3066631),
                new LatLng(-7.8593432, 110.3073877),
                new LatLng(-7.8606929, 110.3073608),
                new LatLng(-7.8606238, 110.3066202),
                new LatLng(-7.8593113, 110.3066631)
            )
        );
        polygon2.setTag("radius 2");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
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
        // Proses menghitung atau pengecekan apakah lokasi pengguna didalam area jangkauan atau tidak
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