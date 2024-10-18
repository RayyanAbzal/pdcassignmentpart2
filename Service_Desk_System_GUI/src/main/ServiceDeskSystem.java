/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;
import util.DatabaseUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import services.TicketService;
/**
 *
 * @author rayyanabzal
 */
/**
 * Main class to handle the entire service desk system. Manages customer and agent registration, login,
 * and ticket management. Provides a menu-driven interface for interaction with customers and agents.
 * 
 * Initializes services, handlers, and manages application flow, including data loading, menu display, 
 * and user input handling.
 */
public class ServiceDeskSystem {
    private Scanner scanner;
    private PersonService<Customer> customerService;
    private PersonService<SupportStaffMember> supportStaffService;
    private int nextCustomerId;
    private CustomerRegistrationHandler customerRegistrationHandler;
    private TicketManagementHandler ticketManagementHandler;

    public ServiceDeskSystem() {
        this.scanner = new Scanner(System.in);
        this.customerService = new PersonService<>(Customer.class);
        this.supportStaffService = new PersonService<>(SupportStaffMember.class);
        this.customerRegistrationHandler = new CustomerRegistrationHandler(customerService, this::setLastMessage);
        this.ticketManagementHandler = new TicketManagementHandler(
                new TicketService(), supportStaffService, customerService, this::generateId, this::getRandomAgent);
    }

    public static void main(String[] args) {
        ServiceDeskSystem system = new ServiceDeskSystem();
        try {
            DatabaseUtil.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            return;
        }
        system.run();
    }

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to the Service Desk System!");
            System.out.println("1. Customer Login");
            System.out.println("2. Agent Login");
            System.out.println("3. Register as Customer");
            System.out.println("4. Register as Agent");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    customerLogin();
                    break;
                case 2:
                    agentLogin();
                    break;
                case 3:
                    registerCustomer();
                    break;
                case 4:
                    registerAgent();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }

        DatabaseUtil.closeConnection();
    }

    private void customerLogin() {
        System.out.println("\nEnter your email:");
        String email = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        try {
            List<Customer> customers = DatabaseUtil.getAllCustomers();
            for (Customer customer : customers) {
                if (customer.getEmail().equals(email) && customer.getPassword().equals(password)) {
                    System.out.println("Login successful! Welcome, " + customer.getName());
                    customerMenu(customer);
                    return;
                }
            }
            System.out.println("Invalid email or password. Please try again.");
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    private void agentLogin() {
        System.out.println("\nEnter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        try {
            List<SupportStaffMember> staffList = DatabaseUtil.getAllSupportStaff();
            for (SupportStaffMember staff : staffList) {
                if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                    System.out.println("Login successful! Welcome, " + staff.getUsername());
                    agentMenu(staff);
                    return;
                }
            }
            System.out.println("Invalid username or password. Please try again.");
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    private void registerCustomer() {
        customerRegistrationHandler.handleRegistration(scanner);
    }

    private void registerAgent() {
        System.out.println("\nEnter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your email:");
        String email = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        SupportStaffMember staff = new SupportStaffMember(generateId(), username, email, password);

        try {
            DatabaseUtil.insertSupportStaff(staff);
            System.out.println("Agent registered successfully!");
        } catch (SQLException e) {
            System.err.println("Error registering agent: " + e.getMessage());
        }
    }

    private void customerMenu(Customer customer) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n1. Create Ticket");
            System.out.println("2. View Tickets");
            System.out.println("3. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    ticketManagementHandler.createTicket(scanner, customer);
                    break;
                case 2:
                    ticketManagementHandler.viewCustomerTickets(customer);
                    break;
                case 3:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void agentMenu(SupportStaffMember staff) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n1. View Open Tickets");
            System.out.println("2. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    //ticketManagementHandler.viewAgentTickets(staff);
                    break;
                case 2:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void setLastMessage(String message) {
        System.out.println(message);
    }

    private int generateId() {
        return nextCustomerId++;
    }

    private SupportStaffMember getRandomAgent() {
        // Implement logic to get a random available agent
        // For simplicity, returning null here
        return null;
    }
}