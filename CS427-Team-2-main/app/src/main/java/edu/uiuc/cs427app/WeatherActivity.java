package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.viewmodel.CityViewModel;

public class WeatherActivity extends AppCompatActivity {
    private CityViewModel cityViewModel;
    String weatherDataJson; // Store the serialized weather data

    /*
     * This activity shows the weather information of a city.
     * The city is identified by its ID, which is passed as an Intent extra.
     * The city's name, temperature, weather, humidity, wind speed, and wind direction are displayed on the screen.
     * The user can click a button to view insights about the weather.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_weather);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        int city_ID = getIntent().getIntExtra("city_ID", -1);

        // Initializing the GUI elements
        TextView cityInfoMessage = findViewById(R.id.cityInfo);
        TextView cityNameTextView = findViewById(R.id.cityName); // Reference to the city name TextView

        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.getCityById(city_ID).observe(this, city -> {
            if (city != null)
            {
                String displayText = city.getCityName();
                displayText = displayText.replace(", N/A", "");
                // Set the city name in the TextView
                cityNameTextView.setText(displayText);

                // Fetch and display weather information
                fetchWeather(city.getCityName(), cityInfoMessage);

                Button weatherInsightsButton = findViewById(R.id.generateInsights);
                weatherInsightsButton.setOnClickListener(v -> showWeatherInsights());
            }
        });

    }

    /**
     * Starts the LLMInsightsActivity to show insights about the weather.
     */
    private void showWeatherInsights() {
        Intent intent = new Intent(this, LLMInsightsActivity.class);

        // Pass weather data as a JSON string via the intent
        intent.putExtra("weatherData", weatherDataJson != null ? weatherDataJson : "");
        startActivity(intent);
    }

    /**
     * Fetches weather from the API based on the provided city name.
     *
     * @param cityName The name of the city to fetch.
     */
    public void fetchWeather(String cityName, TextView cityInfoMessage) {
        new FetchWeatherAsync(cityInfoMessage).execute(cityName);
    }

    /**
     * AsyncTask to fetch weather data from the API.
     */
    private class FetchWeatherAsync extends AsyncTask<String, Void, HashMap<String, String>> {
        private final TextView cityInfoMessage;

        final Map<String, String> infoFields = Map.of(
                "location.lat", "Latitude",
                "location.lon", "Longitude",
                "location.localtime", "Local Time",
                "current.temp_c", "Temperature(°C)",
                "current.condition.text", "Weather",
                "current.humidity", "Humidity",
                "current.wind_mph", "Wind Speed(MPH)",
                "current.wind_dir", "Wind Direction"
        );

        /**
         * Constructor to initialize the AsyncTask.
         *
         * @param cityInfoMessage The TextView to display the weather information.
         */
        FetchWeatherAsync(TextView cityInfoMessage) {
            this.cityInfoMessage = cityInfoMessage;
        }

        /**
         * Fetches weather data from the API based on the provided city name.
         *
         * @param cityName The name of the city to fetch.
         * @return The weather data as a HashMap.
         */
        @Override
        protected HashMap<String, String> doInBackground(String... cityName) {
            String cityname = cityName[0];
            String weatherUrl = "https://api.weatherapi.com/v1/current.json?key=1b866ae7fc7f46b683d132704240211&q=" + cityname + "&aqi=no";
            return fetchFromWeatherAPI(weatherUrl, infoFields.keySet().toArray(new String[0]));
        }

        /**
         * Fetches data from the weather API.
         *
         * @param webUrl    The URL to fetch the data from.
         * @param keyStrings The keys to extract from the JSON response.
         * @return The weather data as a HashMap.
         */
        private HashMap<String, String> fetchFromWeatherAPI(String webUrl, String[] keyStrings) {
            HashMap<String, String> results = new HashMap<>();
            try {
                URL url = new URL(webUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Log the entire JSON response
                String jsonResponse = response.toString();

                // Now parse the JSON to extract specific fields as before
                JsonObject cityObject = new Gson().fromJson(jsonResponse, JsonObject.class);
                for (String keystring : keyStrings) {
                    JsonObject responseObj = cityObject;
                    String[] keys = keystring.split("\\.");
                    String val = "";
                    for (int i = 0; i < keys.length; i++) {
                        String key = keys[i];
                        if (!responseObj.keySet().contains(key)) {
                            break;
                        }
                        if (i + 1 == keys.length) val = responseObj.get(key).getAsString();
                        else responseObj = responseObj.get(key).getAsJsonObject();
                    }
                    results.put(keystring, val);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        /**
         * Updates the UI with the weather information.
         *
         * @param weatherInfo The weather information to display.
         */
        @Override
        protected void onPostExecute(HashMap<String, String> weatherInfo) {
            // Convert weather data to JSON string and store it
            weatherDataJson = new Gson().toJson(weatherInfo);

            // Build the display information in the specified format
            StringBuilder simpleResponse = new StringBuilder();

            // Date and Time (use the local time from the weather API response)
            if (weatherInfo.containsKey("location.localtime")) {
                String localtime = weatherInfo.get("location.localtime");
                // Split the localtime into date and time
                String[] dateTimeParts = localtime.split(" ");
                if (dateTimeParts.length == 2) {
                    // Update the Date field
                    TextView dateValue = findViewById(R.id.dateValue);
                    dateValue.setText(dateTimeParts[0] + "\n");

                    // Update the Time field
                    TextView timeValue = findViewById(R.id.timeValue);
                    timeValue.setText(dateTimeParts[1] + "\n");
                }
            }

            // Weather Data
            if (weatherInfo.containsKey("current.temp_c")) {
                // Update the TextViews with the corresponding data
                TextView temperatureValue = findViewById(R.id.temperatureValue);
                temperatureValue.setText(weatherInfo.get("current.temp_c")+ " °C\n");
            }
            if (weatherInfo.containsKey("current.condition.text")) {
                // Update the weather field
                TextView weatherValue = findViewById(R.id.weatherValue);
                weatherValue.setText(weatherInfo.get("current.condition.text") + "\n");
            }
            if (weatherInfo.containsKey("current.humidity")) {
                // Update the humidity field
                TextView humidityValue = findViewById(R.id.humidityValue);
                humidityValue.setText(weatherInfo.get("current.humidity") + "%\n");
            }
            if (weatherInfo.containsKey("current.wind_mph") && weatherInfo.containsKey("current.wind_dir")) {
                // Update the wind field
                TextView windValue = findViewById(R.id.windValue);
                windValue.setText(weatherInfo.get("current.wind_mph") + " MPH, " + weatherInfo.get("current.wind_dir") + "\n");
            }


            // Set the formatted text to the TextView
            cityInfoMessage.setText(simpleResponse.toString());
        }

    }
}
