package edu.uiuc.cs427app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class ThemeUtils {

    private static final String PREFS_NAME = "user";
    private static final String THEME_KEY = "theme";

    // Set theme based on SharedPreferences
    /** This method a theme to the given activity based on the user's saved preference.
     * The theme preference is stored in SharedPreferences.
     *
     * Themes available:
     * - "Dark" applies a dark theme style.
     * - "Light" applies a light theme style.
     * - "Purple" applies a purple-themed style (default).
     *
     * If no theme preference is found, "Purple" theme is applied by default.
     */
    public static void applyTheme(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String theme = preferences.getString(THEME_KEY, "Purple");

        switch (theme) {
            case "Dark":
                activity.setTheme(R.style.Theme_MyFirstApp_Dark);
                break;
            case "Light":
                activity.setTheme(R.style.Theme_MyFirstApp_Light);
                break;
            case "Purple":
                activity.setTheme(R.style.Theme_MyFirstApp_Purple);
                break;
            default:
                activity.setTheme(R.style.Theme_MyFirstApp_Purple);
                break;
        }
    }
}