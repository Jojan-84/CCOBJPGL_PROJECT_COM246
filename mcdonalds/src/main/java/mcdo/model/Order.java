package mcdo.model;

import java.util.List;

public class Order {
    private String orderType; // "Dine In" or "Take Out"
    private List<String> items;

    public Order(String orderType, List<String> items) {
        this.orderType = orderType;
        this.items = items;
    }

    public String getOrderType() {
        return orderType;
    }

    public List<String> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Type: ").append(orderType).append("\n");
        sb.append("Items:\n");
        for (String item : items) {
            sb.append("- ").append(item).append("\n");
        }
        return sb.toString();
    }
}
