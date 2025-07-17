package edu.uiuc.cs427app;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MockLocationTest {

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Mock
    LocationManager mockLocationManager;

    @Mock
    Location mockLocation;

    private Context context;

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and mocks the behavior of the context and LocationManager.
     */
    @Before
    public void setup() {
        // Initialize mock objects
        MockitoAnnotations.initMocks(this);

        // Mock the context and its behavior
        context = Mockito.mock(Context.class);

        // Mock the behavior of getSystemService to return the mocked LocationManager
        Mockito.when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(mockLocationManager);

        // Mock the location object
        Mockito.when(mockLocation.getLatitude()).thenReturn(40.1138);
        Mockito.when(mockLocation.getLongitude()).thenReturn(-88.2249);
        Mockito.when(mockLocation.getAccuracy()).thenReturn(1f);
        Mockito.when(mockLocation.getTime()).thenReturn(System.currentTimeMillis());

        // Mock the LocationManager to return the mock location
        Mockito.when(mockLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(mockLocation);
    }

    /**
     * Tests that mock location updates the weather information in the UI.
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @Test
    public void testMockLocationUpdatesWeatherInfo() throws InterruptedException {
        // Launch the WeatherActivity
        ActivityScenario<WeatherActivity> scenario = ActivityScenario.launch(WeatherActivity.class);

        // Simulate receiving a mock location update
        scenario.onActivity(activity -> {
            // Prepare mock weather data
            HashMap<String, String> mockWeatherData = new HashMap<>();
            mockWeatherData.put("location.localtime", "2024-12-03 14:30");
            mockWeatherData.put("current.temp_c", "25");
            mockWeatherData.put("current.condition.text", "Sunny");
            mockWeatherData.put("current.humidity", "45");
            mockWeatherData.put("current.wind_mph", "10");
            mockWeatherData.put("current.wind_dir", "NE");
            mockWeatherData.put("location.city_name", "Champaign, Illinois, US"); // Mock city name

            // Convert mock weather data to JSON and store it in the activity
            activity.weatherDataJson = new Gson().toJson(mockWeatherData);

            // Call updateWeatherUI to simulate updating the UI
            updateWeatherUI(activity, mockWeatherData);
        });

        // Add delay (e.g., 2 seconds)
        Thread.sleep(2000);  // Delay for 2 seconds before checking UI updates

        // Assert UI updates for weather data
        Espresso.onView(withId(R.id.temperatureValue))
                .check(matches(withText("25 °C\n")));
        Espresso.onView(withId(R.id.weatherValue))
                .check(matches(withText("Sunny\n")));
        Espresso.onView(withId(R.id.humidityValue))
                .check(matches(withText("45%\n")));
        Espresso.onView(withId(R.id.windValue))
                .check(matches(withText("10 MPH, NE\n")));

        // Assert that the city name is displayed correctly
        Espresso.onView(withId(R.id.cityName)) // Assuming the city name TextView ID is cityName
                .check(matches(withText("Champaign, Illinois, US\n")));
    }

    /**
     * Helper method to update UI elements with weather data.
     * @param activity The WeatherActivity instance.
     * @param weatherData The weather data to update the UI with.
     */
    private void updateWeatherUI(WeatherActivity activity, HashMap<String, String> weatherData) {
        // Run on the UI thread to ensure thread safety
        activity.runOnUiThread(() -> {
            // City Name
            if (weatherData.containsKey("location.city_name")) {
                String cityName = weatherData.get("location.city_name");
                TextView cityNameTextView = activity.findViewById(R.id.cityName); // Assuming the TextView for city name has this ID
                cityNameTextView.setText(cityName + "\n");
            }

            // Date and Time
            if (weatherData.containsKey("location.localtime")) {
                String localtime = weatherData.get("location.localtime");
                String[] dateTimeParts = localtime.split(" ");
                if (dateTimeParts.length == 2) {
                    TextView dateValue = activity.findViewById(R.id.dateValue);
                    dateValue.setText(dateTimeParts[0] + "\n");

                    TextView timeValue = activity.findViewById(R.id.timeValue);
                    timeValue.setText(dateTimeParts[1] + "\n");
                }
            }

            // Temperature
            if (weatherData.containsKey("current.temp_c")) {
                TextView temperatureValue = activity.findViewById(R.id.temperatureValue);
                temperatureValue.setText(weatherData.get("current.temp_c") + " °C\n");
            }

            // Weather Condition
            if (weatherData.containsKey("current.condition.text")) {
                TextView weatherValue = activity.findViewById(R.id.weatherValue);
                weatherValue.setText(weatherData.get("current.condition.text") + "\n");
            }

            // Humidity
            if (weatherData.containsKey("current.humidity")) {
                TextView humidityValue = activity.findViewById(R.id.humidityValue);
                humidityValue.setText(weatherData.get("current.humidity") + "%\n");
            }

            // Wind Data
            if (weatherData.containsKey("current.wind_mph") && weatherData.containsKey("current.wind_dir")) {
                TextView windValue = activity.findViewById(R.id.windValue);
                windValue.setText(weatherData.get("current.wind_mph") + " MPH, " + weatherData.get("current.wind_dir") + "\n");
            }
        });
    }
}