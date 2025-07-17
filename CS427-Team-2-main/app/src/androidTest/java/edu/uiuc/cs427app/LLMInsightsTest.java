package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for verifying LLM insights-related functionalities.
 * This class tests the addition of cities and the functionality of the "Generate Insights" button.
 */
@RunWith(AndroidJUnit4.class)
public class LLMInsightsTest {

    private String city0 = "Champaign";
    private String city1 = "Chicago";

    /**
     * Sets up the test environment before each test.
     * Registers a new user and adds cities to the list of locations.
     */
    @Before
    public void setUp() throws InterruptedException {
        ActivityScenario.launch(RegisterActivity.class);

        onView(withId(R.id.nameEditText)).perform(typeText("Test User"),
                closeSoftKeyboard());
        onView(withId(R.id.netIdEditText)).perform(typeText("testUser"),
                closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"),
                closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("password123"),
                closeSoftKeyboard());
        Thread.sleep(1000);

        // Click on the register button
        onView(withId(R.id.registerButton)).perform(click());

        add_city(city0);
        add_city(city1);
    }

    /**
     * Adds a city to the list of locations.
     * @param city The name of the city to add.
     */
    public void add_city(String city) {
        onView(withId(R.id.buttonAddLocation)).perform(click());
        ViewInteraction perform = onView(withId(R.id.autoCompleteCityName)).perform(replaceText(city));
        onView(withId(R.id.seeMatches)).perform(click());
        // Click the first element in the adapter.
        onData(anything()).atPosition(0).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.addLocationButton)).perform(click());
    }

    /**
     * Tests that LLM insights are shown for the added cities.
     */
    @Test
    public void testLLMInsightsShown() throws InterruptedException {
        onView(allOf(
                hasDescendant(withText("Champaign, Illinois, US")),
                isDescendantOfA(withId(R.id.locationListLayout))))
                .check(matches(isDisplayed()));

        onView(allOf(
                withText("Weather"), // Text of the "Map" button
                isDescendantOfA(allOf(
                        hasDescendant(withText("Champaign, Illinois, US")), // Ensure it's in the layout containing "Boston"
                        isDescendantOfA(withId(R.id.locationListLayout))))))
                .perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.generateInsights)).perform(click());
        Thread.sleep(3000);
        onView(childAtPosition(withId(R.id.insightsContainer), 0))
                .perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.insightsContainer))
                .check(matches(hasTextViewChild()));

        pressBack();
        pressBack();

        onView(allOf(
                hasDescendant(withText("Chicago, Illinois, US")),
                isDescendantOfA(withId(R.id.locationListLayout))))
                .check(matches(isDisplayed()));

        onView(allOf(
                withText("Weather"), // Text of the "Map" button
                isDescendantOfA(allOf(
                        hasDescendant(withText("Chicago, Illinois, US")), // Ensure it's in the layout containing "Boston"
                        isDescendantOfA(withId(R.id.locationListLayout))))))
                .perform(click());
        Thread.sleep(3000);

        Thread.sleep(1000);

        onView(withId(R.id.generateInsights)).perform(click());
        Thread.sleep(3000);
        onView(childAtPosition(withId(R.id.insightsContainer), 0))
                .perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.insightsContainer))
                .check(matches(hasTextViewChild()));

        Thread.sleep(2000);
    }

    /**
     * Matches a child view at a specific position within a parent view.
     * @param parentMatcher The matcher for the parent view.
     * @param position The position of the child view within the parent view.
     * @return A matcher for the child view at the specified position.
     */
    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (view.getParent() instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) view.getParent();
                    return parentMatcher.matches(parent) && parent.getChildAt(position) == view;
                }
                return false;
            }
        };
    }

    /**
     * Matches a ViewGroup that contains at least one TextView.
     * @return A matcher for a ViewGroup containing at least one TextView.
     */
    private static Matcher<View> hasTextViewChild() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        if (group.getChildAt(i) instanceof TextView) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a ViewGroup containing at least one TextView");
            }
        };
    }
}