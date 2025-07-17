package edu.uiuc.cs427app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.viewmodel.CityViewModel;
import edu.uiuc.cs427app.viewmodel.UserViewModel;

public class RemoveLocationActivity extends AppCompatActivity implements View.OnClickListener {

    CityViewModel cityViewModel;
    UserViewModel userViewModel;
    LiveData<List<City>> cities;

    /**
     * Adds a button for a city to the main layout, allowing the user to select it for removal.
     * Each button displays the city's ID and name and has a red background.
     *
     * @param city The City object to add that contains its ID and city name
     */
    private void addCity(City city) {
        LinearLayout mainLayout = findViewById(R.id.removelocation);

        Button button = new Button(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        button.setLayoutParams(params);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimarySurface, typedValue, true);
        int color = ContextCompat.getColor(this, typedValue.resourceId);

        button.setBackgroundColor(color);
        button.setId(city.getCityId());
        button.setText(city.getCityName());
        button.setOnClickListener(this);
        mainLayout.addView(button, 1);
    }

    /**
     * Method to initialize the RemoveLocationActivity
     * It sets the theme, inflates the layout and additionally populates the city buttons for the
     * current user.
     *
     * @param savedInstanceState If the activity is re-initialized after being shut down, this
     *                           Bundle contains the last saved state; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_removelocation);

        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        cities = cityViewModel.getCitiesByUserId(userViewModel.getCurrentUser().getId());

        // Process the Intent payload that has opened this Activity and show the information accordingly
        cities.observe(this, cities -> {
            for (City city : cities) {
                addCity(city);
            }
        });
    }

    /**
     * This method handles button click events in the RemoveLocationActivity.
     * When a city button is clicked, deletes the city by its ID and navigates back to the
     * MainActivity.
     *
     * @param view The View object that was clicked, representing the city button.
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        int id = ((Button) view).getId();
        cityViewModel.deleteCityPairUsingCityId(userViewModel.getCurrentUser().getId(), id);
        cities.removeObservers(this);
        finish();
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}