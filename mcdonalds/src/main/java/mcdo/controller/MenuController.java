package mcdo.controller;

import java.io.IOException;
import java.net.URL;
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
    private final List<Product> catalog = List.of(
        new Product("Big Mac",        5.99, "/images/fooditems/pngwing.com.png",  Product.Category.BURGER),
        new Product("Cheeseburger",   4.49, "/images/fooditems/pngwing.com.png",  Product.Category.BURGER),
        new Product("Coke",           1.99, "/images/fooditems/pngwing.com.png",  Product.Category.DRINK),
        new Product("Sprite",         1.99, "/images/fooditems/pngwing.com.png",  Product.Category.DRINK),
        new Product("Fried Chicken",  3.99, "/images/fooditems/pngwing.com.png",  Product.Category.CHICKEN),
        new Product("Large Fries",    2.49, "/images/fooditems/pngwing.com.png",  Product.Category.FRIES),
        new Product("McSpaghetti",    3.49, "/images/fooditems/pngwing.com.png",  Product.Category.SPAGHETTI)
    );

    /* ---------- initialise view ---------- */
    @FXML
    private void initialize() {

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
                           Product.Category cat =
                                   Product.Category.valueOf(btn.getText().toUpperCase());
                           showCategory(cat);
                       } catch (IllegalArgumentException ex) {
                           System.err.println("Invalid category button: " + btn.getText());
                       }
                   }));

        /*  live cart total  */
        Cart.getItems().addListener(
                (ListChangeListener<OrderItem>) c ->
                        totalLabel.setText(String.format("₱ %.2f", Cart.getTotal())));
        totalLabel.setText(String.format("₱ %.2f", Cart.getTotal()));

        /*  checkout  */
        checkoutBtn.setOnAction(e -> {
            try { App.setRoot("checkout"); }
            catch (IOException ex) { ex.printStackTrace(); }
        });

        /*  default view  */
        showCategory(Product.Category.BURGER);
    }

    /* ---------- helpers ---------- */
    private void showCategory(Product.Category cat) {
        itemsPane.getChildren().clear();
        catalog.stream()
               .filter(p -> p.getCategory() == cat)
               .forEach(this::createCard);
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
        add.setOnAction(e -> Cart.add(p));

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

    /** Adds “menu.css” once, if found on the class-path. */
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
