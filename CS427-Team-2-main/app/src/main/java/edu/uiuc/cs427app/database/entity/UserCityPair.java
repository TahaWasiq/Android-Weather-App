package edu.uiuc.cs427app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Represents a user-city pair entity in the database.
 * This entity establishes a many-to-many relationship between users and cities.
 */
@Entity(
        tableName = "UserCityPairs",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userID", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = City.class, parentColumns = "cityId", childColumns = "cityID", onDelete = ForeignKey.CASCADE)
        }
)
public class UserCityPair {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userID;
    private int cityID;

    /**
     * Constructor to create a new UserCityPair object.
     *
     * @param userID The ID of the user.
     * @param cityID The ID of the city.
     */
    public UserCityPair(int userID, int cityID) {
        this.userID = userID;
        this.cityID = cityID;
    }

    /**
     * Default constructor.
     */
    public UserCityPair() {
        // Default constructor
    }

    /**
     * Gets the ID of the user-city pair.
     *
     * @return The user-city pair ID.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Sets the ID of the user-city pair.
     *
     * @param id The user-city pair ID to set.
     */
    public void setId(int id) { 
        this.id = id; 
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public int getUserID() { 
        return userID; 
    }

    /**
     * Sets the user ID.
     *
     * @param userID The user ID to set.
     */
    public void setUserID(int userID) { 
        this.userID = userID; 
    }

    /**
     * Gets the city ID.
     *
     * @return The city ID.
     */
    public int getCityID() { 
        return cityID; 
    }

    /**
     * Sets the city ID.
     *
     * @param cityID The city ID to set.
     */
    public void setCityID(int cityID) { 
        this.cityID = cityID; 
    }
}