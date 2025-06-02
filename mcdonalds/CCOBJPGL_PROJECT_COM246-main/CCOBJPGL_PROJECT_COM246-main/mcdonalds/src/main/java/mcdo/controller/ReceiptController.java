package mcdo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import mcdo.App;
import mcdo.model.Cart;
import mcdo.model.OrderItem;

public class ReceiptController {
    
    @FXML private Label orderNumberLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label cashierLabel;
    @FXML private ListView<String> itemsList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label changeLabel;
    @FXML private Button printButton;
    @FXML private Button newOrderButton;
    
    private static int orderCounter = 1001;
    
    @FXML
    private void initialize() {
        loadReceiptData();
    }
    
    private void loadReceiptData() {
        // Order number
        orderNumberLabel.setText(String.valueOf(orderCounter++));
        
        // Date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
        
        // Cashier
        cashierLabel.setText("Kiosk #01");
        
        // Items list - show all purchased items with quantities
        ObservableList<String> receiptItems = FXCollections.observableArrayList();
        for (OrderItem item : Cart.getItems()) {
            String itemLine = String.format("%dx %s @ ₱%.2f = ₱%.2f",
                item.getQuantity(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getLineTotal());
            receiptItems.add(itemLine);
        }
        itemsList.setItems(receiptItems);
        
        // Calculate totals
        double subtotal = Cart.getTotal();
        double tax = subtotal * 0.12; // 12% VAT in Philippines
        double total = subtotal + tax;
        double amountPaid = total; // Assuming exact payment
        double change = 0.0;
        
        subtotalLabel.setText(String.format("₱ %.2f", subtotal));
        taxLabel.setText(String.format("₱ %.2f", tax));
        totalLabel.setText(String.format("₱ %.2f", total));
        amountPaidLabel.setText(String.format("₱ %.2f", amountPaid));
        changeLabel.setText(String.format("₱ %.2f", change));
    }
    
    @FXML
    private void handlePrint() {
        System.out.println("Receipt printed successfully!");
        System.out.println("=== RECEIPT DETAILS ===");
        System.out.println("Order #" + orderNumberLabel.getText());
        System.out.println("Items purchased:");
        for (OrderItem item : Cart.getItems()) {
            System.out.printf("  %dx %s - ₱%.2f each = ₱%.2f%n",
                item.getQuantity(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getLineTotal());
        }
        System.out.println("Total: " + totalLabel.getText());
        // In a real application, this would interface with a printer
    }
    
    @FXML
    private void handleNewOrder() throws IOException {
        Cart.clear();
        App.setRoot("menu");
    }
}
