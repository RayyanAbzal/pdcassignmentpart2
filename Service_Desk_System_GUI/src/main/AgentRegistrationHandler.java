/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.SupportStaffMember;
import services.PersonService;
import javax.swing.*;
import java.awt.*;
import util.ValidationUtil;

/**
 *
 * @author rayyanabzal
 */

/*
 * Handles the registration process for new agents. This class prompts the user to input necessary 
 * details, validates the input, and registers the agent if all checks are passed.
 * 
 * The process involves entering and validating the agent's name, username, email, and password. 
 * Once all the details are verified, the agent is registered, and the information is saved.
 */
public class AgentRegistrationHandler {
    private final PersonService<SupportStaffMember> personService;
    private final SetLastMessageCallback setLastMessageCallback;

    // Sets up the handler with the necessary service and callback for agent registration
    public AgentRegistrationHandler(PersonService<SupportStaffMember> personService, SetLastMessageCallback setLastMessageCallback) {
        this.personService = personService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    // Displays the registration form and handles user input for agent details.
    // Validates the input and registers the agent if all details are correct.
    public void handleRegistration(JFrame frame) {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); // Increase rows

        JLabel firstNameLabel = new JLabel("Enter agent first name:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Enter agent last name:");
        JTextField lastNameField = new JTextField();
        JLabel usernameLabel = new JLabel("Enter agent username:");
        JTextField usernameField = new JTextField();
        JLabel emailLabel = new JLabel("Enter agent email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Enter agent password:");
        JPasswordField passwordField = new JPasswordField();

        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Agent Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String email = emailField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email format
            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Invalid email format. Please enter a valid email.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate password criteria
            String passwordError = ValidationUtil.validatePassword(password);
            if (passwordError != null) {
                JOptionPane.showMessageDialog(frame, passwordError, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (personService.findPersonByUsername(username) != null) {
                JOptionPane.showMessageDialog(frame, "An account with this username already exists. Please use a different username.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                SupportStaffMember agent = new SupportStaffMember(0, firstName, lastName, username, email, password);
                saveAgent(agent);
            }
        }
    }

    // Saves the agent to the system and sets a confirmation message
    private void saveAgent(SupportStaffMember agent) {
        agent.setFirstName(capitalizeFirstLetter(agent.getFirstName()));
        agent.setLastName(capitalizeFirstLetter(agent.getLastName()));

        personService.addPerson(agent);
        setLastMessageCallback.set("Agent registered successfully.");
    }

    // Capitalizes the first letter of a given string, used for name formatting
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Functional interface for displaying the last message to the user
    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}