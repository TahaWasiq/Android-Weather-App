package edu.uiuc.cs427app.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.uiuc.cs427app.database.AppDatabase;
import edu.uiuc.cs427app.database.dao.UserCityPairDao;
import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.database.entity.UserCityPair;

/**
 * Repository class that abstracts access to multiple data sources for UserCityPair entities.
 * Manages query threads and allows the use of multiple backends.
 */
public class UserCityPairRepository {

    private final UserCityPairDao userCityPairDao;

    /**
     * Constructor to initialize the UserCityPairDao.
     *
     * @param application The application context.
     */
    public UserCityPairRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application); // Assume AppDatabase is your Room database
        userCityPairDao = db.userCityPairDao();
    }

    /**
     * Fetches cities for a particular user.
     *
     * @param userId The ID of the user whose cities are to be fetched.
     * @return A LiveData list of cities associated with the user.
     */
    public LiveData<List<City>> getCitiesForUser(int userId) {
        return userCityPairDao.getCitiesForUser(userId);
    }

    /**
     * Checks if a city exists for a particular user.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to check.
     * @return True if the city exists for the user, false otherwise.
     */
    public boolean doesCityExistForUser(int userId, int cityId) {
        return userCityPairDao.doesCityExistForUser(userId, cityId);
    }

    /**
     * Inserts a new user-city pair into the database.
     *
     * @param userCityPair The user-city pair to be inserted.
     */
    public void insertUserCityPair(UserCityPair userCityPair) {
        AppDatabase.databaseWriteExecutor.execute(() -> userCityPairDao.insert(userCityPair));
    }

    /**
     * Deletes a user-city pair from the database using the city ID.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to be deleted.
     */
    public void deleteCityPairUsingCityId(int userId, int cityId) {
        AppDatabase.databaseWriteExecutor.execute(() -> userCityPairDao.deleteCityPairUsingCityId(userId, cityId));
    }
}