package mcdo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mcdo.App;

public final class Cart {

    /* Single global list the UI can observe  */
    private static final ObservableList<OrderItem> ITEMS =
            FXCollections.observableArrayList();

    private Cart() {}   // no instances

    /* --------------- API ---------------- */
    public static ObservableList<OrderItem> getItems() { 
        return ITEMS; 
    }

    public static void add(Product p) {
        if (p == null) {
            System.err.println("Cannot add null product to cart");
            return;
        }
        
        try {
            // Find existing item with the same product name
            ITEMS.stream()
                 .filter(oi -> oi.getProduct().getName().equals(p.getName()))
                 .findFirst()
                 .ifPresentOrElse(OrderItem::increment,
                                  () -> ITEMS.add(new OrderItem(p)));
        } catch (Exception e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
        }
    }

    public static void remove(OrderItem oi) { 
        if (oi != null) {
            ITEMS.remove(oi); 
        }
    }

    public static void clear() { 
        ITEMS.clear(); 
    }

    public static double getTotal() {
        try {
            return ITEMS.stream().mapToDouble(OrderItem::getLineTotal).sum();
        } catch (Exception e) {
            System.err.println("Error calculating cart total: " + e.getMessage());
            return 0.0;
        }
    }
    
    public static int getItemCount() {
        return ITEMS.stream().mapToInt(OrderItem::getQuantity).sum();
    }
    
    public static boolean isEmpty() {
        return ITEMS.isEmpty();
    }

    /**
     * Saves the current cart contents to a text file in the project's receipts directory
     * @param filename The name of the file to save to (without extension)
     * @return true if the save was successful, false otherwise
     */
    public static boolean saveToFile(String filename) {
        if (isEmpty()) {
            System.err.println("Cannot save empty cart to file");
            return false;
        }

        try {
            // Get the project's root directory
            String projectPath = System.getProperty("user.dir");
            File receiptsDir = new File(projectPath, "receipts");
            
            // Create receipts directory if it doesn't exist
            if (!receiptsDir.exists()) {
                boolean created = receiptsDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create receipts directory");
                    return false;
                }
            }

            File outputFile = new File(receiptsDir, filename + ".txt");
            System.out.println("Attempting to save to: " + outputFile.getAbsolutePath());

            try (FileWriter writer = new FileWriter(outputFile)) {
                // Write header
                writer.write("==========================================\n");
                writer.write("              MCDONALD'S ORDER            \n");
                writer.write("==========================================\n\n");
                
                // Write date and time
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                writer.write("Date: " + now.format(formatter) + "\n");
                
                // Write order type
                String orderType = App.getOrderType();
                if (orderType != null && !orderType.isEmpty()) {
                    writer.write("Order Type: " + orderType + "\n");
                } else {
                    writer.write("Order Type: Dine In\n");
                }
                
                // Write user information
                String currentUser = App.getCurrentUser();
                if (currentUser != null && !currentUser.isEmpty()) {
                    writer.write("Ordered by: " + currentUser + "\n");
                }
                writer.write("\n");
                
                // Write items
                writer.write("ITEMS ORDERED:\n");
                writer.write("------------------------------------------\n");
                for (OrderItem item : ITEMS) {
                    writer.write(String.format("%-30s ₱%.2f\n", 
                        item.getProduct().getName(), 
                        item.getProduct().getPrice()));
                    writer.write(String.format("  Quantity: %d\n", item.getQuantity()));
                    writer.write(String.format("  Subtotal: ₱%.2f\n", item.getLineTotal()));
                    writer.write("------------------------------------------\n");
                }
                
                // Write total
                writer.write("\nTOTAL AMOUNT: ₱" + String.format("%.2f", getTotal()) + "\n");
                writer.write("==========================================\n");
                
                System.out.println("Successfully saved to: " + outputFile.getAbsolutePath());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error saving cart to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
