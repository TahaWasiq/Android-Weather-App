package edu.uiuc.cs427app;

import android.view.View;
import android.widget.Toast;
import androidx.test.espresso.matcher.BoundedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import android.os.IBinder;
import android.view.WindowManager;
import androidx.test.espresso.Root;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom matcher for identifying Toast messages in Espresso tests.
 */
public class ToastMatcher extends TypeSafeMatcher<Root> {

    /**
     * Determines if the given root is a Toast.
     * @param root The root to check.
     * @return True if the root is a Toast, false otherwise.
     */
    @Override
    protected boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            return windowToken == appToken;
        }
        return false;
    }

    /**
     * Describes the matcher.
     * @param description The description to append to.
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("is a Toast");
    }
}

