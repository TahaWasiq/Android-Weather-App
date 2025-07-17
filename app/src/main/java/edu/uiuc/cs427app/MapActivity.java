package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.uiuc.cs427app.viewmodel.CityViewModel;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private CityViewModel cityViewModel;
    private GoogleMap mMap;


    /*
     * This activity shows the location of a city on a map.
     * The city is identified by its ID, which is passed as an Intent extra.
     * The city's name, latitude, and longitude are displayed on the screen.
     * The city's location is marked on the map.
     * The map is centered on the city's location.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_map);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        int city_ID = getIntent().getIntExtra("city_ID", -1);
        String CID = "City ID: " + String.valueOf(city_ID);

        TextView cityName = findViewById(R.id.cityName);
        TextView latitude = findViewById(R.id.latitude);
        TextView longitude = findViewById(R.id.longitude);


        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.getCityById(city_ID).observe(this, city -> {
            if (city != null) {
                cityName.setText(city.getCityName());
                latitude.setText(String.valueOf(city.latitude));
                longitude.setText(String.valueOf(city.longitude));
                LatLng cityLatLng = new LatLng(city.latitude, city.longitude);
                mMap.addMarker(new MarkerOptions().position(cityLatLng).title(city.getCityName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 10));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Called when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the map.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}