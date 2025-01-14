/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.SupportStaffMember;
import services.PersonService;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author rayyanabzal
 */

/*
 * Manages the login process for agents. This class handles user input for username and password, 
 * checks the credentials, and provides feedback on the status of the login attempt.
 * 
 * The class prompts the user to enter their username and password, verifies the credentials, 
 * and handles cases where the user might want to go back to the main menu.
 */
public class AgentLoginHandler {
    private final PersonService<SupportStaffMember> agentService;
    private final SetLastMessageCallback setLastMessageCallback;
    private final ServiceDeskSystem serviceDeskSystem;

    // Sets up the AgentLoginHandler with agent verification, message updates, and system navigation.
    public AgentLoginHandler(PersonService<SupportStaffMember> agentService, SetLastMessageCallback setLastMessageCallback, ServiceDeskSystem serviceDeskSystem) {
        this.agentService = agentService;
        this.setLastMessageCallback = setLastMessageCallback;
        this.serviceDeskSystem = serviceDeskSystem;
    }

    // Handles the login process by showing a dialog to enter username and password.
    // If login is successful, it navigates to the agent's main menu.
    public void handleLogin(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels and fields with proper alignment
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Enter your username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20); // Increased size
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Enter your password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Agent Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            SupportStaffMember agent = agentService.findPersonByUsername(username);
            if (agent != null && password.equals(agent.getPassword())) {
                // Stores agent info in UserSession for later use
                UserSession.getInstance().setUserInfo("Agent", agent.getEmail(), agent.getFirstName() + " " + agent.getLastName(), agent.getUsername(), agent.getId());
                setLastMessageCallback.set("Login successful! Welcome, " + agent.getUsername());
                // Switches to agent menu
                serviceDeskSystem.showAgentMenu(agent);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Functional interface used to set the last message displayed to the user.
    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}