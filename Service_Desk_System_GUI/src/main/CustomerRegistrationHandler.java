/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.Customer;
import services.PersonService;
import util.EmailUtil;
import util.PasswordUtil;
import java.util.Scanner;
import util.DatabaseUtil;
import java.sql.SQLException;
import main.CustomerLoginHandler.SetLastMessageCallback;


/**
 *
 * @author rayyanabzal
 */
/**
 * Manages the registration process for new customers. This class prompts for and validates the customer's 
 * name, email, and password. It handles all necessary checks to ensure the information is correct and 
 * unique before finalizing the registration.
 * 
 * The class ensures the provided email is valid and not already in use, and validates the password 
 * according to defined rules. It also manages the customer ID sequence and updates the storage file 
 * with the new customer details.
 */
public class CustomerRegistrationHandler {
    private final PersonService<Customer> customerService;
    private final SetLastMessageCallback setLastMessageCallback;

    public CustomerRegistrationHandler(PersonService<Customer> customerService, SetLastMessageCallback setLastMessageCallback) {
        this.customerService = customerService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    public void handleRegistration(Scanner scanner) {
    try {
        String name = promptForInput(scanner, "Enter customer name (or type 'x' to go back): ", this::isValidName);
        if (name == null) return;

        String email = promptForInput(scanner, "Enter customer email (or type 'x' to go back): ", EmailUtil::isValidEmail);
        if (email == null || customerService.findPersonByEmail(email) != null) {
            if (email != null) {
                setLastMessageCallback.set("An account with this email already exists. Please use a different email.");
            }
            return;
        }

        String password = promptForInput(scanner, "Enter customer password (or type 'x' to go back): ", PasswordUtil::validatePassword);
        if (password == null) return;

        Customer customer = new Customer(0, name, email, password);
        saveCustomer(customer);
    } catch (Exception e) {
        e.printStackTrace(); // Print stack trace to identify where the error occurs
        setLastMessageCallback.set("An unexpected error occurred: " + e.getMessage());
    }
}

    private String promptForInput(Scanner scanner, String prompt, InputValidator validator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if ("x".equalsIgnoreCase(input)) return null;
            if (validator.isValid(input)) return input;
            System.out.println("Invalid input. Please try again.");
        }
    }

    private void saveCustomer(Customer customer) {
        try {
            int generatedId = DatabaseUtil.insertCustomer(customer);
            setLastMessageCallback.set("Customer registered successfully. Your customer ID is " + generatedId);
        } catch (SQLException e) {
            setLastMessageCallback.set("Error occurred while saving the customer: " + e.getMessage());
        }
    }

    private boolean isValidName(String name) {
        return name.length() >= 3; // Validate name length
    }

    @FunctionalInterface
    public interface InputValidator {
        boolean isValid(String input);
    }
}
