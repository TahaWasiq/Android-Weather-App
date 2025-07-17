package edu.uiuc.cs427app.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.uiuc.cs427app.database.AppDatabase;
import edu.uiuc.cs427app.database.dao.CityDao;
import edu.uiuc.cs427app.database.entity.City;



/**
 * Repository class that abstracts access to multiple data sources.
 * Manages query threads and allows the use of multiple backends.
 */
public class CityRepository {
    private final CityDao cityDao;

    /**
     * Constructor that initializes the CityDao.
     *
     * @param application The application context.
     */
    public CityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        cityDao = db.cityDao();
    }

    /**
     * Inserts a city into the database.
     *
     * @param city The city to be inserted.
     * @return The row ID of the newly inserted city.
     */
    public long insertCity(City city) {
        Callable<Long> insertTask = () -> cityDao.insertCity(city);

        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(insertTask);
        try {
            return future.get(); // This will wait for the result and return the inserted ID
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1; // Or handle the exception as needed
        }
    }

    /**
     * Deletes a city from the database.
     *
     * @param city The city to be deleted.
     */
    public void deleteCity(City city) {
        AppDatabase.databaseWriteExecutor.execute(() -> cityDao.deleteCity(city));
    }

    /**
     * Deletes a city from the database using its ID.
     *
     * @param cityId The ID of the city to be deleted.
     */
    public void deleteCityUsingId(int cityId) {
        AppDatabase.databaseWriteExecutor.execute(() -> cityDao.deleteCityUsingId(cityId));
    }

    /**
     * Checks if a city exists in the database by its name.
     *
     * @param cityName The name of the city to check.
     * @return True if the city exists, false otherwise.
     */
    public boolean doesCityExist(String cityName) {
        Callable<Boolean> isValidCityTask = () -> cityDao.doesCityExist(cityName);

        Future<Boolean> future = AppDatabase.databaseWriteExecutor.submit(isValidCityTask);
        try {
            return future.get(); // This will wait for the result and return the inserted ID
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false; // Or handle the exception as needed
        }
    }

    /**
     * Retrieves all cities from the database ordered by their ID in ascending order.
     *
     * @return A LiveData list of all cities.
     */
    public LiveData<List<City>> getAllCities() {
        return cityDao.getAllCities();
    }

    /**
     * Retrieves a list of cities from the database using their IDs.
     *
     * @param cityIds The list of city IDs.
     * @return A list of cities with the specified IDs.
     */
    public List<City> getCitiesByCitiesId(List<Integer> cityIds) {
        List<City> cities = new ArrayList<>();
        for (int cityId : cityIds) {
            cities.add(cityDao.getCity(cityId));
        }
        return cities;
    }

    /**
     * Retrieves a city from the database by its name.
     *
     * @param cityName The name of the city to be retrieved.
     * @return The city with the specified name.
     */
    public City getCityByName(String cityName) {
        return cityDao.getCityByName(cityName);
    }

    /**
     * Retrieves a city from the database by its ID.
     *
     * @param cityId The ID of the city to be retrieved.
     * @return The city with the specified ID.
     */
    public LiveData<City> getCityById(int cityId){return cityDao.getCityById(cityId);}

}
