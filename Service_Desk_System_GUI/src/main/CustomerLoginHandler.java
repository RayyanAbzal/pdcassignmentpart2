/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


import service.desk.system.Customer;
import services.PersonService;
import javax.swing.*;
import java.awt.*;

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
    private final ServiceDeskSystem serviceDeskSystem;

    // Initializes the CustomerLoginHandler with customer services and callback for updating messages
    public CustomerLoginHandler(PersonService<Customer> customerService, SetLastMessageCallback setLastMessageCallback, ServiceDeskSystem serviceDeskSystem) {
        this.customerService = customerService;
        this.setLastMessageCallback = setLastMessageCallback;
        this.serviceDeskSystem = serviceDeskSystem;
    }

    // Displays the login dialog for the customer, verifies credentials, and navigates to customer menu if successful
    public void handleLogin(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and fields with proper alignment
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Enter your email:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(20); // Increased size
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Enter your password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Customer Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            Customer customer = customerService.findPersonByEmail(email);
            if (customer != null && password.equals(customer.getPassword())) {
                // Stores customer info in UserSession
                UserSession.getInstance().setUserInfo("Customer", customer.getEmail(), customer.getFirstName() + " " + customer.getLastName(), null, customer.getId());
                setLastMessageCallback.set("Login successful! Welcome, " + customer.getFirstName() + " " + customer.getLastName());
                // Navigates to customer menu
                serviceDeskSystem.showCustomerMenu(customer);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Functional interface for setting the last displayed message to the user
    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}