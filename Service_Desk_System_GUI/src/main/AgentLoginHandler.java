/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.SupportStaffMember;
import services.PersonService;
import util.PasswordUtil;
import java.util.Scanner;
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
    private PersonService<SupportStaffMember> agentService;
    private SetLastMessageCallback setLastMessageCallback;

    /*
     * Constructs an instance of AgentLoginHandler with the specified agent service and message callback.
     * 
     * Sets up the necessary service for agent management and the callback to update messages 
     * based on the login process.
     */
    public AgentLoginHandler(PersonService<SupportStaffMember> agentService, SetLastMessageCallback setLastMessageCallback) {
        this.agentService = agentService;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    /*
     * Handles the login process for agents. Prompts for a username and password, and checks 
     * if the entered credentials match an existing agent.
     * 
     * Continues to ask for credentials until a successful login occurs or the user chooses to go back.
     * 
     * Uses the provided scanner for input and interacts with the agent service to validate the 
     * credentials.
     */
    public SupportStaffMember handleLogin(Scanner scanner) {
        SupportStaffMember agent = null;
        while (agent == null) {
            System.out.print("Enter agent username (or 'x' to go back): ");
            String username = scanner.nextLine();

            // Check if user wants to return to the main menu
            if ("x".equalsIgnoreCase(username)) {
                setLastMessageCallback.set("Returning to the main menu.");
                return null;
            }

            // Attempt to find the agent with the provided username
            agent = agentService.findPersonByUsername(username);
            if (agent == null) {
                setLastMessageCallback.set("Agent not found. Please check the username and try again.");
                continue;
            }

            System.out.print("Enter password (or 'x' to go back): ");
            String password = scanner.nextLine();

            // Check if user wants to return to the main menu
            if ("x".equalsIgnoreCase(password)) {
                setLastMessageCallback.set("Returning to the main menu.");
                return null;
            }

            // Validate the provided password
            if (PasswordUtil.verifyPassword(agent.getPassword(), password)) {
                setLastMessageCallback.set("Login successful.");
                return agent; // Successful login
            } else {
                setLastMessageCallback.set("Incorrect password. Please try again.");
                agent = null; // Reset agent to retry login
            }
        }
        return null; // ensures the method always returns something
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        /*
         * Updates the message for the login process.
         * 
         * This interface allows setting a message that reflects the current status of the login attempt.
         */
        void set(String message);
    }
}