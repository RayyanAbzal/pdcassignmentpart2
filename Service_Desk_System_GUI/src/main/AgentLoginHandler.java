/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.SupportStaffMember;
import services.PersonService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public AgentLoginHandler(PersonService<SupportStaffMember> agentService, SetLastMessageCallback setLastMessageCallback, ServiceDeskSystem serviceDeskSystem) {
        this.agentService = agentService;
        this.setLastMessageCallback = setLastMessageCallback;
        this.serviceDeskSystem = serviceDeskSystem;
    }

    public void handleLogin(JFrame frame) {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

    JLabel usernameLabel = new JLabel("Enter your username:");
    JTextField usernameField = new JTextField();
    JLabel passwordLabel = new JLabel("Enter your password:");
    JPasswordField passwordField = new JPasswordField();

    panel.add(usernameLabel);
    panel.add(usernameField);
    panel.add(passwordLabel);
    panel.add(passwordField);

    int option = JOptionPane.showConfirmDialog(frame, panel, "Agent Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (option == JOptionPane.OK_OPTION) {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        SupportStaffMember agent = agentService.findPersonByUsername(username);
        if (agent != null && password.equals(agent.getPassword())) {
            // Set user info in UserSession
            UserSession.getInstance().setUserInfo("Agent", agent.getEmail(), agent.getFirstName() + " " + agent.getLastName(), agent.getUsername(), agent.getId());
            setLastMessageCallback.set("Login successful! Welcome, " + agent.getUsername());
            // Switch to agent menu
            serviceDeskSystem.showAgentMenu(agent);
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    @FunctionalInterface
    public interface SetLastMessageCallback {
        void set(String message);
    }
}