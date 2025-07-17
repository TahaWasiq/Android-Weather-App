package edu.uiuc.cs427app.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.database.entity.UserCityPair;
import edu.uiuc.cs427app.database.repository.CityRepository;
import edu.uiuc.cs427app.database.repository.UserCityPairRepository;

/**
 * ViewModel class for managing UI-related data for cities.
 * It provides a layer of abstraction between the UI and the data sources.
 */
public class CityViewModel extends AndroidViewModel {
    private final CityRepository repository;
    private final UserCityPairRepository userCityPairRepository;

    /**
     * Constructor to initialize the repositories.
     *
     * @param application The application context.
     */
    public CityViewModel(Application application) {
        super(application);
        repository = new CityRepository(application);
        userCityPairRepository = new UserCityPairRepository(application);
    }

    /**
     * Retrieves cities associated with a particular user.
     *
     * @param userId The ID of the user whose cities are to be fetched.
     * @return A LiveData list of cities associated with the user.
     */
    public LiveData<List<City>> getCitiesByUserId(int userId) {
        return userCityPairRepository.getCitiesForUser(userId);
    }

    /**
     * Checks if a city exists in the database by its name.
     *
     * @param cityName The name of the city to check.
     * @return True if the city exists, false otherwise.
     */
    public boolean doesCityExist(String cityName) {
        return repository.doesCityExist(cityName);
    }

    /**
     * Inserts a city into the database.
     *
     * @param city The city to be inserted.
     * @return The row ID of the newly inserted city.
     */
    public long insertCity(City city) {
        return repository.insertCity(city);
    }


    public void deleteCityUsingCityId(int cityId) {
        repository.deleteCityUsingId(cityId);
    }
    /**
     * Deletes a user-city pair from the database using the city ID.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to be deleted.
     */
    public void deleteCityPairUsingCityId(int userId, int cityId) {
        userCityPairRepository.deleteCityPairUsingCityId(userId, cityId);
    }

    /**
     * Inserts a new user-city pair into the database.
     *
     * @param pair The user-city pair to be inserted.
     */
    public void insertUserCityPair(UserCityPair pair) {
        userCityPairRepository.insertUserCityPair(pair);
    }

    /**
     * Retrieves a city from the database by its name.
     *
     * @param cityName The name of the city to be retrieved.
     * @return The city with the specified name.
     */
    public City getCityUsingName(String cityName) {
        return repository.getCityByName(cityName);
    }
    /**
     * Retrieves a list of cities from the database by their IDs.
     *
     * @param cityIds The IDs of the cities to be retrieved.
     * @return List of cities with the specified IDs.
     */
    public List<City> getCitiesUsingId(List<Integer> cityIds){return repository.getCitiesByCitiesId(cityIds);}


    /**
     * Checks if a city exists for a particular user.
     *
     * @param userId The ID of the user.
     * @param cityId The ID of the city to check.
     * @return True if the city exists for the user, false otherwise.
     */
    public boolean doesCityExistForUser(int userId, int cityId) {
        return userCityPairRepository.doesCityExistForUser(userId, cityId);
    }

    /**
     * Retrieves a city from the database using its ID.
     *
     * @param cityId The ID of the city to be retrieved.
     * @return The city with the specified ID.
     */
    public LiveData<City> getCityById(int cityId) {
        return repository.getCityById(cityId);
    }
}