package mcdo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry-point for the McDonald's kiosk.
 */
public class App extends Application {

    /* ───────────  singletons  ─────────── */
    private static Stage primaryStage;   // kept for global navigation
    private static Scene scene;          // current scene (always non-null after start)

    private static String orderType;     // “Dine-in” | “Take-out”

    /* ───────────  JavaFX life-cycle  ─────────── */
    @Override
    public void start(Stage stage) throws IOException {
        /* 1️⃣  cache the stage reference immediately */
        primaryStage = stage;

        /* 2️⃣  load first view (“welcome.fxml”) */
        scene = new Scene(loadFXML("welcome"), 1280, 720);

        /* 3️⃣  normal stage plumbing */
        primaryStage.setScene(scene);
        primaryStage.setTitle("McDonald's Kiosk");
        primaryStage.show();
    }

    /* ───────────  navigation helpers  ─────────── */

    /** Switch the root to another FXML (found under /mcdo/fxml/). */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /** Convenience for controllers that need the *live* scene. */
    public static Scene getCurrentScene() {
        return scene;          // always valid after start()
    }

    /** Expose the stage only if you truly need it (e.g. dialogs). */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /* ───────────  order-type (Dine-in / Take-out)  ─────────── */
    public static void   setOrderType(String type) { orderType = type; }
    public static String getOrderType()            { return orderType; }

    /* ───────────  helpers  ─────────── */

    /** Loads an FXML from the class-path location “/mcdo/fxml/<name>.fxml”. */
    private static Parent loadFXML(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/mcdo/fxml/" + name + ".fxml"));
        return loader.load();
    }

    public static void main(String[] args) { launch(args); }
}
