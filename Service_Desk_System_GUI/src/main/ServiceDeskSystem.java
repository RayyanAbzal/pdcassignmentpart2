/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;
import services.TicketService;
import util.FileUtil;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
    public static final String CUSTOMERS_FILE = "./resources/customers.txt";
    public static final String AGENTS_FILE = "./resources/agents.txt";
    private static final String TICKETS_OPEN_FILE = "./resources/tickets_open.txt";
    private static final String TICKETS_CLOSED_FILE = "./resources/tickets_closed.txt";

    private PersonService<Customer> customerService = new PersonService<>(Customer.class);
    private PersonService<SupportStaffMember> agentService = new PersonService<>(SupportStaffMember.class);
    private TicketService ticketService = new TicketService();

    private int nextCustomerId = 1;
    private int nextAgentId = 1;
    private Random random = new Random();
    private String lastMessage = "";

    private CustomerRegistrationHandler customerRegistrationHandler;
    private AgentRegistrationHandler agentRegistrationHandler;
    private CustomerLoginHandler customerLoginHandler;
    private AgentLoginHandler agentLoginHandler;
    private TicketManagementHandler ticketManagementHandler;
    /**
     * Initializes the system by setting up handlers and loading initial data.
     * Creates instances of registration and login handlers, ticket management, 
     * and communication handlers, and loads data from files.
     */
    public ServiceDeskSystem() {
        this.customerRegistrationHandler = new CustomerRegistrationHandler(
            customerService, 
            nextCustomerId, 
            this::updateNextCustomerId, 
            this::setLastMessage
        );
        
        this.agentRegistrationHandler = new AgentRegistrationHandler(
            agentService, 
            nextAgentId, 
            this::updateNextAgentId, 
            this::setLastMessage
        );

        this.customerLoginHandler = new CustomerLoginHandler(
            customerService, 
            this::setLastMessage
        );

        this.agentLoginHandler = new AgentLoginHandler(
            agentService, 
            this::setLastMessage
        );

        this.ticketManagementHandler = new TicketManagementHandler(
            ticketService, 
            agentService, 
            customerService, 
            this::generateTicketId, 
            this::getRandomAgent
        );
    }

    public static void main(String[] args) {
        ServiceDeskSystem system = new ServiceDeskSystem();
        system.loadInitialData();
        system.run();
    }

    /**
     * Loads initial data for customers, agents, and tickets from respective files.
     * Updates internal ID counters based on the data read from files.
     */
    private void loadInitialData() {
        List<Customer> customers = FileUtil.readCustomersFromFile(CUSTOMERS_FILE);
        for (Customer customer : customers) {
            customerService.addPerson(customer);
            updateNextCustomerId(customer.getId());
        }

        List<SupportStaffMember> agents = FileUtil.readSupportAgentsFromFile(AGENTS_FILE);
        for (SupportStaffMember agent : agents) {
            agentService.addPerson(agent);
            updateNextAgentId(agent.getId());
        }

        List<Ticket> tickets = FileUtil.readTicketsFromFile(TICKETS_OPEN_FILE, customers, agents);
        for (Ticket ticket : tickets) {
            ticketService.addTicket(ticket);
        }
    }

    /**
     * Runs the main loop of the service desk system, displaying the main menu 
     * and handling user input to perform various operations.
     */
    private void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Service Desk System");
            if (lastMessage != null) {
                System.out.println(lastMessage);
                lastMessage = null;
            }
            System.out.println("1. Register as Customer");
            System.out.println("2. Login as Customer");
            System.out.println("3. Register as Support Staff Member");
            System.out.println("4. Login as Support Staff Member");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    customerRegistrationHandler.handleRegistration(scanner);
                    break;
                case 2:
                    handleCustomerLogin(scanner);
                    break;
                case 3:
                    agentRegistrationHandler.handleRegistration(scanner);
                    break;
                case 4:
                    handleAgentLogin(scanner);
                    break;
                case 5:
                    saveData();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    /*
     * Handles customer login, allowing the user to log in and access the customer menu if successful.
     */
    private void handleCustomerLogin(Scanner scanner) {
        Customer customer = customerLoginHandler.handleLogin(scanner);
        if (customer != null) {
            customerMenu(scanner, customer);
        } else {
            System.out.println("Login failed. Try again.");
        }
    }

    /*
     * Handles agent login, allowing the user to log in and access the agent menu if successful.
     */
    private void handleAgentLogin(Scanner scanner) {
        SupportStaffMember agent = agentLoginHandler.handleLogin(scanner);
        if (agent != null) {
            agentMenu(scanner, agent);
        } else {
            System.out.println("Login failed. Try again.");
        }
    }

    /*
     * Displays the customer menu and handles customer-specific operations such as 
     * creating tickets, viewing tickets, and adding comments.
     */
    private void customerMenu(Scanner scanner, Customer customer) {
        boolean running = true;
        while (running) {
            System.out.println("Customer Menu");
            System.out.println("1. Create Ticket");
            System.out.println("2. View My Tickets");
            System.out.println("3. Add Comment to Ticket");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    ticketManagementHandler.createTicket(scanner, customer);
                    break;
                case 2:
                    ticketManagementHandler.viewCustomerTickets(customer);
                    break;
                case 3:
                    ticketManagementHandler.addCommentToTicket(scanner, customer, null);
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    /*
     * Displays the agent menu and handles agent-specific operations such as 
     * viewing tickets, resolving tickets, and setting ticket priority.
     */
    private void agentMenu(Scanner scanner, SupportStaffMember agent) {
        boolean running = true;
        while (running) {
            System.out.println("Agent Menu");
            System.out.println("1. View All Tickets");
            System.out.println("2. Resolve Ticket");
            System.out.println("3. View Ticket Comments");
            System.out.println("4. Add Comment to Ticket");
            System.out.println("5. Set Ticket Priority"); // Add this line
            System.out.println("6. Logout");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 1:
                    ticketManagementHandler.viewAllTickets();
                    break;
                case 2:
                    ticketManagementHandler.resolveTicket(scanner);
                    break;
                case 3:
                    ticketManagementHandler.viewCommentsForTicket(scanner);
                    break;
                case 4:
                    ticketManagementHandler.addCommentToTicket(scanner, null, agent);
                    break;
                case 5:
                    ticketManagementHandler.setTicketPriority(scanner, agent); // Add this line
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Saves current data for customers, agents, and tickets to their respective files.
     * Updates open and closed ticket files as needed.
     */
    private void saveData() {
        FileUtil.writeCustomersToFile(CUSTOMERS_FILE, customerService.getAllPersons());
        FileUtil.writeSupportAgentsToFile(AGENTS_FILE, agentService.getAllPersons());
        FileUtil.appendTicketsToClosedFile(TICKETS_CLOSED_FILE, ticketService.getAllTickets());
        FileUtil.appendTicketsToOpenFile(TICKETS_OPEN_FILE, ticketService.getOpenTickets());
    }

    /*
     * Updates the next customer ID to be used. Ensures the ID sequence continues correctly 
     * based on the highest ID currently in use.
     */
    private void updateNextCustomerId(int id) {
        if (id >= nextCustomerId) {
            nextCustomerId = id + 1;
        }
    }

    /*
     * Updates the next agent ID to be used. Ensures the ID sequence continues correctly 
     * based on the highest ID currently in use.
     */
    private void updateNextAgentId(int id) {
        if (id >= nextAgentId) {
            nextAgentId = id + 1;
        }
    }

    /*
     * Sets the last message to be displayed to the user.
     */
    private void setLastMessage(String message) {
        this.lastMessage = message;
    }

    /*
     * Generates a new ticket ID. This example simply uses the size of open tickets list 
     * plus one as a new ID.
     */
    private int generateTicketId() {
        return ticketService.getOpenTickets().size() + 1; // Simple example; adjust as needed
    }

    /*
     * Selects a random agent from the list of available agents.
     */
    private SupportStaffMember getRandomAgent() {
        List<SupportStaffMember> agents = agentService.getAllPersons();
        if (agents.isEmpty()) {
            return null;
        }
        int index = random.nextInt(agents.size());
        return agents.get(index);
    }
}