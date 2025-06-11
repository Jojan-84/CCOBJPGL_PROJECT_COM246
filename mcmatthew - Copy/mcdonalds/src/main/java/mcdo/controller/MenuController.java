package mcdo.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mcdo.App;
import mcdo.model.Cart;
import mcdo.model.OrderItem;
import mcdo.model.Product;

public class MenuController {

    /* ---------- FXML nodes ---------- */
    @FXML private ImageView logoImage;
    @FXML private FlowPane  itemsPane;
    @FXML private Label     totalLabel;
    @FXML private Button    checkoutBtn;
    @FXML private VBox      categoryBox;

    /* ---------- Menu data ---------- */
    private List<Product> catalog;

    /* ---------- initialise view ---------- */
    @FXML
    private void initialize() {
        // Initialize catalog with default items
        catalog = new ArrayList<>(List.of(
            new Product("Burger Mcdo",        5.99, "/images/fooditems/burger.png",  Product.Category.BURGER),
            new Product("Cheeseburger",   4.49, "/images/fooditems/cheeseburger.png",  Product.Category.BURGER),
            new Product("McChicken Sandwich",   5.59, "/images/fooditems/chickensandwich.png",  Product.Category.BURGER),
            new Product("Coke",           1.99, "/images/fooditems/coke.png",  Product.Category.DRINK),
            new Product("Coke McFloat",         1.99, "/images/fooditems/mcfloat.png",  Product.Category.DRINK),
            new Product("1pc. Chicken McDo with Rice",  3.99, "/images/fooditems/chickenrice.png",  Product.Category.CHICKEN),
            new Product("2pc. Chicken McDo with Rice",  4.99, "/images/fooditems/2chicken.png",  Product.Category.CHICKEN),
            new Product("Crispy Chicken Fillet with Rice",  5.99, "/images/fooditems/fillet.png",  Product.Category.CHICKEN),
            new Product("6pc. Chicken McNuggets with Fries",  5.99, "/images/fooditems/nuggets.png",  Product.Category.CHICKEN),
            new Product("Cheesy Eggdesal",    2.49, "/images/fooditems/eggdesal.png",  Product.Category.BREAKFAST),
            new Product("Sausage McMuffin with Egg",    2.49, "/images/fooditems/sausage.png",  Product.Category.BREAKFAST),
            new Product("Sausage Platter with Rice",    2.49, "/images/fooditems/sausage1.png",  Product.Category.BREAKFAST),
            new Product("Egg McMuffin",    2.49, "/images/fooditems/egg1.png",  Product.Category.BREAKFAST),
            new Product("Hash Browns",    2.49, "/images/fooditems/hash.png",  Product.Category.BREAKFAST),
            new Product("2pc. Hotcakes with Sausage",    2.49, "/images/fooditems/sausage2.png",  Product.Category.BREAKFAST),
            new Product("2pc. Hotcakes",    2.49, "/images/fooditems/pancake.png",  Product.Category.BREAKFAST),
            new Product("Large Fries",    2.49, "/images/fooditems/fries.png",  Product.Category.FRIES),
            new Product("McSpaghetti",    3.49, "/images/fooditems/mcspag.png",  Product.Category.SPAGHETTI)
        ));

        /*  logo  */
        logoImage.setImage(
                new Image(App.class
                          .getResource("/images/mcdonalds-png-logo-2772.png")
                          .toExternalForm()));

        /*  hook CSS only once  */
        hookCssIfMissing();

        /*  category buttons  */
        categoryBox.getChildren().stream()
                   .filter(n -> n instanceof Button)
                   .map(n -> (Button) n)
                   .forEach(btn -> btn.setOnAction(e -> {
                       try {
                           if (btn.getText().equalsIgnoreCase("ALL FOOD")) {
                               showAllFood();
                           } else {
                               Product.Category cat =
                                       Product.Category.valueOf(btn.getText().toUpperCase());
                               showCategory(cat);
                           }
                       } catch (IllegalArgumentException ex) {
                           System.err.println("Invalid category button: " + btn.getText());
                       }
                   }));

        /*  live cart total - set up once here  */
        Cart.getItems().addListener(
                (ListChangeListener<OrderItem>) c ->
                        updateTotalLabel());
        updateTotalLabel();

        /*  checkout  */
        checkoutBtn.setOnAction(e -> {
            try { App.setRoot("checkout"); }
            catch (IOException ex) { ex.printStackTrace(); }
        });

        /*  default view  */
        showCategory(Product.Category.BURGER);
    }

    /* ---------- Product Management Methods ---------- */
    
    /**
     * Add a new product to the catalog
     * @param product The product to add
     * @return true if the product was added successfully
     */
    public boolean addProduct(Product product) {
        if (product == null) return false;
        boolean added = catalog.add(product);
        if (added) {
            // Refresh the current category view
            Product.Category currentCategory = getCurrentCategory();
            if (currentCategory != null) {
                showCategory(currentCategory);
            }
        }
        return added;
    }

