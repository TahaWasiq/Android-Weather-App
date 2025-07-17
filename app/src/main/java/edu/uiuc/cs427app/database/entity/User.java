package edu.uiuc.cs427app.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a user entity in the database.
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String netId;
    private String password;
    private String theme;

    /**
     * Constructor to create a new User object.
     *
     * @param name The name of the user.
     * @param email The email (netId) of the user.
     * @param password The password of the user.
     * @param theme The theme preference of the user.
     */
    public User(String name, String email, String password, String theme) {
        this.name = name;
        this.netId = email;
        this.password = password;
        this.theme = theme;
    }

    /**
     * Default constructor.
     */
    public User() {
        // Default constructor
    }

    /**
     * Gets the ID of the user.
     *
     * @return The user ID.
     */
    public int getId() { 
        return id; 
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The user ID to set.
     */
    public void setId(int id) { 
        this.id = id; 
    }

    /**
     * Gets the name of the user.
     *
     * @return The user name.
     */
    public String getName() { 
        return name; 
    }

    /**
     * Sets the name of the user.
     *
     * @param name The user name to set.
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * Gets the netId (email) of the user.
     *
     * @return The user netId.
     */
    public String getNetId() { 
        return netId; 
    }

    /**
     * Sets the netId (email) of the user.
     *
     * @param email The user netId to set.
     */
    public void setNetId(String email) { 
        this.netId = email; 
    }

    /**
     * Gets the password of the user.
     *
     * @return The user password.
     */
    public String getPassword() { 
        return password; 
    }

    /**
     * Sets the password of the user.
     *
     * @param password The user password to set.
     */
    public void setPassword(String password) { 
        this.password = password; 
    }

    /**
     * Gets the theme preference of the user.
     *
     * @return The user theme preference.
     */
    public String getTheme() { 
        return theme; 
    }

    /**
     * Sets the theme preference of the user.
     *
     * @param theme The user theme preference to set.
     */
    public void setTheme(String theme) { 
        this.theme = theme; 
    }
}