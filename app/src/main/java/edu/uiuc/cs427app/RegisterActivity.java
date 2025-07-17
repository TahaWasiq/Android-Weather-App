package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.uiuc.cs427app.database.entity.User;
import edu.uiuc.cs427app.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private UserViewModel userViewModel;

    /**
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Find buttons by their IDs
        Button loginButton = findViewById(R.id.backToLoginButton);
        Button signupButton = findViewById(R.id.registerButton);

        // Set click listeners for the buttons
        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
    }

    /**
     * Handles click events for the buttons.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginButton:
                // Finish the activity and return to the previous screen
                finish();
                break;
            case R.id.registerButton:
                // Call the register method to handle user registration
                register();
                break;
        }
    }

    /**
     * Handles the user registration process.
     * Validates the input fields and inserts a new user into the database.
     */
    private void register() {
        // Get the user's input from the EditText fields
        EditText netIdText = findViewById(R.id.netIdEditText);
        EditText fullNameText = findViewById(R.id.nameEditText);
        EditText passwordText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordText = findViewById(R.id.confirmPasswordEditText);
        Spinner themeSelector = findViewById(R.id.themeSelector);

        String name = fullNameText.getText().toString();
        String netId = netIdText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();
        String selectedTheme = themeSelector.getSelectedItem().toString();

        // Check if any fields are empty
        if (name.isEmpty() || netId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("netId", netId);
        editor.putString("theme", selectedTheme);
        editor.apply();


        // Create a new user object
        User newUser = new User(name, netId, password, selectedTheme);

        // Insert the new user into the database
        userViewModel.insertUser(newUser);
        userViewModel.saveCurrentUser(newUser);

        // Navigate to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}