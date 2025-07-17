package edu.uiuc.cs427app;
import android.content.Context;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import androidx.test.espresso.matcher.RootMatchers;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.click;
import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.content.Intent;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * Test adding Champaign, Chicago and LA.
 */
@RunWith(AndroidJUnit4.class)
public class AddRemoveLocationTest {
    private String test_name = "TestUser";
    private String test_uid = "TestUserID";
    private String test_password = "123";
    private String city0 = "Champaign";
    private String cityName0 = "Champaign, Illinois, US";
    private String city1 = "Chicago";
    private String cityName1 = "Chicago, Illinois, US";
    private String city2 = "Los Angeles";
    private String cityName2 = "Los Angeles, California, US";

    /**
     * Launches the application by starting the RegisterActivity.
     */
    private void launchApp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent();
        intent.setClass(context, RegisterActivity.class);
        ActivityScenario.launch(intent);
    }

    /**
     * Adds a city to the list of locations.
     * @param city The name of the city to add.
     */
    private void add_city(String city) {
        onView(withId(R.id.buttonAddLocation)).perform(click());
        ViewInteraction perform = onView(withId(R.id.autoCompleteCityName)).perform(replaceText(city));
        onView(withId(R.id.seeMatches)).perform(click());
        // Click the first element in the adapter.
        onData(anything()).atPosition(0).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.addLocationButton)).perform(click());
    }

    /**
     * Removes a city from the list of locations.
     * @param city The name of the city to remove.
     */
    private void remove_city(String city) {
        onView(withId(R.id.buttonRemoveLocation)).perform(click());
        onView(withText(containsString(city))).perform(click());
    }

    /**
     * Initializes an account by registering a new user.
     */
    private void initAccount(){
        launchApp();
        onView(withId(R.id.nameEditText)).perform(replaceText(test_name));
        onView(withId(R.id.netIdEditText)).perform(replaceText(test_uid));
        onView(withId(R.id.passwordEditText)).perform(replaceText(test_password));
        onView(withId(R.id.confirmPasswordEditText)).perform(replaceText(test_password));
        onView(withId(R.id.registerButton)).perform(click());
    }

    /**
     * Tests adding and removing cities from the list of locations.
     */
    @Test
    public void testAddAndRemoveCity() {
        initAccount();

        add_city(city0);
        add_city(city1);
        add_city(city2);

        onView(withText(containsString(city0))).check(matches(withText(cityName0)));
        onView(withText(containsString(city1))).check(matches(withText(cityName1)));
        onView(withText(containsString(city2))).check(matches(withText(cityName2)));

        add_city(city2);
        onView(withText("City already exist!"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        remove_city(city0);
        onView(withText(containsString(city0))).check(doesNotExist());
        remove_city(city1);
        onView(withText(containsString(city1))).check(doesNotExist());
        remove_city(city2);
        onView(withText(containsString(city2))).check(doesNotExist());
    }

}