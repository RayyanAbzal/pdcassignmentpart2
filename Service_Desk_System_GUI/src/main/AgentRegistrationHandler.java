/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.SupportStaffMember;
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

/*
 * Handles the registration process for new agents. This class prompts the user to input necessary 
 * details, validates the input, and registers the agent if all checks are passed.
 * 
 * The process involves entering and validating the agent's name, username, email, and password. 
 * Once all the details are verified, the agent is registered, and the information is saved.
 */
public class AgentRegistrationHandler {
    // Pattern for validating the agent's name (at least 3 letters long)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]{3,}$");

    private PersonService<SupportStaffMember> agentService;
    private int nextAgentId;
    private UpdateNextAgentIdCallback updateNextAgentIdCallback;
    private SetLastMessageCallback setLastMessageCallback;

    /**
     * Sets up the registration handler with necessary services and callbacks.
     * 
     * Initializes the handler with the agent service, the next agent ID to use, and callbacks 
     * for updating the agent ID and setting messages.
     */
    // got assistance from chatgpt for this:
    public AgentRegistrationHandler(PersonService<SupportStaffMember> agentService, int nextAgentId, 
                                    UpdateNextAgentIdCallback updateNextAgentIdCallback, 
                                    SetLastMessageCallback setLastMessageCallback) {
        this.agentService = agentService;
        this.nextAgentId = nextAgentId;
        this.updateNextAgentIdCallback = updateNextAgentIdCallback;
        this.setLastMessageCallback = setLastMessageCallback;
    }

    /*
     * Manages the registration process for an agent. Prompts for and validates the agent's 
     * name, username, email, and password. Handles cases where the user might want to go back 
     * to the previous step or if the input is invalid.
     * 
     * Saves the agent details and updates the relevant files and states upon successful registration.
     */
    public void handleRegistration(Scanner scanner) {
        String name;
        do {
            System.out.print("Enter agent name (or type 'x' to go back): ");
            name = scanner.nextLine();
            if ("x".equalsIgnoreCase(name)) {
                return;
            }
            if (!isValidName(name)) {
                System.out.println("Invalid name. Name must be at least 3 letters long. Please try again.");
            }
        } while (!isValidName(name));

        String username;
        do {
            System.out.print("Enter agent username (or type 'x' to go back): ");
            username = scanner.nextLine();
            if ("x".equalsIgnoreCase(username)) {
                return;
            }
            if (agentService.findPersonByUsername(username) != null) {
                System.out.println("Username already exists. Please choose a different username.");
            }
        } while (agentService.findPersonByUsername(username) != null);

        String email;
        do {
            System.out.print("Enter agent email (or type 'x' to go back): ");
            email = scanner.nextLine();
            if ("x".equalsIgnoreCase(email)) {
                return;
            }
            if (!EmailUtil.isValidEmail(email)) {
                System.out.println("Invalid email format. Please try again.");
            }
        } while (!EmailUtil.isValidEmail(email));

        if (agentService.findPersonByEmail(email) != null) {
            System.out.println("An account with this email already exists. Please use a different email.");
            return;
        }

        String password;
        String validationMessage;
        do {
            System.out.print("Enter agent password (or type 'x' to go back): ");
            password = scanner.nextLine();
            if ("x".equalsIgnoreCase(password)) {
                return;
            }
            validationMessage = PasswordUtil.validatePassword(password);
            if (validationMessage != null) {
                System.out.println(validationMessage);
            }
        } while (validationMessage != null);

        // Hash the password before saving
        String hashedPassword = PasswordUtil.hashPassword(password);

        // Create and save the new agent
        int id = nextAgentId++;
        SupportStaffMember agent = new SupportStaffMember(id, username, email, hashedPassword);
        agentService.addPerson(agent);
        updateNextAgentIdCallback.update(nextAgentId);

        // Save the agent details to the file
        FileUtil.appendAgentToFile(ServiceDeskSystem.AGENTS_FILE, agent);

        // Notify the user of successful registration
        setLastMessageCallback.set("Agent registered successfully. Your agent ID is " + id);
    }

    /*
     * Validates the agent's name based on a predefined pattern.
     * 
     * Ensures the name consists of at least 3 letters.
     * 
     */
    private boolean isValidName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    @FunctionalInterface
    public interface UpdateNextAgentIdCallback {
        /*
         * Updates the next agent ID after a new agent is registered.
         * 
         * This callback is used to set the next ID to be used for new agents.
         */
        void update(int nextAgentId);
    }

    @FunctionalInterface
    public interface SetLastMessageCallback {
        /**
         * Sets a message to reflect the outcome of the registration process.
         * 
         * Allows the system to provide feedback to the user regarding the registration status.
         * 
         */
        void set(String message);
    }
}