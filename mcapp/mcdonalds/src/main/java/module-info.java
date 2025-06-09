module com.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens mcdo to javafx.graphics;
    opens mcdo.controller to javafx.fxml;

    exports mcdo;
}
