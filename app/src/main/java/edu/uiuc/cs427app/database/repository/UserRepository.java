package edu.uiuc.cs427app.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.uiuc.cs427app.database.AppDatabase;
import edu.uiuc.cs427app.database.dao.UserDao;
import edu.uiuc.cs427app.database.entity.User;

/**
 * Repository class that abstracts access to multiple data sources for User entities.
 * Manages query threads and allows the use of multiple backends.
 */
public class UserRepository {
    private final UserDao userDao;

    /**
     * Constructor to initialize the UserDao.
     *
     * @param application The application context.
     */
    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A LiveData list of all users.
     */
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    /**
     * Inserts a user into the database in the background.
     *
     * @param user The user to be inserted.
     */
    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insertUser(user));
    }

    /**
     * Deletes a user from the database in the background.
     *
     * @param user The user to be deleted.
     */
    public void deleteUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.deleteUser(user));
    }

    /**
     * Checks if a user with the specified netId and password exists in the database.
     *
     * @param netId The netId of the user.
     * @param password The password of the user.
     * @return True if the user exists, false otherwise.
     */
    public boolean isValidUser(String netId, String password) {
        return userDao.isValidUser(netId, password);
    }

    /**
     * Retrieves a user from the database using their netId.
     *
     * @param netId The netId of the user to be retrieved.
     * @return The user with the specified netId.
     */
    public User getUserByNetId(String netId) {
        return userDao.getUserByNetId(netId);
    }

    /**
     * Retrieves a user from the database using their ID.
     *
     * @param userId The ID of the user to be retrieved.
     * @return The user with the specified ID.
     */
    public User getUserById(int userId) {
        return userDao.getUser(userId);
    }

    /**
     * Retrieves the theme preference for a given user.
     *
     * @param netId The netId of the user whose theme preference is to be retrieved.
     * @return A LiveData object containing the theme preference of the user.
     */
    public LiveData<String> getThemeForUser(String netId) {
        return userDao.getThemeForUser(netId);
    }
}