package mcdo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import mcdo.App;
import mcdo.model.Cart;
import mcdo.model.OrderItem;

public class ReceiptController {
    
    @FXML private Label orderNumberLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label cashierLabel;
    @FXML private ScrollPane itemsScrollPane;
    @FXML private VBox itemsContainer;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label changeLabel;
    
    private List<OrderItem> completedOrder;
    
    @FXML private void initialize() {
        // Store the current cart items before clearing
        completedOrder = new ArrayList<>(Cart.getItems());
        System.out.println("Cart has" + Cart.getItems().size() + "stuff" );
        System.out.println("Completed order size: " + completedOrder.size());
        
        // Generate order number
        Random random = new Random();
        int orderNumber = 1000 + random.nextInt(9000);
        orderNumberLabel.setText(String.valueOf(orderNumber));
        
        // Set current date/time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
        
        // Set cashier
        cashierLabel.setText("Kiosk #01");
        
        // Populate items list
        populateItemsList();
        
        // Calculate totals
        calculateTotals();
        
        // Clear the cart after displaying receipt
        Cart.clear();
    }
    
    private void populateItemsList() {
        itemsContainer.getChildren().clear();
        
        for (OrderItem item : completedOrder) {
            // Create main container for each item
            VBox itemBox = new VBox(3);
            itemBox.setStyle("-fx-padding: 12 0; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");
            
            // Top row: Product name and line total
            HBox topRow = new HBox();
            topRow.setStyle("-fx-alignment: center-left;");
            
            Label nameLabel = new Label(item.getProduct().getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label lineTotalLabel = new Label(String.format("₱ %.2f", item.getLineTotal()));
            lineTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            topRow.getChildren().addAll(nameLabel, spacer, lineTotalLabel);
            
            // Bottom row: Quantity and unit price
            Label detailLabel = new Label(String.format("Qty: %d × ₱%.2f", 
                item.getQuantity(), item.getProduct().getPrice()));
            detailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            
            itemBox.getChildren().addAll(topRow, detailLabel);
            itemsContainer.getChildren().add(itemBox);
        }
    }
    
    private void calculateTotals() {
        double total = completedOrder.stream()
            .mapToDouble(OrderItem::getLineTotal)
            .sum();
        
        double subtotal = total / 1.12; // Assuming 12% VAT included
        double tax = total - subtotal;
        
        subtotalLabel.setText(String.format("₱ %.2f", subtotal));
        taxLabel.setText(String.format("₱ %.2f", tax));
        totalLabel.setText(String.format("₱ %.2f", total));
        amountPaidLabel.setText(String.format("₱ %.2f", total));
        changeLabel.setText("₱ 0.00");
    }
    
    @FXML private void handlePrint() {
        System.out.println("Printing receipt...");
        // Add actual print functionality here
    }
    
    @FXML private void handleNewOrder() throws IOException {
        App.setRoot("menu");
    }
}
