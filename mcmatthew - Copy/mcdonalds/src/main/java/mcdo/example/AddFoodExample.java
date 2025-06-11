package mcdo.example;

import mcdo.controller.MenuController;
import mcdo.model.Product;

/**
 * Example class showing how to add food items to the menu
 */
public class AddFoodExample {
    
    public static void main(String[] args) {
        // Create a menu controller instance
        MenuController menuController = new MenuController();
        
        // Example 1: Add a McChicken using the existing placeholder image
        Product mcChicken = new Product(
            "McChicken",
            4.99,
            "/images/fooditems/pngwing.com.png",
            Product.Category.BURGER
        );
        menuController.addProduct(mcChicken);
        
        
        // Example 2: Add McSpaghetti using the existing spaghetti image
        Product mcSpaghetti = new Product(
            "McSpaghetti",
            3.49,
            "/images/fooditems/spaghettibruh.webp",
            Product.Category.SPAGHETTI
        );
        menuController.addProduct(mcSpaghetti);
        
        // Example 3: Add a new item with a custom image
        // Note: You need to add your image file to /src/main/resources/images/fooditems/
        Product chickenNuggets = new Product(
            "Chicken Nuggets",
            5.49,
            "/images/fooditems/nuggets.png",  // You need to add this image file
            Product.Category.CHICKEN
        );
        menuController.addProduct(chickenNuggets);
    }
} 