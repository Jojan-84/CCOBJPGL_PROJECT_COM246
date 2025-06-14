package mcdo.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.geometry.Pos;
import mcdo.util.Constants;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;

import java.util.ResourceBundle;

public class MenuController implements Initializable {

    /* ---------- FXML nodes ---------- */
    @FXML private ImageView logoImage;
    @FXML private FlowPane  itemsPane;
    @FXML private Label     totalLabel;
    @FXML private Label     emptyCartLabel;
    @FXML private Button    checkoutBtn;
    @FXML private VBox      categoryBox;

    /* ---------- Menu data ---------- */
    private List<Product> catalog;

    /* ---------- initialise view ---------- */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize catalog with default items
        catalog = new ArrayList<>(List.of(
            new Product("Burger Mcdo", 5.99, "/images/fooditems/burger.png", Product.Category.BURGER, 
                "Classic beef patty with onions, pickles, mustard, and ketchup"),
            new Product("Cheeseburger", 4.49, "/images/fooditems/cheeseburger.png", Product.Category.BURGER,
                "Beef patty with cheese, onions, pickles, mustard, and ketchup"),
            new Product("McChicken Sandwich", 5.59, "/images/fooditems/chickensandwich.png", Product.Category.BURGER,
                "Crispy chicken fillet with lettuce and creamy mayonnaise"),
            new Product("Coke", 1.99, "/images/fooditems/coke.png", Product.Category.DRINK,
                "Refreshing Coca-Cola soft drink"),
            new Product("Coke McFloat", 1.99, "/images/fooditems/mcfloat.png", Product.Category.DRINK,
                "Coca-Cola with vanilla soft serve"),
            new Product("1pc. Chicken McDo with Rice", 3.99, "/images/fooditems/chickenrice.png", Product.Category.CHICKEN,
                "One piece of crispy fried chicken with steamed rice"),
            new Product("2pc. Chicken McDo with Rice", 4.99, "/images/fooditems/2chicken.png", Product.Category.CHICKEN,
                "Two pieces of crispy fried chicken with steamed rice"),
            new Product("Crispy Chicken Fillet with Rice", 5.99, "/images/fooditems/fillet.png", Product.Category.CHICKEN,
                "Crispy chicken fillet with steamed rice"),
            new Product("6pc. Chicken McNuggets with Fries", 5.99, "/images/fooditems/nuggets.png", Product.Category.CHICKEN,
                "Six pieces of crispy chicken nuggets with fries"),
            new Product("Cheesy Eggdesal", 2.49, "/images/fooditems/eggdesal.png", Product.Category.BREAKFAST,
                "Egg and cheese on a soft bun"),
            new Product("Sausage McMuffin with Egg", 2.49, "/images/fooditems/sausage.png", Product.Category.BREAKFAST,
                "Sausage patty and egg on an English muffin"),
            new Product("Sausage Platter with Rice", 2.49, "/images/fooditems/sausage1.png", Product.Category.BREAKFAST,
                "Sausage with scrambled eggs and rice"),
            new Product("Egg McMuffin", 2.49, "/images/fooditems/egg1.png", Product.Category.BREAKFAST,
                "Egg and cheese on an English muffin"),
            new Product("Hash Browns", 2.49, "/images/fooditems/hash.png", Product.Category.BREAKFAST,
                "Crispy golden hash browns"),
            new Product("2pc. Hotcakes with Sausage", 2.49, "/images/fooditems/sausage2.png", Product.Category.BREAKFAST,
                "Two hotcakes with sausage patty"),
            new Product("2pc. Hotcakes", 2.49, "/images/fooditems/pancake.png", Product.Category.BREAKFAST,
                "Two fluffy hotcakes with syrup"),
            new Product("Large Fries", 2.49, "/images/fooditems/fries.png", Product.Category.FRIES,
                "Crispy golden french fries"),
            new Product("McSpaghetti", 3.49, "/images/fooditems/mcspag.png", Product.Category.SPAGHETTI,
                "Spaghetti with sweet-style sauce and ground beef")
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
                (ListChangeListener<OrderItem>) c -> {
                    updateTotalLabel();
                    updateCartState();
                });
        updateTotalLabel();
        updateCartState();

        /*  checkout  */
        checkoutBtn.setOnAction(e -> {
            try { App.setRoot("checkout"); }
            catch (IOException ex) { ex.printStackTrace(); }
        });

        /*  default view  */
        showCategory(Product.Category.BURGER);
    }

    @FXML
    private void handleBack() throws IOException {
        // Clear the cart before going back
        Cart.clear();
        // Clear the order type
        App.setOrderType(null);
        // Return to order type selection
        App.setRoot(Constants.ORDER_TYPE_FXML);
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
                         .filter(btn -> !btn.getText().equals("← Back")) // Exclude back button
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
        Label name = new Label(p.getName());
        Label description = new Label(p.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        Label price = new Label(String.format("₱ %.2f", p.getPrice()));
        price.getStyleClass().add("price");

        // Create quantity controls
        HBox quantityBox = new HBox(8);
        quantityBox.setAlignment(Pos.CENTER);
        
        Button minusBtn = new Button("−");
        minusBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
        
        Label quantityLabel = new Label("0");
        quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 30px; -fx-alignment: center;");
        
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #51cf66; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
        
        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);

        // Add to cart button
        Button addBtn = new Button("Add to Cart");
        addBtn.getStyleClass().add("add-btn");
        addBtn.setDisable(true);

        // Set up quantity controls
        minusBtn.setOnAction(e -> {
            int currentQty = Integer.parseInt(quantityLabel.getText());
            if (currentQty > 0) {
                final int newQty = currentQty - 1;
                quantityLabel.setText(String.valueOf(newQty));
                addBtn.setDisable(newQty == 0);
                
                // Find existing item in cart
                Cart.getItems().stream()
                    .filter(item -> item.getProduct().getName().equals(p.getName()))
                    .findFirst()
                    .ifPresent(item -> Cart.updateItemQuantity(item, newQty));
                
                updateTotalLabel();
            }
        });

        plusBtn.setOnAction(e -> {
            int currentQty = Integer.parseInt(quantityLabel.getText());
            final int newQty = currentQty + 1;
            quantityLabel.setText(String.valueOf(newQty));
            addBtn.setDisable(false);
            
            // Find existing item in cart
            Cart.getItems().stream()
                .filter(item -> item.getProduct().getName().equals(p.getName()))
                .findFirst()
                .ifPresent(item -> Cart.updateItemQuantity(item, newQty));
            
            updateTotalLabel();
        });

        // Add to cart action
        addBtn.setOnAction(e -> {
            int qty = Integer.parseInt(quantityLabel.getText());
            if (qty > 0) {
                OrderItem item = new OrderItem(p);
                item.setQuantity(qty);
                Cart.add(item);
                quantityLabel.setText("0");
                addBtn.setDisable(true);
                updateTotalLabel();
            }
        });

        card.getChildren().addAll(iv, name, description, price, quantityBox, addBtn);
        itemsPane.getChildren().add(card);
    }

    private void updateCartState() {
        boolean isEmpty = Cart.isEmpty();
        checkoutBtn.setDisable(isEmpty);
        emptyCartLabel.setVisible(isEmpty);
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