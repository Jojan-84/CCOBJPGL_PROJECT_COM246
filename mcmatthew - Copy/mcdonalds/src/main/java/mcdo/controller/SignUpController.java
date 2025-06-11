package mcdo.controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import mcdo.App;
import mcdo.service.AuthenticationService;
import mcdo.util.Constants;

public class SignUpController {
    
    @FXML
    private TextField newusername;
    
    @FXML
    private PasswordField newpassword;
    
    @FXML
    private PasswordField confirmpassword;
    
    @FXML
    private Button signupbutton;
    
    @FXML
    private Button backbutton;
    
    @FXML
    private Label statuslabel;
    
    private AuthenticationService authService;
    
    @FXML
    private void initialize() {
        authService = new AuthenticationService();
        
        // Set up button actions
        signupbutton.setOnAction(event -> handleSignUp());
        backbutton.setOnAction(event -> {
            try {
                App.setRoot(Constants.WELCOME_FXML);
            } catch (IOException e) {
                showErrorAlert(Constants.NAVIGATION_ERROR_TITLE, "Could not return to login page: " + e.getMessage());
            }
        });
        
        // Add real-time validation feedback
        setupRealTimeValidation();
        
        // Clear status label initially
        statuslabel.setText("");
    }
    
    /**
     * Set up real-time validation feedback as user types
     */
    private void setupRealTimeValidation() {
        // Username validation
        newusername.textProperty().addListener((observable, oldValue, newValue) -> {
            validateUsernameRealTime(newValue);
        });
        
        // Password validation
        newpassword.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordRealTime(newValue);
        });
        
        // Confirm password validation
        confirmpassword.textProperty().addListener((observable, oldValue, newValue) -> {
            validateConfirmPasswordRealTime(newValue);
        });
    }
    
    /**
     * Real-time username validation
     */
    private void validateUsernameRealTime(String username) {
        if (username == null || username.trim().isEmpty()) {
            setStatusMessage("", Color.BLACK);
            return;
        }
        
        username = username.trim();
        
        if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            setStatusMessage("Username too long (max " + Constants.MAX_USERNAME_LENGTH + " chars)", Color.RED);
            return;
        }
        
        if (username.contains(",") || username.contains("\n") || username.contains("\r")) {
            setStatusMessage("Username contains invalid characters", Color.RED);
            return;
        }
        
        if (authService.userExists(username)) {
            setStatusMessage("Username already exists", Color.RED);
            return;
        }
        
        setStatusMessage("Username available ✓", Color.GREEN);
    }
    
    /**
     * Real-time password validation
     */
    private void validatePasswordRealTime(String password) {
        if (password == null || password.trim().isEmpty()) {
            return;
        }
        
        password = password.trim();
        
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            setStatusMessage("Password too short (min " + Constants.MIN_PASSWORD_LENGTH + " chars)", Color.RED);
            return;
        }
        
        if (password.length() > Constants.MAX_PASSWORD_LENGTH) {
            setStatusMessage("Password too long (max " + Constants.MAX_PASSWORD_LENGTH + " chars)", Color.RED);
            return;
        }
        
        if (password.contains(",") || password.contains("\n") || password.contains("\r")) {
            setStatusMessage("Password contains invalid characters", Color.RED);
            return;
        }
        
        // Check if passwords match if confirm password is filled
        String confirmPass = confirmpassword.getText();
        if (confirmPass != null && !confirmPass.isEmpty()) {
            validateConfirmPasswordRealTime(confirmPass);
        }
    }
    
    /**
     * Real-time confirm password validation
     */
    private void validateConfirmPasswordRealTime(String confirmPass) {
        String password = newpassword.getText();
        
        if (confirmPass == null || confirmPass.isEmpty()) {
            return;
        }
        
        if (password == null || password.isEmpty()) {
            setStatusMessage("Please enter password first", Color.RED);
            return;
        }
        
        if (!password.equals(confirmPass)) {
            setStatusMessage("Passwords do not match", Color.RED);
            return;
        }
        
        setStatusMessage("Passwords match ✓", Color.GREEN);
    }
    
    /**
     * Set status message with color
     */
    private void setStatusMessage(String message, Color color) {
        Platform.runLater(() -> {
            statuslabel.setText(message);
            statuslabel.setTextFill(color);
        });
    }
    
    /**
     * Handle sign up button click
     */
    private void handleSignUp() {
        String username = newusername.getText();
        String password = newpassword.getText();
        String confirmPass = confirmpassword.getText();
        
        // Clear any previous status messages
        setStatusMessage("Processing...", Color.BLUE);
        
        try {
            // Input validation
            if (username == null || username.trim().isEmpty()) {
                setStatusMessage(Constants.ERROR_EMPTY_USERNAME, Color.RED);
                newusername.requestFocus();
                return;
            }
            
            if (password == null || password.trim().isEmpty()) {
                setStatusMessage(Constants.ERROR_EMPTY_PASSWORD, Color.RED);
                newpassword.requestFocus();
                return;
            }
            
            if (confirmPass == null || confirmPass.trim().isEmpty()) {
                setStatusMessage("Please confirm your password", Color.RED);
                confirmpassword.requestFocus();
                return;
            }
            
            // Check if passwords match
            if (!password.equals(confirmPass)) {
                setStatusMessage(Constants.ERROR_PASSWORDS_DONT_MATCH, Color.RED);
                confirmpassword.clear();
                confirmpassword.requestFocus();
                return;
            }
            
            // Attempt to register user
            boolean success = authService.registerUser(username, password);
            
            if (success) {
                // Registration successful
                showSuccessAlert("Registration Successful", 
                    "Welcome " + username + "!\n\nYour account has been created successfully.\n\nYou can now log in with your new credentials.");
                
                // Clear form
                clearForm();
                
                // Navigate back to login
                try {
                    App.setRoot(Constants.WELCOME_FXML);
                } catch (IOException e) {
                    showErrorAlert(Constants.NAVIGATION_ERROR_TITLE, "Registration successful but could not return to login page: " + e.getMessage());
                }
            } else {
                // Username already exists
                setStatusMessage("Username already exists. Please choose a different username.", Color.RED);
                newusername.selectAll();
                newusername.requestFocus();
            }
            
        } catch (IllegalArgumentException e) {
            // Validation error
            setStatusMessage(e.getMessage(), Color.RED);
            
            // Focus appropriate field based on error
            if (e.getMessage().contains("Username")) {
                newusername.requestFocus();
            } else if (e.getMessage().contains("Password")) {
                newpassword.requestFocus();
            }
            
        } catch (Exception e) {
            // Unexpected error
            setStatusMessage("Registration failed: " + e.getMessage(), Color.RED);
            showErrorAlert("Registration Error", "An unexpected error occurred during registration:\n\n" + e.getMessage());
        }
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        newusername.clear();
        newpassword.clear();
        confirmpassword.clear();
        setStatusMessage("", Color.BLACK);
    }
    
    /**
     * Show success alert
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("🎉 Success!");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show error alert
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("❌ Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
