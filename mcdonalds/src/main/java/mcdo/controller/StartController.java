package mcdo.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import mcdo.App;

public class StartController {

    @FXML
    private Button start;

    @FXML
    private void initialize() {
        start.setOnAction(event -> {
            try {
                App.setRoot("order_type");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
