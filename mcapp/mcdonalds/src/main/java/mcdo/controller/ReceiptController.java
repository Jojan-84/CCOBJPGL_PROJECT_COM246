package mcdo.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mcdo.App;
import mcdo.model.Cart;
import mcdo.model.OrderItem;

public class ReceiptController {
    
    @FXML private Label orderNumberLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label cashierLabel;
    @FXML private ListView<VBox> itemsList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label changeLabel;
    
    private List<OrderItem> completedOrder;
    
    @FXML private void initialize() {
        // Store the current cart items before clearing
        completedOrder = new ArrayList<>(Cart.getItems());
        
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
        itemsList.getItems().clear();
        
        for (OrderItem item : completedOrder) {
            VBox itemBox = new VBox(2);
            itemBox.setStyle("-fx-padding: 5 0 5 0;");
            
            // Item name and total price
            HBox nameRow = new HBox();
            nameRow.setStyle("-fx-alignment: center-left;");
            Label nameLabel = new Label(item.getProduct().getName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            Label totalPriceLabel = new Label(String.format("₱ %.2f", item.getLineTotal()));
            totalPriceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            
            nameRow.getChildren().addAll(nameLabel);
            nameRow.setSpacing(10);
            
            // Quantity and unit price details
            HBox detailRow = new HBox();
            detailRow.setStyle("-fx-alignment: center-left;");
            Label detailLabel = new Label(String.format("Qty: %d × ₱%.2f", 
                item.getQuantity(), item.getProduct().getPrice()));
            detailLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
            
            detailRow.getChildren().add(detailLabel);
            
            // Add price label to the right side
            HBox fullRow = new HBox();
            fullRow.setStyle("-fx-alignment: center-left;");
            VBox leftSide = new VBox(2);
            leftSide.getChildren().addAll(nameRow, detailRow);
            
            fullRow.getChildren().addAll(leftSide);
            HBox.setHgrow(leftSide, javafx.scene.layout.Priority.ALWAYS);
            
            Label priceLabel = new Label(String.format("₱ %.2f", item.getLineTotal()));
            priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            fullRow.getChildren().add(priceLabel);
            
            itemBox.getChildren().add(fullRow);
            itemsList.getItems().add(itemBox);
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