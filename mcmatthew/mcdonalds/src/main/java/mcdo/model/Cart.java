package mcdo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
            // merge if the item already exists
            ITEMS.stream()
                 .filter(oi -> oi.getProduct().equals(p))
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
}
