package edu.uiuc.cs427app.authentication;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.LoginActivity;
import edu.uiuc.cs427app.MainActivity;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.RegisterActivity;
import edu.uiuc.cs427app.ToastMatcher;

@RunWith(AndroidJUnit4.class)
public class UserLoginTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    /**
     * Sets up the test environment before each test.
     * Initializes intents for verifying navigation.
     */
    @Before
    public void setUp() {
        // Initialize intents for verifying navigation
        init();
    }

    /**
     * Cleans up the test environment after each test.
     * Releases intents.
     */
    @After
    public void tearDown() {
        release();
    }

    /**
     * Tests that an error message is displayed when the login fields are empty.
     */
    @Test
    public void testEmptyFieldsShowErrorMessage() {
        activityRule.launchActivity(new Intent());
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        onView(withText("Please enter your netId and password"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Tests that an error message is displayed when invalid credentials are entered.
     */
    @Test
    public void testInvalidCredentialsShowErrorMessage() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.netIdEditText)).perform(typeText("invalidNetId"));
        onView(withId(R.id.passwordEditText)).perform(typeText("invalidPassword"));
        closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        onView(withText("Invalid netId or password"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Registers a user for testing purposes.
     */
    public void registerUser(){
        // Launch RegisterActivity
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.netIdEditText)).perform(typeText("testUser"));
        onView(withId(R.id.nameEditText)).perform(typeText("Test User"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"));
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("password123"));
        closeSoftKeyboard();

        // Click on the register button
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
    }

    /**
     * Tests successful user login with valid credentials.
     */
    @Test
    public void testUserLogin() {
        registerUser();
        // Mock valid credentials
        String validNetId = "testUser";
        String validPassword = "password123";

        // Launch LoginActivity
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.netIdEditText)).perform(typeText(validNetId));
        onView(withId(R.id.passwordEditText)).perform(typeText(validPassword));
        onView(withId(R.id.loginButton)).perform(click());

        // Verify navigation to MainActivity
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        // Assert that the user login key is present and has the expected value
        String loggedInNetId = sharedPreferences.getString("loggedInNetId", "");
        assertNotNull("User login key should not be null", loggedInNetId);
        assertEquals("testUser", loggedInNetId);
    }

    /**
     * Tests that the signup button navigates to the RegisterActivity.
     */
    @Test
    public void testSignupButtonNavigatesToRegisterActivity() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.signUpButton)).perform(click());

        intended(hasComponent(RegisterActivity.class.getName()));
    }
}