package mcdo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import mcdo.util.Constants;

public class AuthenticationService {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private Map<String, String> userAccounts;
    
    public AuthenticationService() {
        loadAccounts();
    }
    
    /**
     * Loads user accounts from the resources file
     */
    private void loadAccounts() {
        userAccounts = new HashMap<>();
        
        try (InputStream inputStream = getClass().getResourceAsStream(Constants.ACCOUNTS_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            
            if (inputStream == null) {
                LOGGER.severe("Accounts file not found: " + Constants.ACCOUNTS_FILE);
                return;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    userAccounts.put(parts[0].trim(), parts[1].trim());
                }
            }
            
            LOGGER.info("Loaded " + userAccounts.size() + " user accounts");
            
        } catch (IOException e) {
            LOGGER.severe("Error loading accounts: " + e.getMessage());
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
} 