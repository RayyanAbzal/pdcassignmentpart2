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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.FileUtil;

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
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]{3,}$");

    private PersonService<Customer> customerService;
    private int nextCustomerId;
    private UpdateNextCustomerIdCallback updateNextCustomerIdCallback;
    private SetLastMessageCallback setLastMessageCallback;

    /**
     * Sets up the registration handler with the required services and callbacks.
     * 
     * Initializes the customer service, ID counter, and callbacks for updating the ID sequence 
     * and setting messages during the registration process.
     */
    public CustomerRegistrationHandler(PersonService<Customer> customerService, int nextCustomerId, 
                                        UpdateNextCustomerIdCallback updateNextCustomerIdCallback, 
                                        SetLastMessageCallback setLastMessageCallback) {
        this.customerService = customerService;
        this.nextCustomerId = nextCustomerId;
        this.updateNextCustomerIdCallback = updateNextCustomerIdCallback;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    /**
     * Handles the customer registration process. Prompts for and validates the customer's name, email, 
     * and password. Ensures that the email is unique and the password meets the required standards.
     * 
     * Continues to request information until valid entries are provided or the user opts to go back.
     * Updates the customer ID and stores the new customer details in the file system.
     */
    public void handleRegistration(Scanner scanner) {
        String name;
        do {
            System.out.print("Enter customer name (or type 'x' to go back): ");
            name = scanner.nextLine();
            if ("x".equalsIgnoreCase(name)) {
                return;
            }
            if (!isValidName(name)) {
                System.out.println("Invalid name. Name must be at least 3 letters long. Please try again.");
            }
        } while (!isValidName(name));

        String email;
        do {
            System.out.print("Enter customer email (or type 'x' to go back): ");
            email = scanner.nextLine();
            if ("x".equalsIgnoreCase(email)) {
                return;
            }
            if (!EmailUtil.isValidEmail(email)) {
                System.out.println("Invalid email format. Please try again.");
            }
        } while (!EmailUtil.isValidEmail(email));

        // Check for email uniqueness
        if (customerService.findPersonByEmail(email) != null) {
            System.out.println("An account with this email already exists. Please use a different email.");
            return;
        }

        String password;
        String validationMessage;
        do {
            System.out.print("Enter customer password (or type 'x' to go back): ");
            password = scanner.nextLine();
            if ("x".equalsIgnoreCase(password)) {
                return;
            }
            validationMessage = PasswordUtil.validatePassword(password);
            if (validationMessage != null) {
                System.out.println(validationMessage);
            }
        } while (validationMessage != null);

        // Hash the password before storing it
        String hashedPassword = PasswordUtil.hashPassword(password);

        int id = nextCustomerId++;
        Customer customer = new Customer(id, name, email, hashedPassword);
        customerService.addPerson(customer);
        updateNextCustomerIdCallback.update(nextCustomerId);

        FileUtil.appendCustomerToFile(ServiceDeskSystem.CUSTOMERS_FILE, customer);

        setLastMessageCallback.set("Customer registered successfully. Your customer ID is " + id);
    }

    /*
     * Checks if the provided name matches the required pattern.
     * 
     * The name must be at least 3 letters long and contain only alphabetical characters.
     */
    private boolean isValidName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    @FunctionalInterface
    public interface UpdateNextCustomerIdCallback {
        /*
         * Updates the next customer ID.
         * 
         * This callback is used to synchronize the customer ID sequence after registration.
         */
        void update(int nextCustomerId);
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        /*
         * Sets the last message for the registration process.
         * 
         * This callback allows setting a message to inform the user of the registration result.
         */
        void set(String message);
    }
}
