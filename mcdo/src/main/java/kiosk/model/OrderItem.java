package kiosk.model;

import javafx.beans.property.*;

public class OrderItem {
    private final MenuItem menuItem;
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final DoubleProperty  lineTotal = new SimpleDoubleProperty();

    public OrderItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        recalcTotal();
        quantity.addListener((obs,o,n)->recalcTotal());
    }
    private void recalcTotal() {
        lineTotal.set(menuItem.getPrice() * quantity.get());
    }

    public MenuItem       getMenuItem()         { return menuItem; }
    public int            getQuantity()         { return quantity.get(); }
    public void           setQuantity(int q)    { quantity.set(q); }
    public IntegerProperty quantityProperty()   { return quantity; }
    public double         getLineTotal()        { return lineTotal.get(); }
    public ReadOnlyDoubleProperty lineTotalProperty(){ return lineTotal; }
}
