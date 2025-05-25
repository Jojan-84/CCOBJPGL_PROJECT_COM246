package kiosk.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kiosk.SceneManager;
import kiosk.model.OrderItem;
import kiosk.session.OrderSession;
import kiosk.util.CurrencyHelper;

public class OrderSummaryController {
    @FXML private TableView<OrderItem> table;
    @FXML private TableColumn<OrderItem, String> colItem;
    @FXML private TableColumn<OrderItem, Integer> colQty;
    @FXML private TableColumn<OrderItem, String> colPrice;
    @FXML private TableColumn<OrderItem, Void> colRemove;
    @FXML private Label lblTotal;

    @FXML
    public void initialize() {
        var order = OrderSession.get();
        table.setItems(order.getItems());

        colItem.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getMenuItem().getName()));
        colQty.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        colQty.setCellFactory(col -> new SpinnerTableCell());

        colPrice.setCellValueFactory(this::priceCell);
        colRemove.setCellFactory(col -> new RemoveButtonCell());

        updateTotal();

        // Update total if items are added/removed
        order.getItems().addListener((ListChangeListener<OrderItem>) c -> updateTotal());
    }

    private ObservableValue<String> priceCell(TableColumn.CellDataFeatures<OrderItem, String> data) {
        return new ReadOnlyObjectWrapper<>(CurrencyHelper.fmt(data.getValue().getLineTotal()));
    }

    private void updateTotal() {
        lblTotal.setText(CurrencyHelper.fmt(OrderSession.get().getTotal()));
    }

    public void backToMenu() {
        SceneManager.switchTo("MenuScreen.fxml");
    }

    public void goCheckout() {
        SceneManager.switchTo("CheckoutScreen.fxml");
    }

    /* ---------- inner cell classes ---------- */

    private class SpinnerTableCell extends TableCell<OrderItem, Integer> {
        private final Spinner<Integer> spinner;

        SpinnerTableCell() {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99);
            spinner = new Spinner<>(valueFactory);
            spinner.setEditable(true);

            // Listener only added once
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                OrderItem item = getTableRow() != null ? (OrderItem) getTableRow().getItem() : null;
                if (item != null) {
                    item.setQuantity(newVal);
                    updateTotal();
                    table.refresh();
                }
            });
        }

        @Override
        protected void updateItem(Integer qty, boolean empty) {
            super.updateItem(qty, empty);
            if (empty || qty == null) {
                setGraphic(null);
            } else {
                spinner.getValueFactory().setValue(qty);
                setGraphic(spinner);
            }
        }
    }

    private class RemoveButtonCell extends TableCell<OrderItem, Void> {
        private final Button btn = new Button("✖");

        RemoveButtonCell() {
            btn.setOnAction(e -> {
                OrderItem oi = getTableView().getItems().get(getIndex());
                OrderSession.get().remove(oi);
            });
        }

        @Override
        protected void updateItem(Void v, boolean empty) {
            super.updateItem(v, empty);
            setGraphic(empty ? null : btn);
        }
    }
}
