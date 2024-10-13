/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


import service.desk.system.Customer;
import services.PersonService;
import util.PasswordUtil;
import java.util.Scanner;
import java.util.regex.Pattern;
import util.EmailUtil;

/**
 *
 * @author rayyanabzal
 */
/**
 * Handles the login process for customers. This class prompts for and verifies the customer’s email 
 * and password, providing feedback throughout the process. It manages the login flow and handles 
 * user navigation back to the main menu if needed.
 * 
 * The class checks if the provided email and password match an existing customer’s credentials 
 * and returns the customer object upon successful login.
 */
public class CustomerLoginHandler {
    private PersonService<Customer> customerService;
    private SetLastMessageCallback setLastMessageCallback;

    /**
     * Initializes the login handler with the customer service and message callback.
     * 
     * Sets up the necessary service for customer management and the callback for updating messages 
     * based on the login process.
     */
    public CustomerLoginHandler(PersonService<Customer> customerService, SetLastMessageCallback setLastMessageCallback) {
        this.customerService = customerService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    /**
     * Manages the customer login process. Prompts for the customer’s email and password, and validates 
     * the credentials. Handles cases where the user wants to go back or if the input is invalid.
     * 
     * Continues to ask for credentials until a successful login is achieved or the user chooses to return 
     * to the main menu.
     * 
     * Utilizes the provided scanner for input and interacts with the customer service to verify credentials.
     */
    public Customer handleLogin(Scanner scanner) {
        Customer customer = null;
        while (customer == null) {
            System.out.print("Enter customer email (or 'x' to go back): ");
            String email = scanner.nextLine();

            // Allow user to return to the main menu
            if ("x".equalsIgnoreCase(email)) {
                setLastMessageCallback.set("Returning to the main menu.");
                return null;
            }

            // Validate email format
            if (!EmailUtil.isValidEmail(email)) {
                setLastMessageCallback.set("Invalid email format. Please enter a valid email.");
                continue;
            }

            // Check if the customer exists
            customer = customerService.findPersonByEmail(email);
            if (customer == null) {
                setLastMessageCallback.set("Customer not found. Please check the email and try again.");
                continue;
            }

            System.out.print("Enter password (or 'x' to go back): ");
            String password = scanner.nextLine();

            // Allow user to return to the main menu
            if ("x".equalsIgnoreCase(password)) {
                setLastMessageCallback.set("Returning to the main menu.");
                return null;
            }

            // Verify the provided password
            if (PasswordUtil.verifyPassword(customer.getPassword(), password)) {
                setLastMessageCallback.set("Login successful.");
                return customer; // Successful login, return the customer
            } else {
                setLastMessageCallback.set("Incorrect password. Please try again.");
                customer = null; // Reset customer to retry login
            }
        }
        return null; // This line is here to ensure the method always returns a value
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        /*
         * Updates the message for the login process.
         * 
         * This callback allows setting a message to inform the user about the status of the login attempt.
         */
        void set(String message);
    }
}