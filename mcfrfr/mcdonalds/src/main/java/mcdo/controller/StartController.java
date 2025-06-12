package mcdo.controller;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mcdo.App;
import mcdo.model.User;
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



    @FXML
    private void initialize() {
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
    public void loginbuttonHandler(ActionEvent event) throws IOException{
        String username = usernametextfield.getText();
        String password = passwordfield.getText();

        User user = new User(username, password, "", "");

        File accountsfile = new File("accounts.txt");

        if (accountsfile.exists()){
            Scanner filescanner = new Scanner(accountsfile);

            while (filescanner.hasNextLine()) {
                String data = filescanner.nextLine();

                String fileuname = data.split(",")[0];
                String filepword = data.split(",")[1];
                String filestatus = data.split(",")[3];

                if (fileuname.equals(user.getUsername()) && filepword.equals(user.getPassword()) && filestatus.equals("customer")){

                    System.out.println("login successful");
                    goOrderType();
                }

                if (fileuname.equals(user.getUsername()) && filepword.equals(user.getPassword()) && filestatus.equals("admin")){

                    System.out.println("login successful");
                    goAdmin();
                }
            }
        }
    }

    private void goOrderType() {
        try {
            App.setRoot(Constants.ORDER_TYPE_FXML);
        } catch (IOException e) {
            System.err.println("Error navigating to menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goAdmin() {
        try {
            App.setRoot(Constants.ADMIN_FXML);
        } catch (IOException e) {
            System.err.println("Error navigating to menu: " + e.getMessage());
            e.printStackTrace();
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