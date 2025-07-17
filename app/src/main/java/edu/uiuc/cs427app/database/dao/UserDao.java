package edu.uiuc.cs427app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.uiuc.cs427app.database.entity.User;

@Dao
public interface UserDao {

    /**
     * Inserts a user into the database.
     * If the user already exists, it replaces it.
     *
     * @param user The user to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    /**
     * Retrieves all users from the database ordered by their name in ascending order.
     *
     * @return A LiveData list of all users.
     */
    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<User>> getAllUsers();

    /**
     * Deletes a specific user from the database.
     *
     * @param user The user to be deleted.
     */
    @Delete
    void deleteUser(User user);

    /**
     * Checks if a user with the specified netId and password exists in the database.
     *
     * @param netId The netId of the user.
     * @param password The password of the user.
     * @return True if the user exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM users WHERE netId = :netId AND password = :password)")
    boolean isValidUser(String netId, String password);

    /**
     * Retrieves a user from the database using their ID.
     *
     * @param id The ID of the user to be retrieved.
     * @return The user with the specified ID.
     */
    @Query("SELECT * FROM users WHERE id = :id")
    User getUser(int id);

    /**
     * Retrieves a user from the database using their netId.
     *
     * @param netId The netId of the user to be retrieved.
     * @return The user with the specified netId.
     */
    @Query("SELECT * FROM users WHERE netId = :netId")
    User getUserByNetId(String netId);

    /**
     * Retrieves the theme preference for a given user.
     *
     * @param netId The netId of the user whose theme preference is to be retrieved.
     * @return A LiveData object containing the theme preference of the user.
     */
    @Query("SELECT theme FROM users WHERE netId = :netId")
    LiveData<String> getThemeForUser(String netId);
}