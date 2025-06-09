package mcdo.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mcdo.App;
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
    
    @FXML
    private void initialize() {
        // Set up button actions
        signupbutton.setOnAction(event -> handleSignUp());
        backbutton.setOnAction(event -> {
            try {
                App.setRoot(Constants.WELCOME_FXML);
            } catch (IOException e) {
                showErrorAlert(Constants.NAVIGATION_ERROR_TITLE, "Could not return to login page: " + e.getMessage());
            }
        });
    }
    
    private void handleSignUp() {
        String username = newusername.getText();
        String password = newpassword.getText();
        String confirmPass = confirmpassword != null ? confirmpassword.getText() : "";
        
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            showErrorAlert(Constants.SIGNUP_ERROR_TITLE, Constants.ERROR_EMPTY_USERNAME);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showErrorAlert(Constants.SIGNUP_ERROR_TITLE, Constants.ERROR_EMPTY_PASSWORD);
            return;
        }
        
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            showErrorAlert(Constants.SIGNUP_ERROR_TITLE, Constants.ERROR_PASSWORD_TOO_SHORT);
            return;
        }
        
        if (confirmpassword != null && !password.equals(confirmPass)) {
            showErrorAlert(Constants.SIGNUP_ERROR_TITLE, Constants.ERROR_PASSWORDS_DONT_MATCH);
            return;
        }
        
        // For now, just show a message that sign up is not fully implemented
        // In a real application, you would save the user to a database or file
        showInfoAlert("Sign Up", "Sign up functionality is not fully implemented yet.\n" +
                     "Please use existing accounts:\n" +
                     "- Username: matthew, Password: 123\n" +
                     "- Username: juhan, Password: tanginamo");
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
