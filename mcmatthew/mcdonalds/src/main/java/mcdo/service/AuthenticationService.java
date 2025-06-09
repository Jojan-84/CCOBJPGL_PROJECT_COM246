package mcdo.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import mcdo.util.Constants;

public class AuthenticationService {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private Map<String, String> userAccounts;
    private String accountsFilePath;
    
    public AuthenticationService() {
        userAccounts = new HashMap<>();
        initializeAccountsFile();
        loadAccounts();
    }
    
    /**
     * Initialize the accounts file path for reading and writing
     */
    private void initializeAccountsFile() {
        try {
            // First try to get the file from resources
            URL resourceUrl = getClass().getResource(Constants.ACCOUNTS_FILE);
            if (resourceUrl != null) {
                accountsFilePath = resourceUrl.getPath();
                LOGGER.info("Found accounts file at: " + accountsFilePath);
            } else {
                // Create accounts file in user directory if not found in resources
                String userHome = System.getProperty("user.home");
                String appDir = userHome + File.separator + ".mcdonalds-kiosk";
                File appDirectory = new File(appDir);
                if (!appDirectory.exists()) {
                    appDirectory.mkdirs();
                }
                accountsFilePath = appDir + File.separator + "accounts.txt";
                
                // Create default accounts file if it doesn't exist
                File accountsFile = new File(accountsFilePath);
                if (!accountsFile.exists()) {
                    createDefaultAccountsFile(accountsFile);
                }
                LOGGER.info("Using accounts file at: " + accountsFilePath);
            }
        } catch (Exception e) {
            LOGGER.severe("Error initializing accounts file: " + e.getMessage());
            // Fallback to user directory
            String userHome = System.getProperty("user.home");
            accountsFilePath = userHome + File.separator + "accounts.txt";
        }
    }
    
    /**
     * Create default accounts file with initial users
     */
    private void createDefaultAccountsFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("matthew,123");
            writer.newLine();
            writer.write("juhan,tanginamo");
            writer.newLine();
            LOGGER.info("Created default accounts file with initial users");
        } catch (IOException e) {
            LOGGER.severe("Error creating default accounts file: " + e.getMessage());
        }
    }
    
    /**
     * Loads user accounts from the file
     */
    private void loadAccounts() {
        userAccounts.clear();
        
        // First try to load from file system
        File accountsFile = new File(accountsFilePath);
        if (accountsFile.exists()) {
            loadFromFile(accountsFile);
        } else {
            // Fallback to loading from resources
            loadFromResources();
        }
    }
    
    /**
     * Load accounts from file system
     */
    private void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    userAccounts.put(parts[0].trim(), parts[1].trim());
                }
            }
            LOGGER.info("Loaded " + userAccounts.size() + " user accounts from file");
        } catch (IOException e) {
            LOGGER.severe("Error loading accounts from file: " + e.getMessage());
        }
    }
    
    /**
     * Load accounts from resources (fallback)
     */
    private void loadFromResources() {
        try (InputStream inputStream = getClass().getResourceAsStream(Constants.ACCOUNTS_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            if (inputStream == null) {
                LOGGER.warning("Accounts file not found in resources: " + Constants.ACCOUNTS_FILE);
                return;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    userAccounts.put(parts[0].trim(), parts[1].trim());
                }
            }
            
            LOGGER.info("Loaded " + userAccounts.size() + " user accounts from resources");
            
        } catch (IOException e) {
            LOGGER.severe("Error loading accounts from resources: " + e.getMessage());
        }
    }
    
    /**
     * Save user accounts to file
     */
    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountsFilePath))) {
            for (Map.Entry<String, String> entry : userAccounts.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            LOGGER.info("Saved " + userAccounts.size() + " user accounts to file");
        } catch (IOException e) {
            LOGGER.severe("Error saving accounts to file: " + e.getMessage());
            throw new RuntimeException("Failed to save user accounts", e);
        }
    }
    
    /**
     * Authenticates a user with username and password
     * @param username the username
     * @param password the password
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        String storedPassword = userAccounts.get(username.trim());
        return storedPassword != null && storedPassword.equals(password.trim());
    }
    
    /**
     * Checks if a username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username) {
        return username != null && userAccounts.containsKey(username.trim());
    }
    
    /**
     * Register a new user
     * @param username the username
     * @param password the password
     * @return true if registration successful, false if username already exists
     * @throws IllegalArgumentException if username or password is invalid
     */
    public boolean registerUser(String username, String password) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        username = username.trim();
        password = password.trim();
        
        // Validate username length
        if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username too long (max " + Constants.MAX_USERNAME_LENGTH + " characters)");
        }
        
        // Validate password length
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too short (min " + Constants.MIN_PASSWORD_LENGTH + " characters)");
        }
        
        if (password.length() > Constants.MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too long (max " + Constants.MAX_PASSWORD_LENGTH + " characters)");
        }
        
        // Check for invalid characters in username
        if (username.contains(",") || username.contains("\n") || username.contains("\r")) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
        
        // Check for invalid characters in password
        if (password.contains(",") || password.contains("\n") || password.contains("\r")) {
            throw new IllegalArgumentException("Password contains invalid characters");
        }
        
        // Check if username already exists
        if (userExists(username)) {
            return false; // Username already exists
        }
        
        // Add user to memory
        userAccounts.put(username, password);
        
        // Save to file
        try {
            saveAccounts();
            LOGGER.info("Successfully registered new user: " + username);
            return true;
        } catch (Exception e) {
            // Remove from memory if save failed
            userAccounts.remove(username);
            LOGGER.severe("Failed to save new user registration: " + e.getMessage());
            throw new RuntimeException("Failed to register user", e);
        }
    }
    
    /**
     * Get total number of registered users
     * @return number of users
     */
    public int getUserCount() {
        return userAccounts.size();
    }
} 