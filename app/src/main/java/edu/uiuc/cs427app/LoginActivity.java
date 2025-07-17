package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.uiuc.cs427app.database.entity.User;
import edu.uiuc.cs427app.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_login);

        // Find buttons by their IDs
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signUpButton);

        // Set click listeners for the buttons
        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);

        // Initialize the UserViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    /**
     * Handles click events for the buttons.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                // Call the login method to handle user login
                login();
                break;
            case R.id.signUpButton:
                // Navigate to the RegisterActivity
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Handles the user login process.
     * Validates the input fields and checks if the user exists in the database.
     */
    private void login() {
        // Get the user's input from the EditText fields
        EditText netIdText = findViewById(R.id.netIdEditText);
        EditText passwordText = findViewById(R.id.passwordEditText);

        String netId = netIdText.getText().toString();
        String password = passwordText.getText().toString();

        // Check if the netId and password are valid
        if (netId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your netId and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is valid
        boolean isValid = userViewModel.isValidUser(netId, password);
        if (isValid) {
            User user = userViewModel.getUserByNetId(netId);
            // If valid, redirect to MainActivity
            userViewModel.saveCurrentUser(user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }

        // If invalid, show an error message
        Toast.makeText(this, "Invalid netId or password", Toast.LENGTH_SHORT).show();
    }
}