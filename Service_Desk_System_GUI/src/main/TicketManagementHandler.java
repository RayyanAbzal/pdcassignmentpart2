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
import util.DatabaseUtil;
import java.sql.SQLException;

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
    System.out.print("Enter ticket topic: ");
    String topic = scanner.nextLine();
    System.out.print("Enter ticket issue: ");
    String issue = scanner.nextLine();
    int ticketId = generateTicketIdCallback.generateTicketId();

    Ticket ticket = new Ticket(ticketId, customer, null, topic, issue, LocalDateTime.now(), 1);

    // Use DatabaseUtil to insert the ticket into the database
    try {
        DatabaseUtil.insertTicket(ticket);
        System.out.println("Ticket created successfully with ID: " + ticketId);

        // Attempt to assign the ticket to a random agent
        SupportStaffMember assignedAgent = getRandomAgentCallback.getRandomAgent();
        if (assignedAgent != null) {
            ticket.setAssignedAgent(assignedAgent);
            DatabaseUtil.updateTicket(ticket); // Update ticket with assigned agent
            System.out.println("Ticket assigned to agent: " + assignedAgent.getUsername());
        } else {
            System.out.println("No agents available. Ticket created but not assigned.");
        }

    } catch (SQLException e) {
        System.out.println("Error creating ticket: " + e.getMessage());
        e.printStackTrace(); // Optional: print the stack trace for debugging
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
            // Update the ticket status in the database
            try {
                DatabaseUtil.updateTicket(ticket); // Assuming updateTicket method handles updating existing tickets
                System.out.println("Ticket resolved successfully.");
            } catch (SQLException e) {
                System.out.println("Error resolving ticket: " + e.getMessage());
                e.printStackTrace(); // Optional: print the stack trace for debugging
            }
        } else {
            System.out.println("Ticket resolution cancelled.");
        }
    }

    /**
     * Displays all tickets associated with a specific customer.
     */
    public void viewCustomerTickets(Customer customer) {
        List<Ticket> tickets = ticketService.findTicketsByCustomer(customer);
        if (tickets.isEmpty()) {
            System.out.println("No tickets found for this customer.");
        } else {
            System.out.println("Tickets for customer " + customer.getName() + ":");
            for (Ticket ticket : tickets) {
                System.out.println(ticket); // Assuming Ticket has a proper toString() method
            }
        }
    }

    @FunctionalInterface
    public interface GenerateTicketIdCallback {
        int generateTicketId();
    }

    // Define a functional interface for getting a random agent
    @FunctionalInterface
    public interface GetRandomAgentCallback {
        SupportStaffMember getRandomAgent();
    }
}