    /**
     * Remove a product from the catalog
     * @param product The product to remove
     * @return true if the product was removed successfully
     */
    public boolean removeProduct(Product product) {
        if (product == null) return false;
        boolean removed = catalog.remove(product);
        if (removed) {
            // Refresh the current category view
            Product.Category currentCategory = getCurrentCategory();
            if (currentCategory != null) {
                showCategory(currentCategory);
            }
        }
        return removed;
    }

    /**
     * Update an existing product in the catalog
     * @param oldProduct The product to update
     * @param newProduct The new product details
     * @return true if the product was updated successfully
     */
    public boolean updateProduct(Product oldProduct, Product newProduct) {
        if (oldProduct == null || newProduct == null) return false;
        int index = catalog.indexOf(oldProduct);
        if (index != -1) {
            catalog.set(index, newProduct);
            // Refresh the current category view
            Product.Category currentCategory = getCurrentCategory();
            if (currentCategory != null) {
                showCategory(currentCategory);
            }
            return true;
        }
        return false;
    }

    /**
     * Get all products in the catalog
     * @return A copy of the product catalog
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(catalog);
    }

    /**
     * Get products by category
     * @param category The category to filter by
     * @return List of products in the specified category
     */
    public List<Product> getProductsByCategory(Product.Category category) {
        return catalog.stream()
                     .filter(p -> p.getCategory() == category)
                     .toList();
    }

    /* ---------- Category Management Methods ---------- */
    
    /**
     * Get the currently displayed category
     * @return The current category or null if no category is selected
     */
    private Product.Category getCurrentCategory() {
        return categoryBox.getChildren().stream()
                         .filter(n -> n instanceof Button)
                         .map(n -> (Button) n)
                         .filter(Button::isFocused)
                         .findFirst()
                         .map(btn -> {
                             try {
                                 return Product.Category.valueOf(btn.getText().toUpperCase());
                             } catch (IllegalArgumentException e) {
                                 return null;
                             }
                         })
                         .orElse(null);
    }

    /* ---------- helpers ---------- */
    private void showCategory(Product.Category cat) {
        itemsPane.getChildren().clear();
        catalog.stream()
               .filter(p -> p.getCategory() == cat)
               .forEach(this::createCard);
    }

    private void showAllFood() {
        itemsPane.getChildren().clear();
        // Group products by category
        java.util.Map<Product.Category, List<Product>> grouped = new java.util.LinkedHashMap<>();
        for (Product p : catalog) {
            grouped.computeIfAbsent(p.getCategory(), k -> new java.util.ArrayList<>()).add(p);
        }
        for (Product.Category cat : Product.Category.values()) {
            List<Product> products = grouped.get(cat);
            if (products != null && !products.isEmpty()) {
                // Create a horizontal separator with category name
                javafx.scene.control.Label catLabel = new javafx.scene.control.Label(cat.toString().charAt(0) + cat.toString().substring(1).toLowerCase());
                catLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 8 16 8 0;");
                javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
                sep.setPrefWidth(800); // Adjust as needed for your layout
                sep.setStyle("-fx-background-color: black; -fx-opacity: 0.5; -fx-padding: 0 0 0 0;");
                javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10, catLabel, sep);
                hbox.setStyle("-fx-alignment: center-left; -fx-padding: 24 0 8 0;");
                itemsPane.getChildren().add(hbox);
                // Add all products in this category
                products.forEach(this::createCard);
            }
        }
    }

    private void createCard(Product p) {
        VBox card = new VBox(6);
        card.getStyleClass().add("product-card");

        ImageView iv = new ImageView(
                new Image(App.class.getResource(p.getImagePath()).toExternalForm(),
                          180, 0, true, true));
        Label name  = new Label(p.getName());
        Label price = new Label(String.format("₱ %.2f", p.getPrice()));
        price.getStyleClass().add("price");

        Button add = new Button("Add");
        add.setOnAction(e -> {
            Cart.add(p);
            updateTotalLabel();
            System.out.println("Added " + p.getName() + " to cart");
        });

        card.getChildren().addAll(iv, name, price, add);

        /*  simple fade + slide-up  */
        card.setOpacity(0);
        card.setTranslateY(30);
        FadeTransition ft = new FadeTransition(Duration.millis(300), card);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), card);
        tt.setToY(0);
        ft.play();
        tt.play();

        itemsPane.getChildren().add(card);
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("₱ %.2f", Cart.getTotal()));
    }

    /** Adds "menu.css" once, if found on the class-path. */
    private void hookCssIfMissing() {
        Scene sc = App.getCurrentScene();
        if (sc == null) return;                       // should never happen after App.start()

        boolean already = sc.getStylesheets()
                             .stream()
                             .anyMatch(s -> s.endsWith("menu.css"));
        if (already) return;

        /* try two typical resource locations */
        URL css =
            App.class.getResource("/css/menu.css") != null
                ? App.class.getResource("/css/menu.css")
                : App.class.getResource("/mcdo/css/menu.css");

        if (css != null) {                            // only add if actually present
            sc.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("[warn] menu.css not found – running without kiosk theme.");
        }
    }
}