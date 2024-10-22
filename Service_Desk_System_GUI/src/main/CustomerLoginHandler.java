/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


import service.desk.system.Customer;
import services.PersonService;
import util.PasswordUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public CustomerLoginHandler(PersonService<Customer> customerService, SetLastMessageCallback setLastMessageCallback, ServiceDeskSystem serviceDeskSystem) {
        this.customerService = customerService;
        this.setLastMessageCallback = setLastMessageCallback;
        this.serviceDeskSystem = serviceDeskSystem;
    }

    public void handleLogin(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel emailLabel = new JLabel("Enter your email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Enter your password:");
        JPasswordField passwordField = new JPasswordField();

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Customer Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            Customer customer = customerService.findPersonByEmail(email);
            if (customer != null && password.equals(customer.getPassword())) {
                // Set user info in UserSession
                UserSession.getInstance().setUserInfo("Customer", customer.getEmail(), customer.getFirstName() + " " + customer.getLastName(), null, customer.getId());
                setLastMessageCallback.set("Login successful! Welcome, " + customer.getFirstName() + " " + customer.getLastName());
                // Switch to customer menu
                serviceDeskSystem.showCustomerMenu(customer);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}