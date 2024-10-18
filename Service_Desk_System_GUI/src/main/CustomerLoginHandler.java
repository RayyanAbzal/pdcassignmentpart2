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
import util.DatabaseUtil;
import util.EmailUtil;
import java.sql.SQLException;
import java.util.List;

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
    private final PersonService<Customer> customerService;
    private final SetLastMessageCallback setLastMessageCallback;

    public CustomerLoginHandler(PersonService<Customer> customerService, SetLastMessageCallback setLastMessageCallback) {
        this.customerService = customerService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    public void handleLogin(Scanner scanner) {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Fetch the customer using the email
        Customer customer = customerService.findPersonByEmail(email);
        if (customer != null) {
            // Compare the plain password directly
            if (password.equals(customer.getPassword())) { 
                setLastMessageCallback.set("Login successful! Welcome, " + customer.getName());
                // Proceed with further options after successful login
            } else {
                setLastMessageCallback.set("Invalid email or password. Please try again.");
            }
        } else {
            setLastMessageCallback.set("Invalid email or password. Please try again.");
        }
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}