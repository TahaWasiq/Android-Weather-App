package edu.uiuc.cs427app.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.uiuc.cs427app.database.entity.User;
import edu.uiuc.cs427app.database.repository.UserRepository;
import kotlinx.coroutines.flow.Flow;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<List<User>> allUsers;

    public User currentUser;

    /**
     * This is to initialize the UserViewModel with the application context.
     * Sets up the UserRepository and fetches all users.
     *
     * @param application The application context.
     */
    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
        allUsers = repository.getAllUsers();
    }

    /**
     * Retrieves a LiveData list of all users from the database.
     * 
     * @return LiveData object that contains a list of all users.
     */
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    /**
     * Inserts a user into the database.
     * 
     * @param user The user object that needs to be inserted.
     */
    public void insertUser(User user) {
        repository.insertUser(user);
    }

    /**
     * Deletes a user from the database.
     * 
     * @param user The User object that needs to be deleted.
     */
    public void deleteUser(User user) {
        repository.deleteUser(user);
    }

    /**
     * Checks if a user with the specified netId and password exists in the database.
     *
     * @param netId The user's netID.
     * @param password The user's password.
     * @return True if a matching user exists, false otherwise.
     */
    public boolean isValidUser(String netId, String password) {
        return repository.isValidUser(netId, password);
    }

    /**
     * Retrieves the theme preference for a specified user.
     * 
     * @param netId The netID of the user.
     * @return LiveData containing the theme preference of the user.
     */
    public LiveData<String> getThemeForUser(String netId) {
        return repository.getThemeForUser(netId);
    }

    /**
     * Saves the current user session data in SharedPreferences.
     *
     * @param user The User object of the currently logged-in user.
     */
    public void saveCurrentUser(User user) {
        currentUser = user;
        SharedPreferences sharedPref = getApplication().getSharedPreferences("user", getApplication().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("loggedInNetId", user.getNetId());
        editor.putString("theme", user.getTheme());
        System.out.println("GOD: " + user.getId());
        editor.apply();
    }

    /**
     * Retrieves a user using their netID.
     *
     * @param netId The netID of the user.
     * @return The User object with the specified netID.
     */
    public User getUserByNetId(String netId) {
        return repository.getUserByNetId(netId);
    }

    /**
     * Retrieves the current user that is logged in from memory or SharedPreferences if it is not already set.
     *
     * @return The User object representing the currently logged-in user.
     */
    public User getCurrentUser() {
        if (currentUser == null) {
            SharedPreferences sharedPref = getApplication().getSharedPreferences("user", getApplication().MODE_PRIVATE);
            String loggedInNetId = sharedPref.getString("loggedInNetId", "");
            if (!loggedInNetId.equals("")) {
                currentUser = repository.getUserByNetId(loggedInNetId);
            }
        }
        return currentUser;
    }

    /**
     * Logs out the current user by clearing session data from SharedPreferences.
     */
    public void logout() {
        currentUser = null;
        SharedPreferences sharedPref = getApplication().getSharedPreferences("user", getApplication().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("loggedInNetId");
        editor.apply();
    }
}