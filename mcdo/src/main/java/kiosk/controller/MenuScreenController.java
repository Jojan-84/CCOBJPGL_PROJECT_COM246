package kiosk.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import kiosk.SceneManager;
import kiosk.model.MenuItem;
import kiosk.session.OrderSession;

public class MenuScreenController {
    @FXML private TabPane tabPane;

    private final List<MenuItem> MENU = List.of(
        new MenuItem(1,"Cheeseburger",89,"Burgers"),
        new MenuItem(2,"Big Mac",149,"Burgers"),
        new MenuItem(3,"Fries",49,"Sides"),
        new MenuItem(4,"McFloat",39,"Drinks"),
        new MenuItem(5,"Chicken McDo",120,"Meals")
    );

    @FXML
    public void initialize(){
        Map<String,List<MenuItem>> byCat = MENU.stream()
                                               .collect(Collectors.groupingBy(MenuItem::getCategory));

        byCat.forEach((cat,items)->{
            ScrollPane scroll = new ScrollPane();
            FlowPane  flow = new FlowPane(10,10);
            flow.setPadding(new Insets(10));
            scroll.setContent(flow);

            items.forEach(mi->flow.getChildren().add(createCard(mi)));

            Tab tab = new Tab(cat, scroll);
            tabPane.getTabs().add(tab);
        });
    }

    private VBox createCard(MenuItem mi){
        Label name  = new Label(mi.getName());
        Label price = new Label("₱" + mi.getPrice());
        Button add  = new Button("Add");
        add.setOnAction(e -> {
            OrderSession.get().add(mi);
            add.setText("Added ✔");
        });

        VBox card = new VBox(name, price, add);
        card.getStyleClass().add("card");
        card.setMinSize(120,120);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        return card;
    }

    public void goToOrder(){ SceneManager.switchTo("OrderSummary.fxml"); }
}
