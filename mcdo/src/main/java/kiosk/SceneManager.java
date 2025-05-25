package kiosk;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primary;

    public static void setPrimaryStage(Stage stage) {
        primary = stage;
    }

    public static void switchTo(String fxml) {
        try {
            // Load FXML from resources folder
            String fxmlPath = "/kiosk/view/" + fxml;
            System.out.println("Loading FXML: " + fxmlPath);

            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            primary.setScene(new Scene(root));
            primary.show(); // <-- Ensures the window is displayed
        } catch (IOException | NullPointerException e) {
            System.err.println("Failed to load FXML: " + fxml);
            e.printStackTrace();
        }
    }
}
