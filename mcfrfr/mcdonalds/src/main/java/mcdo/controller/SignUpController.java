package mcdo.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import mcdo.App;
import mcdo.model.User;
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

    @FXML private void backToLogin() throws IOException { App.setRoot("welcome"); }
    
    @FXML
    private void initialize() {
        
        // Set up button actions
        signupbutton.setOnAction(event -> {
            try {
                handleSignUp();
            } catch (FileNotFoundException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("sign up button not working");
            }
        });
        backbutton.setOnAction(event -> {
            try {
                App.setRoot(Constants.WELCOME_FXML);
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("failed to go back");
            }
        });
        
        // Clear status label initially
        statuslabel.setText("");
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
    private void handleSignUp() throws FileNotFoundException {
        String username = newusername.getText();
        String password = newpassword.getText();
        String confirmPass = confirmpassword.getText();
        
        User user = new User(username, password, "", "");

        File accountsfile = new File("accounts.txt");
        Scanner filescanner = new Scanner(accountsfile);

        if(username.length() == 0)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("no username provided");
        } else {
            while (filescanner.hasNextLine()) {
                String data = filescanner.nextLine();

                String fileuname = data.split(",")[0];

                if (fileuname.equals(newusername.getText())){

                    System.out.println("multiple username detected");
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setContentText("Username already exists!");
                    clearForm();
                }
            }
        }

        if(password.length() == 0)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("no password provided");
        }

        if(password.length() < 3)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Password must be longer than 3 characters!");
        }

        if(confirmPass.length() == 0)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("no password provided");
        }

        if (!password.equals(confirmPass)) 
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("password do not match");
        }

        // Get current date
        LocalDate today = LocalDate.now();

        // Format as MM-dd-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String formattedDate = today.format(formatter);

        try {

            BufferedWriter myWriter = new BufferedWriter(new FileWriter("accounts.txt", true));
      
            // .write() methods adds content to the file
            myWriter.newLine(); // adds a new line
            myWriter.write(user.getUsername() + "," + user.getPassword() + "," + formattedDate + "," + "customer");

            // Close FileWriter
            myWriter.close();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Account successfully created!");
            alert.setHeaderText("Welcome to Mcdonalds " + username + "!");
            alert.setContentText("You can now log in with your account");
            alert.showAndWait();
            clearForm();
            backToLogin();

        } catch (IOException e) {
            System.out.println("An error occurred.");
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
     
}
