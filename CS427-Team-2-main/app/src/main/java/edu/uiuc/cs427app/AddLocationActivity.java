package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {

    TextView AddCity;
    private String userInput;
    AutoCompleteTextView autoCompleteCityName;

    private ArrayAdapter<String> adapter;
    public static final String API_KEY = "454f24df1f41dca822c069c74c23242d";
    private Map<String, Double[]> cityCoordinates = new LinkedHashMap<>(); // To store lat and lon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_addlocation);

        Button buttonMap = findViewById(R.id.addLocationButton);
        buttonMap.setOnClickListener(this);

        Button seeMatches = findViewById(R.id.seeMatches);
        seeMatches.setOnClickListener(this);

        autoCompleteCityName = findViewById(R.id.autoCompleteCityName);
        autoCompleteCityName.requestFocus();

        // Initialize the adapter once
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteCityName.setAdapter(adapter);

        // Add a TextWatcher to the AutoCompleteTextView
        autoCompleteCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) { // Fetch only if more than 1 character
//                    TODEBUG
//                    fetchCitiesFromApi(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }

    /**
     * Fetches cities from the API based on the provided city name.
     *
     * @param cityName The name of the city to fetch.
     */
    private void fetchCitiesFromApi(String cityName) {
        String apiUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=5&appid=" + API_KEY; // Use HTTPS
        new FetchCitiesTask(this, autoCompleteCityName, cityCoordinates).execute(apiUrl);
    }

    private static class FetchCitiesTask extends AsyncTask<String, Void, List<String>> {
        private final AddLocationActivity activity;
        private final AutoCompleteTextView autoCompleteTextView;
        private ArrayAdapter<String> adapter;  // Declare the adapter here
        private Map<String, Double[]> cityCoordinates; // To store lat and lon

        /**
         * Constructor for FetchCitiesTask.
         *
         * @param activity The activity context.
         * @param autoCompleteTextView The AutoCompleteTextView to update.
         */
        FetchCitiesTask(AddLocationActivity activity, AutoCompleteTextView autoCompleteTextView, Map<String, Double[]> cityCoordinates) {
            this.activity = activity;
            this.autoCompleteTextView = autoCompleteTextView;
            this.cityCoordinates = cityCoordinates;

            // Initialize the ArrayAdapter with an empty list
            adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
            autoCompleteTextView.setAdapter(adapter);
        }

        /**
         * Parses the JSON response to extract city names.
         *
         * @param json The JSON response from the API.
         * @return A list of city names.
         */
        private static List<String> parseCities(String json, Map<String, Double[]> cityCoordinates) {
            Map<String, String> uniqueCities = new LinkedHashMap<>(); // Keeps insertion order
            try {
                JsonArray cityArray = new Gson().fromJson(json, JsonArray.class);
                for (JsonElement element : cityArray) {
                    JsonObject cityObject = element.getAsJsonObject();
                    String cityName = cityObject.get("name").getAsString();
                    String state = cityObject.has("state") ? cityObject.get("state").getAsString() : "N/A"; // Default to "N/A" if state is not available
                    String country = cityObject.get("country").getAsString();
                    double lat = cityObject.get("lat").getAsDouble();
                    double lon = cityObject.get("lon").getAsDouble();

                   String displayName = String.format("%s, %s, %s", cityName, state, country);

                    uniqueCities.put(cityName + state, displayName);
                    cityCoordinates.put(displayName, new Double[]{lat, lon}); // Store lat and lon

                    Log.d("ParseCities", "Parsed city: " + displayName);
                }

                // Log the complete list of parsed cities
                Log.d("ParseCities", "Complete list of cities: " + uniqueCities.values().toString());
            } catch (Exception e) {
                Log.e("ParseCities", "Error parsing cities", e);
            }
            // Return the values from the map as a list
            return new ArrayList<>(uniqueCities.values());
        }

        // Fetch cities in the background
        @Override
        protected List<String> doInBackground(String... urls) {
            String apiUrl = urls[0];
            StringBuilder result = new StringBuilder();
            try {
                Log.d("FetchCitiesTask", "API URL: " + apiUrl); // Log the API URL before the connection
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("FetchCitiesTask", "API Response: " + result.toString()); // Log the API response
                    return parseCities(result.toString(), cityCoordinates);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("FetchCitiesTask", "Error fetching cities", e);
                return null;
            }
        }
        // Add city to the adapter
        @Override
        protected void onPostExecute(List<String> uniqueCities) {
            // Log the entire list of cities to see if it contains data
            Log.d("FetchCitiesTask", "onPostExecute called with cities: " + uniqueCities);

            if (uniqueCities != null && !uniqueCities.isEmpty()) {
                adapter.clear();
                adapter.addAll(uniqueCities);
                adapter.notifyDataSetChanged();
                autoCompleteTextView.showDropDown();
                Log.d("FetchCitiesTask", "Adapter updated with cities: " + uniqueCities.size() + " items");
            } else {
                Log.d("FetchCitiesTask", "No cities found or API returned null"); // Log when no cities are found
            }
        }
    }

    /*
     * Handles the click events for the buttons.
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        userInput = autoCompleteCityName.getText().toString().trim();
        switch (view.getId()) {
            case R.id.addLocationButton:
                if (!userInput.isEmpty()) {
                        // Start MainActivity and pass the selected city
                        intent = new Intent(this, MainActivity.class);
                        intent.putExtra("cityId", userInput);
                        Double[] coordinates = cityCoordinates.get(userInput);
                       if (coordinates != null) {
                            intent.putExtra("latitude", coordinates[0]);
                            intent.putExtra("longitude", coordinates[1]);
                        }
                        startActivity(intent);

                } else {
                    // Notify user to input a city
                    Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.seeMatches:
                if (!userInput.isEmpty()) {
                    fetchCitiesFromApi(userInput);
                } else {
                    // Notify user to input a city
                    Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}