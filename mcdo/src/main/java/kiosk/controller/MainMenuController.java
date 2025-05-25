package kiosk.controller;

import javafx.event.ActionEvent;
import kiosk.SceneManager;

public class MainMenuController {
    public void handleStart(ActionEvent e){ SceneManager.switchTo("MenuScreen.fxml"); }
    public void handleExit(ActionEvent e){ System.exit(0); }
}
