package mcdo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import mcdo.App;

public class OrderTypeController {

    @FXML
    private Button dinein;

    @FXML
    private Button takeout;

    @FXML
    private void initialize() {
        // When "Dine In" is clicked
        dinein.setOnAction(event -> {
            App.setOrderType("Dine In");
            goToMenu();
        });

        // When "Take Out" is clicked
        takeout.setOnAction(event -> {
            App.setOrderType("Take Out");
            goToMenu();
        });
    }

    private void goToMenu() {
        try {
            App.setRoot("menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
