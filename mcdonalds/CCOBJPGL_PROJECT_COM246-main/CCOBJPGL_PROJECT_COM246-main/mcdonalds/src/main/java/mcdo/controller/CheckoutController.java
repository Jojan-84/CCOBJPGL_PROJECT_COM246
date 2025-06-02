package mcdo.controller;

import java.io.IOException;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mcdo.App;
import mcdo.model.Cart;
import mcdo.model.OrderItem;

public class CheckoutController {

    @FXML private TableView<OrderItem>          table;
    @FXML private TableColumn<OrderItem,String> colName;
    @FXML private TableColumn<OrderItem,Integer>colQty;
    @FXML private TableColumn<OrderItem,Double> colPrice;
    @FXML private TableColumn<OrderItem,Double> colLineTot;
    @FXML private TableColumn<OrderItem,Void>   colAction;
    @FXML private Label grandTotal;

    @FXML private void initialize() {

        table.setItems(Cart.getItems());

        colName   .setCellValueFactory(d -> new ReadOnlyStringWrapper(
                                            d.getValue().getProduct().getName()));
        colQty    .setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(
                                            d.getValue().getQuantity()));
        colPrice  .setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(
                                            d.getValue().getProduct().getPrice()));
        colLineTot.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(
                                            d.getValue().getLineTotal()));

        /* X button in last column */
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btn = new Button("✕");
            { btn.setOnAction(e ->
                    Cart.remove(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        Cart.getItems().addListener(
            (ListChangeListener<? super OrderItem>) c ->
                grandTotal.setText(String.format("₱ %.2f", Cart.getTotal()))
        );
        grandTotal.setText(String.format("₱ %.2f", Cart.getTotal()));
    }

    @FXML private void backToMenu() throws IOException { App.setRoot("menu"); }

    @FXML private void completeOrder() throws IOException {
        // Navigate to receipt page - the cart will be cleared in the receipt controller
        App.setRoot("receipt");
    }
}
