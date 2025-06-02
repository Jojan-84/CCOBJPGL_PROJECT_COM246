package mcdo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Cart {

    /* Single global list the UI can observe  */
    private static final ObservableList<OrderItem> ITEMS =
            FXCollections.observableArrayList();

    private Cart() {}   // no instances

    /* --------------- API ---------------- */
    public static ObservableList<OrderItem> getItems() { return ITEMS; }

    public static void add(Product p) {
        // merge if the item already exists
        ITEMS.stream()
             .filter(oi -> oi.getProduct().equals(p))
             .findFirst()
             .ifPresentOrElse(OrderItem::increment,
                              () -> ITEMS.add(new OrderItem(p)));
    }

    public static void remove(OrderItem oi) { ITEMS.remove(oi); }

    public static void clear() { ITEMS.clear(); }

    public static double getTotal() {
        return ITEMS.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }
}
