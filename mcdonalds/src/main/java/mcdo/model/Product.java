package mcdo.model;

public class Product {
    public enum Category { BURGER, DRINK, CHICKEN, FRIES, SPAGHETTI }

    private final String   name;
    private final double   price;
    private final String   imagePath;    // e.g. "/images/fooditems/bigmac.png"
    private final Category category;

    public Product(String name, double price, String imagePath, Category cat) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = cat;
    }

    /* ---- getters ---- */
    public String   getName()      { return name;      }
    public double   getPrice()     { return price;     }
    public String   getImagePath() { return imagePath; }
    public Category getCategory()  { return category;  }
}
