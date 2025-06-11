package mcdo.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mcdo.App;
import mcdo.service.AuthenticationService;
import mcdo.util.Constants;

public class StartController {

    @FXML
    private Label usernamelabel;

    @FXML
    private Label passwordlabel;

    @FXML
    private TextField usernametextfield;

    @FXML
    private PasswordField passwordfield;

    @FXML
    private Button loginbutton;

    @FXML
    private Button signupbutton;

    private AuthenticationService authService;

    @FXML
    private void initialize() {
        authService = new AuthenticationService();
        
        // Set up sign up button action
        signupbutton.setOnAction(event -> {
            try {
                App.setRoot(Constants.SIGNUP_FXML);
            } catch (IOException e) {
                showErrorAlert(Constants.NAVIGATION_ERROR_TITLE, "Could not open sign up page: " + e.getMessage());
            }
        });
    }

    @FXML
    public void loginbuttonHandler(ActionEvent event) {
        String username = usernametextfield.getText();
        String password = passwordfield.getText();

        // Input validation
        if (username == null || username.trim().isEmpty()) {
            showErrorAlert(Constants.LOGIN_ERROR_TITLE, Constants.ERROR_EMPTY_USERNAME);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showErrorAlert(Constants.LOGIN_ERROR_TITLE, Constants.ERROR_EMPTY_PASSWORD);
            return;
        }

        // Authenticate user
        if (authService.authenticate(username, password)) {
            System.out.println(Constants.LOGIN_SUCCESS_MSG + username);
            // Set the current user
            App.setCurrentUser(username);
            try {
                App.setRoot(Constants.ORDER_TYPE_FXML);
            } catch (IOException e) {
                showErrorAlert(Constants.NAVIGATION_ERROR_TITLE, "Could not proceed to order selection: " + e.getMessage());
            }
        } else {
            showErrorAlert(Constants.LOGIN_FAILED_TITLE, Constants.ERROR_INVALID_CREDENTIALS);
            // Clear password field for security
            passwordfield.clear();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}