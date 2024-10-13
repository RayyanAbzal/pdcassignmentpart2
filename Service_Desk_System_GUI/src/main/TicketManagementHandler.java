/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.time.LocalDateTime;
import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;
import services.TicketService;
import java.util.List;
import java.util.Scanner;
import service.desk.system.Message;
import util.FileUtil;
/**
 *
 * @author rayyanabzal
 */
/**
 * Manages various ticket-related operations, including creation, resolution, and comment handling.
 */
public class TicketManagementHandler {
    private TicketService ticketService;
    private PersonService<SupportStaffMember> agentService;
    private PersonService<Customer> customerService;
    private GenerateTicketIdCallback generateTicketIdCallback;
    private GetRandomAgentCallback getRandomAgentCallback;

    private static final String OPEN_TICKETS_FILE = "./resources/tickets_open.txt";
    private static final String CLOSED_TICKETS_FILE = "./resources/tickets_closed.txt";

    public TicketManagementHandler(TicketService ticketService, PersonService<SupportStaffMember> agentService,
                                   PersonService<Customer> customerService, GenerateTicketIdCallback generateTicketIdCallback,
                                   GetRandomAgentCallback getRandomAgentCallback) {
        this.ticketService = ticketService;
        this.agentService = agentService;
        this.customerService = customerService;
        this.generateTicketIdCallback = generateTicketIdCallback;
        this.getRandomAgentCallback = getRandomAgentCallback;
    }

    /*
     * Creates a new ticket and assigns it to a random available agent.
     */
    public void createTicket(Scanner scanner, Customer customer) {
        // Check if there are any agents available
        SupportStaffMember assignedAgent = getRandomAgentCallback.get();
        if (assignedAgent == null) {
            System.out.println("No agents available. Ticket cannot be created.");
            return;
        }

        System.out.print("Enter ticket topic: ");
        String topic = scanner.nextLine();
        System.out.print("Enter ticket issue: ");
        String issue = scanner.nextLine();
        int ticketId = generateTicketIdCallback.generate();

        Ticket ticket = new Ticket(ticketId, customer, assignedAgent, topic, issue, LocalDateTime.now(), 1);

        // Confirm ticket creation
        System.out.print("Do you want to create this ticket? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            ticketService.addTicket(ticket);
            // Save the newly created ticket to the open tickets file immediately
            FileUtil.appendTicketsToOpenFile(OPEN_TICKETS_FILE, List.of(ticket));
            System.out.println("Ticket created successfully with ID: " + ticketId);
        } else {
            System.out.println("Ticket creation cancelled.");
        }
    }

    /*
     * Resolves an existing ticket by changing its status to CLOSED.
     */
    public void resolveTicket(Scanner scanner) {
        System.out.print("Enter ticket ID to resolve: ");
        int ticketId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        if (ticket.getStatus() == Ticket.Status.CLOSED) {
            System.out.println("Ticket is already resolved.");
            return;
        }

        // Confirm ticket resolution
        System.out.print("Do you want to resolve this ticket? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            ticket.setStatus(Ticket.Status.CLOSED);
            // Move the ticket from open to closed tickets file
            moveTicketToClosed(ticket);
            System.out.println("Ticket resolved successfully.");
        } else {
            System.out.println("Ticket resolution cancelled.");
        }
    }

    /**
     * Moves a resolved ticket from the open tickets file to the closed tickets file.
     */
    private void moveTicketToClosed(Ticket ticket) {
        // Read all open tickets
        List<Ticket> openTickets = FileUtil.readTicketsFromFile(OPEN_TICKETS_FILE, customerService.getAllPersons(), agentService.getAllPersons());
        // Remove the resolved ticket from open tickets
        openTickets.removeIf(t -> t.getId() == ticket.getId());
        // Write remaining open tickets back to the open file
        FileUtil.appendTicketsToOpenFile(OPEN_TICKETS_FILE, openTickets);

        // Append the resolved ticket to the closed tickets file
        FileUtil.appendTicketsToClosedFile(CLOSED_TICKETS_FILE, List.of(ticket));
    }

