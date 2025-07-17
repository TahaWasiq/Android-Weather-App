package edu.uiuc.cs427app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import edu.uiuc.cs427app.database.entity.City;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface CityDao {

    /**
     * Inserts a city into the database.
     * If the city already exists, it replaces it.
     *
     * @param city The city to be inserted.
     * @return The row ID of the newly inserted city.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCity(City city);

    /**
     * Retrieves a city from the database by its name.
     *
     * @param cityName The name of the city to be retrieved.
     * @return The city with the specified name.
     */
    @Query("SELECT * FROM cities WHERE cityName = :cityName")
    City getCityByName(String cityName);

    /**
     * Retrieves a city from the database by its ID.
     *
     * @param cityId The ID of the city to be retrieved.
     * @return The city with the specified ID.
     */
    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    LiveData<City> getCityById(int cityId);

    /**
     * Retrieves all cities from the database ordered by their ID in ascending order.
     *
     * @return A LiveData list of all cities.
     */
    @Query("SELECT * FROM cities ORDER BY cityId ASC")
    LiveData<List<City>> getAllCities();

    /**
     * Deletes a city from the database.
     *
     * @param city The city to be deleted.
     */
    @Delete
    void deleteCity(City city);

    /**
     * Deletes a city from the database using its ID.
     *
     * @param cityId The ID of the city to be deleted.
     */
    @Query("DELETE FROM cities WHERE cityId = :cityId")
    void deleteCityUsingId(int cityId);

    /**
     * Checks if a city exists in the database by its name.
     *
     * @param cityName The name of the city to check.
     * @return True if the city exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM cities WHERE cityName = :cityName)")
    boolean doesCityExist(String cityName);

    /**
     * Retrieves a city from the database by its ID.
     *
     * @param cityId The ID of the city to be retrieved.
     * @return The city with the specified ID.
     */
    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    City getCity(int cityId);
}