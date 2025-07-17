package edu.uiuc.cs427app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.database.entity.UserCityPair;

@Dao
public interface UserCityPairDao {

    /**
     * Fetches all cities associated with a particular user.
     *
     * @param userId The ID of the user whose cities are to be fetched.
     * @return A LiveData list of cities associated with the user.
     */
    @Query("SELECT cities.* FROM cities " +
            "INNER JOIN UserCityPairs ON cities.cityId = UserCityPairs.cityID " +
            "WHERE UserCityPairs.userID = :userId")
    LiveData<List<City>> getCitiesForUser(int userId);

    /**
     * Inserts a new user-city pair into the database.
     * If the pair already exists, it replaces it.
     *
     * @param userCityPair The user-city pair to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserCityPair userCityPair);

    /**
     * Deletes a user-city pair from the database using the city ID.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to be deleted.
     */
    @Query("DELETE FROM UserCityPairs WHERE userID = :userId AND cityID = :cityId")
    void deleteCityPairUsingCityId(int userId, int cityId);

    /**
     * Checks if a city exists for a particular user.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to check.
     * @return True if the city exists for the user, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM UserCityPairs " +
            "WHERE UserCityPairs.userID = :userId AND UserCityPairs.cityID = :cityId)")
    boolean doesCityExistForUser(int userId, int cityId);
}
