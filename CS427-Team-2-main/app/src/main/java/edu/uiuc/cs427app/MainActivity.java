package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.database.entity.User;
import edu.uiuc.cs427app.database.entity.UserCityPair;
import edu.uiuc.cs427app.databinding.ActivityMainBinding;
import edu.uiuc.cs427app.viewmodel.CityViewModel;
import edu.uiuc.cs427app.viewmodel.UserViewModel;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import androidx.lifecycle.Observer;

/**
 * Main activity class for the application.
 * Handles user interactions and displays the list of cities.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    UserViewModel userViewModel;
    CityViewModel cityViewModel;

    /**
     * Adds a city view to the main layout.
     *
     * @param city The city to add.
     */
    void addCityView(City city) {
        LinearLayout mainLayout = findViewById(R.id.locationListLayout);
        LinearLayout oldLocationLayout = findViewById(city.getCityId());
        if (oldLocationLayout != null) {
            mainLayout.removeView(oldLocationLayout);
        }
        LinearLayout newLocationLayout = new LinearLayout(this);
        newLocationLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200
        );
        int marginInDp = 20;
        float scale = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * scale + 0.5f);
        params.setMarginStart(marginInPx);
        params.setMarginEnd(marginInPx);
        newLocationLayout.setLayoutParams(params);
        newLocationLayout.setId(city.getCityId());
        TextView textView = new TextView(this);

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                50,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                8));
        textView.setText(city.getCityName());

//      Placeholder between button
        TextView placeholder = new TextView(this);
        placeholder.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                1,
                1));

//      Add weather button
        Button button_w = new Button(this);
        button_w.setLayoutParams(new LinearLayout.LayoutParams(
                20,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                3));

//      Add map button
        Button button_m = new Button(this);
        button_m.setLayoutParams(new LinearLayout.LayoutParams(
                15,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                3));

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimarySurface, typedValue, true);
        int color = ContextCompat.getColor(this, typedValue.resourceId);

        button_w.setBackgroundColor(color);
        button_w.setText("Weather");
        button_w.setOnClickListener(this);

        button_m.setBackgroundColor(color);
        button_m.setText("Map");
        button_m.setOnClickListener(this);

        newLocationLayout.addView(textView);
        newLocationLayout.addView(button_w);
        newLocationLayout.addView(placeholder);
        newLocationLayout.addView(button_m);

        mainLayout.addView(newLocationLayout, 2);
    }

    /**
     * Adds a city to the user's list of cities.
     *
     * @param cityName The name of the city to add.
     * @param latitude The latitude of the city.
     * @param longitude The longitude of the city.
     */
    private void addCity(String cityName, Double latitude, Double longitude) {
        User user = userViewModel.getCurrentUser();

        City city = cityViewModel.getCityUsingName(cityName);
        long cityId = -1;
        if(city == null){
            city = new City(cityName, latitude, longitude);
            cityId = cityViewModel.insertCity(city);
        }else{
            cityId = city.getCityId();
        }

        if(!cityViewModel.doesCityExistForUser(user.getId(), (int) cityId)) {
            UserCityPair userCityPair = new UserCityPair(user.getId(), (int) cityId);
            cityViewModel.insertUserCityPair(userCityPair);
        }else {
            Toast.makeText(this, "City already exist!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes the main activity.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_main);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);

        // Check if the user is logged in
        User currentUser = userViewModel.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish(); // Ensure the current activity is finished if no user is logged in
            return; // Early return to avoid further code execution
        }

        // Get the cities added by the user
        cityViewModel.getCitiesByUserId(currentUser.getId()).observe(this, new Observer<List<City>>() {
            @Override
            public void onChanged(List<City> cities) {
                for (City city : cities) {
                    addCityView(city);
                }
            }
        });

        String cityName = getIntent().getStringExtra("cityId");
        Double latitude = getIntent().getDoubleExtra("latitude", 0);
        Double longitude = getIntent().getDoubleExtra("longitude", 0);

        if (cityName != null) {
            addCity(cityName, latitude, longitude);
        }

        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
        buttonAddLocation.setOnClickListener(this);

        Button buttonRemoveLocation = findViewById(R.id.buttonRemoveLocation);
        buttonRemoveLocation.setOnClickListener(this);

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        // Set title and update logout button text
        setTitle("Team 2 - " + currentUser.getNetId());
        logoutButton.setText("Logout " + currentUser.getNetId());
    }

    /**
     * Handles the click events for the views.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {

            case R.id.buttonAddLocation:
                // Add a new location to the list of locations
                intent = new Intent(this, AddLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonRemoveLocation:
                // Remove a location from the list of locations
                finish();
                intent = new Intent(this, RemoveLocationActivity.class);
                startActivity(intent);
                break;
            case R.id.logoutButton:
                // Logout
                userViewModel.logout();
                intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                break;

        }
        int city_id = ((View) view.getParent()).getId();
//      Switch activity according to button text.
        switch (((TextView) view).getText().toString()) {
            case "Weather":
                intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("city_ID", city_id);
                startActivity(intent);
                break;
            case "Map":
                intent = new Intent(this, MapActivity.class);
                intent.putExtra("city_ID", city_id);
                startActivity(intent);
                break;
        }
    }

    /**
     * Handles the back button press.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}