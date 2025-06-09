package mcdo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import mcdo.App;
import mcdo.util.Constants;

public class OrderTypeController {

    @FXML
    private Button dinein;

    @FXML
    private Button takeout;

    @FXML
    private void initialize() {
        // When "Dine In" is clicked
        dinein.setOnAction(event -> {
            App.setOrderType(Constants.ORDER_TYPE_DINE_IN);
            System.out.println("Order type selected: " + Constants.ORDER_TYPE_DINE_IN);
            goToMenu();
        });

        // When "Take Out" is clicked
        takeout.setOnAction(event -> {
            App.setOrderType(Constants.ORDER_TYPE_TAKE_OUT);
            System.out.println("Order type selected: " + Constants.ORDER_TYPE_TAKE_OUT);
            goToMenu();
        });
    }

    private void goToMenu() {
        try {
            App.setRoot(Constants.MENU_FXML);
        } catch (IOException e) {
            System.err.println("Error navigating to menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
