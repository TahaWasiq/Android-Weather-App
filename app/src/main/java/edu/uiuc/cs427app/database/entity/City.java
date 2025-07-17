package edu.uiuc.cs427app.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a city entity in the database.
 */
@Entity(tableName = "cities")
public class City {
    @PrimaryKey(autoGenerate = true)
    private int cityId;
    private String cityName;
    public double latitude;
    public double longitude;

    /**
     * Constructor to create a new City object.
     *
     * @param cityName The name of the city.
     */
    public City(String cityName, double latitude, double longitude) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the ID of the city.
     *
     * @return The city ID.
     */
    public int getCityId() { 
        return cityId; 
    }

    /**
     * Sets the ID of the city.
     *
     * @param cityId The city ID to set.
     */
    public void setCityId(int cityId) { 
        this.cityId = cityId; 
    }

    /**
     * Gets the name of the city.
     *
     * @return The city name.
     */
    public String getCityName() { 
        return cityName; 
    }

    /**
     * Sets the name of the city.
     *
     * @param cityName The city name to set.
     */
    public void setCityName(String cityName) { 
        this.cityName = cityName; 
    }
}