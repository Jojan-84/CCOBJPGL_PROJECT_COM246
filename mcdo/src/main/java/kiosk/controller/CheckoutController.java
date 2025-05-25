package kiosk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import kiosk.SceneManager;
import kiosk.session.OrderSession;
import kiosk.util.CurrencyHelper;

public class CheckoutController {
    @FXML private Label lblTotal;

    @FXML public void initialize(){
        lblTotal.setText(CurrencyHelper.fmt(OrderSession.get().getTotal()));
    }

    public void handleConfirm(){
        OrderSession.get().clear();
        SceneManager.switchTo("ThankYou.fxml");
    }
    public void handleCancel(){
        SceneManager.switchTo("OrderSummary.fxml");
    }
}