    /**
     * Displays all tickets associated with a specific customer.
     */
    public void viewCustomerTickets(Customer customer) {
        List<Ticket> tickets = ticketService.findTicketsByCustomer(customer);
        if (tickets.isEmpty()) {
            System.out.println("No tickets found for this customer.");
            return;
        }

        System.out.println("Tickets for customer " + customer.getName() + ":");
        for (Ticket ticket : tickets) {
            System.out.println("ID: " + ticket.getId() + ", Status: " + ticket.getStatus() + 
                               ", Topic: " + ticket.getTopic() + ", Issue: " + ticket.getContent());
        }
    }

    /*
     * Displays all tickets in the system, sorted by priority.
     */
    public void viewAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        if (tickets.isEmpty()) {
            System.out.println("No tickets available.");
            return;
        }

        // Sort tickets by priority (3 = High, 2 = Medium, 1 = Low)
        //Got help from chatGPT for this
        tickets.sort((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));

        System.out.println("All tickets:");
        for (Ticket ticket : tickets) {
            System.out.println("ID: " + ticket.getId() + ", Status: " + ticket.getStatus() + 
                               ", Topic: " + ticket.getTopic() + ", Issue: " + ticket.getContent() + 
                               ", Assigned Agent: " + (ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getName() : "None") + 
                               ", Priority: " + (ticket.getPriority() == 1 ? "Low" : ticket.getPriority() == 2 ? "Medium" : "High"));
        }
    }

    /*
     * Adds a comment to a specific ticket.
     */
    public void addCommentToTicket(Scanner scanner, Customer customer, SupportStaffMember agent) {
        System.out.print("Enter ticket ID to add comment: ");
        int ticketId = Integer.parseInt(scanner.nextLine());

        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        System.out.print("Enter your comment: ");
        String commentContent = scanner.nextLine();

        Message message;
        if (customer != null) {
            message = new Message("Customer", customer.getName(), commentContent, LocalDateTime.now());
        } else if (agent != null) {
            message = new Message("Agent", agent.getName(), commentContent, LocalDateTime.now());
        } else {
            System.out.println("Invalid user.");
            return;
        }

        ticketService.addMessageToTicket(ticketId, message);
        // Save the ticket with the new comment immediately
        FileUtil.appendTicketsToOpenFile(OPEN_TICKETS_FILE, List.of(ticketService.getTicketById(ticketId)));
        System.out.println("Comment added successfully.");
    }

    /*
     * Displays all comments for a specific ticket.
     */
    public void viewCommentsForTicket(Scanner scanner) {
        System.out.print("Enter ticket ID to view comments: ");
        int ticketId = Integer.parseInt(scanner.nextLine());

        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        List<Message> messages = ticketService.getMessagesForTicket(ticketId);
        if (messages.isEmpty()) {
            System.out.println("No comments for this ticket.");
            return;
        }

        System.out.println("Comments for ticket ID " + ticketId + ":");
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    /*
     * Sets the priority of a specific ticket.
     */
    public void setTicketPriority(Scanner scanner, SupportStaffMember agent) {
        System.out.print("Enter ticket ID to set priority: ");
        int ticketId = Integer.parseInt(scanner.nextLine());

        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        if (ticket.getAssignedAgent() == null || !ticket.getAssignedAgent().equals(agent)) {
            System.out.println("You do not have permission to change the priority of this ticket.");
            return;
        }

        System.out.print("Enter new priority (1 = Low, 2 = Medium, 3 = High): ");
        int priority = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (priority < 1 || priority > 3) {
            System.out.println("Invalid priority. Please enter a number between 1 and 3.");
            return;
        }

        ticket.setPriority(priority);
        // Save the updated ticket immediately
        FileUtil.appendTicketsToOpenFile(OPEN_TICKETS_FILE, List.of(ticket));
        System.out.println("Ticket priority updated successfully.");
    }

    @FunctionalInterface
    public interface GenerateTicketIdCallback {
        int generate();
    }

    @FunctionalInterface
    public interface GetRandomAgentCallback {
        SupportStaffMember get();
    }
}
