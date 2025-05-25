package kiosk.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Order {
    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

    /** adds 1 or increments existing */
    public void add(MenuItem item){
        orderItems.stream()
                  .filter(oi->oi.getMenuItem().getId()==item.getId())
                  .findFirst()
                  .ifPresentOrElse(
                     oi->oi.setQuantity(oi.getQuantity()+1),
                     ()->orderItems.add(new OrderItem(item))
                  );
    }
    public void remove(OrderItem oi){ orderItems.remove(oi); }
    public ObservableList<OrderItem> getItems(){ return orderItems; }

    public double getTotal(){
        return orderItems.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }
    public void clear(){ orderItems.clear(); }
}
