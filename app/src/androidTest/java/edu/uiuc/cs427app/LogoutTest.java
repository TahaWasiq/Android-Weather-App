package edu.uiuc.cs427app;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertEquals;

/**
 * Tests the logout functionality.
 */
@RunWith(AndroidJUnit4.class)
public class LogoutTest {
    private SharedPreferences sharedPreferences;

    private String test_name = "Taha Wasiq";
    private String test_uid = "twasiq2";
    private String test_password = "123";

    /**
     * Launches the application by starting the RegisterActivity.
     */
    private void launchApp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent();
        intent.setClass(context, RegisterActivity.class);
        ActivityScenario.launch(intent);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Sets up the test environment before each test.
     * Initializes SharedPreferences and registers a new user.
     */
    @Before
    public void setUp() {
        // Prepare context and SharedPreferences
        Context context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        // Clear SharedPreferences before each test
        sharedPreferences.edit().clear().apply();

        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInNetId", test_uid); // Save netId
        editor.apply(); // Apply changes

        launchApp();
        // Second, register an account for testing.
        onView(withId(R.id.nameEditText)).perform(replaceText(test_name));
        onView(withId(R.id.netIdEditText)).perform(replaceText(test_uid));
        onView(withId(R.id.passwordEditText)).perform(replaceText(test_password));
        onView(withId(R.id.confirmPasswordEditText)).perform(replaceText(test_password));
        onView(withId(R.id.registerButton)).perform(click());
    }

    /**
     * Tests the logout functionality.
     * Verifies that the user NetID is cleared from SharedPreferences after logout.
     * @throws InterruptedException if the thread sleep is interrupted
     */
    @Test
    public void testLogout() throws InterruptedException {
        // Verify the user NetID is saved in SharedPreferences after registration
        assertEquals(test_uid, sharedPreferences.getString("loggedInNetId", ""));

        // Add delay (e.g., 1 second) to simulate UI interaction
        Thread.sleep(1200);

        // Simulate logout button click
        onView(withId(R.id.logoutButton)).perform(click());

        // Add delay (e.g., 1 second) to allow logout operation to complete
        Thread.sleep(1000);

        // Verify NetID in SharedPreferences is cleared after logout
        assertEquals("", sharedPreferences.getString("loggedInNetId", ""));
    }
}