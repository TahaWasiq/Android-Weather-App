package edu.uiuc.cs427app.authentication;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.LoginActivity;
import edu.uiuc.cs427app.MainActivity;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.RegisterActivity;
import edu.uiuc.cs427app.ToastMatcher;
import edu.uiuc.cs427app.viewmodel.UserViewModel;


@RunWith(AndroidJUnit4.class)
public class UserSignUpTest {

    /**
     * Sets up the test environment before each test.
     * Initializes intents and launches the RegisterActivity.
     */
    @Before
    public void setup() {
        // Initialize intents for testing navigation
        Intents.init();

        // Launch the RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);
    }

    /**
     * Cleans up the test environment after each test.
     * Releases intents.
     */
    @After
    public void tearDown() {
        // Release intents after test
        Intents.release();
    }

    /**
     * Tests that the appropriate error message is displayed when no data is entered.
     */
    @Test
    public void testEmptyFieldsValidation() {
        ActivityScenario.launch(RegisterActivity.class);

        // Click on the register button without entering any data
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());

        // Check that the appropriate error message is displayed
        onView(withText("Please fill out all fields"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Tests that the validation handles password mismatch correctly.
     */
    @Test
    public void testPasswordMismatchValidation() {
        // Fill in the fields with mismatched passwords
        onView(withId(R.id.netIdEditText)).perform(typeText("testUser"));
        onView(withId(R.id.nameEditText)).perform(typeText("Test User"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"));
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("password456"));

        closeSoftKeyboard();
        // Click on the register button
        onView(withId(R.id.registerButton)).perform(click());

        // Check that the appropriate error message is displayed
        // Check that the appropriate error message is displayed in a Toast
        onView(withText("Passwords do not match"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Tests successful user registration with valid data.
     */
    @Test
    public void testUserRegistration() {
        // Fill in the fields correctly
        onView(withId(R.id.netIdEditText)).perform(typeText("testUser"));
        onView(withId(R.id.nameEditText)).perform(typeText("Test User"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"));
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("password123"));
        closeSoftKeyboard();

        // Click on the register button
        onView(withId(R.id.registerButton)).perform(click());

        // Check that MainActivity is launched
        intended(hasComponent(MainActivity.class.getName()));

        // Check that the user is logged in
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        // Assert that the user login key is present and has the expected value
        String loggedInNetId = sharedPreferences.getString("loggedInNetId", "");
        assertNotNull("User login key should not be null", loggedInNetId);
        assertEquals("testUser", loggedInNetId);
    }

    /**
     * Tests navigation back to the login screen from the registration screen.
     */
    @Test
    public void testBackToLoginButton() {
        // Launch the activity
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.signUpButton)).perform(click());
        // Click on the back to login button
        onView(withId(R.id.backToLoginButton)).perform(click());
        // Verify that an intent to the login activity was started
        intended(hasComponent(LoginActivity.class.getName()));
    }
}