package kiosk.model;

public class MenuItem {
    private final int id;
    private final String name;
    private final double price;
    private final String category;   // e.g. "Burgers", "Drinks"

    public MenuItem(int id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public int    getId()       { return id; }
    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public String getCategory() { return category; }
}
