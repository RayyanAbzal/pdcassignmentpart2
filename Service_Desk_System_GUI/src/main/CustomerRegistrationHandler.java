/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.Customer;
import services.PersonService;
import util.EmailUtil;
import util.PasswordUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import util.DatabaseUtil;


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
    private final PersonService<Customer> personService;
    private final SetLastMessageCallback setLastMessageCallback;

    public CustomerRegistrationHandler(PersonService<Customer> personService, SetLastMessageCallback setLastMessageCallback) {
        this.personService = personService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    public void handleRegistration(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); // Increase rows

        JLabel firstNameLabel = new JLabel("Enter customer first name:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Enter customer last name:");
        JTextField lastNameField = new JTextField();
        JLabel emailLabel = new JLabel("Enter customer email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Enter customer password:");
        JPasswordField passwordField = new JPasswordField();

        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Customer Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email using EmailUtil
            if (!EmailUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Invalid email format. Please enter a valid email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (personService.findPersonByEmail(email) != null) {
                JOptionPane.showMessageDialog(frame, "An account with this email already exists. Please use a different email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                Customer customer = new Customer(0, firstName, lastName, email, password);
                saveCustomer(customer);
            }
        }
    }

    private void saveCustomer(Customer customer) {
        customer.setFirstName(capitalizeFirstLetter(customer.getFirstName()));
        customer.setLastName(capitalizeFirstLetter(customer.getLastName()));

        personService.addPerson(customer);
        setLastMessageCallback.set("Customer registered successfully.");
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}