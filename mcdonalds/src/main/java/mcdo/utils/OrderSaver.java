package mcdo.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import mcdo.model.Order;

public class OrderSaver {

    private static final String FILE_PATH = "order.txt";

    public static void saveOrder(Order order) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write("=== Order Placed at: " + LocalDateTime.now() + " ===\n");
            writer.write(order.toString());
            writer.write("-------------------------------\n");
        } catch (IOException e) {
            System.out.println("Error writing order to file: " + e.getMessage());
        }
    }
}
