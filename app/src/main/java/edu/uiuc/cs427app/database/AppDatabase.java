package edu.uiuc.cs427app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uiuc.cs427app.database.dao.CityDao;
import edu.uiuc.cs427app.database.dao.UserCityPairDao;
import edu.uiuc.cs427app.database.dao.UserDao;
import edu.uiuc.cs427app.database.entity.City;
import edu.uiuc.cs427app.database.entity.User;
import edu.uiuc.cs427app.database.entity.UserCityPair;

/**
 * AppDatabase is the main database class for the application.
 * It provides access to the DAOs and manages the database creation and version management.
 */
@Database(entities = {User.class, City.class, UserCityPair.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    // Define the DAO
    public abstract UserDao userDao();
    public abstract CityDao cityDao();
    public abstract UserCityPairDao userCityPairDao();

    // Executor service for background tasks
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Singleton instance to get the database.
     *
     * @param context The application context.
     * @return The singleton instance of the AppDatabase.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .allowMainThreadQueries() // Allow queries on the main thread (not recommended for production)
                            .fallbackToDestructiveMigration() // Handle migrations by destroying and recreating the database
                            .build();
                }
            }
        }
        return instance;
    }
}