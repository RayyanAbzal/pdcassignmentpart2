/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import service.desk.system.Ticket;
import util.DatabaseUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import service.desk.system.Customer;
import service.desk.system.Message;
import service.desk.system.SupportStaffMember;
/**
 *
 * @author rayyanabzal
 */

/*
 * Service class for managing tickets in the Service Desk System.
 * Handles operations related to tickets, including adding, retrieving, and resolving tickets,
 * as well as managing ticket messages.
 */
public class TicketService {

    /*
     * Adds a new ticket to the database.
     */
    public void addTicket(Ticket ticket) {
        try {
            DatabaseUtil.insertTicket(ticket);
            System.out.println("Ticket added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding ticket: " + e.getMessage());
        }
    }

    /*
     * Retrieves a ticket by its ID from the database.
     */
    public Ticket getTicketById(int id) {
        try {
            List<Ticket> allTickets = DatabaseUtil.getAllTickets();
            for (Ticket ticket : allTickets) {
                if (ticket.getId() == id) {
                    return ticket;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving ticket: " + e.getMessage());
        }
        return null;
    }

    /*
     * Retrieves all tickets from the database.
     */
    public List<Ticket> getAllTickets() {
        try {
            List<Ticket> tickets = DatabaseUtil.getAllTickets();
            if (tickets.isEmpty()) {
                System.out.println("No tickets found.");
            }
            return tickets;
        } catch (SQLException e) {
            System.out.println("Error retrieving tickets: " + e.getMessage());
        }
        return null;
    }

    /*
     * Retrieves all open tickets from the database.
     */
    public List<Ticket> getOpenTickets() {
        try {
            List<Ticket> openTickets = DatabaseUtil.getAllTickets();
            openTickets.removeIf(ticket -> ticket.getStatus() != Ticket.Status.OPEN);
            if (openTickets.isEmpty()) {
                System.out.println("No open tickets found.");
            }
            return openTickets;
        } catch (SQLException e) {
            System.out.println("Error retrieving open tickets: " + e.getMessage());
        }
        return null;
    }

    /*
     * Resolves a ticket by setting its status to CLOSED in the database.
     */
    public void resolveTicket(int id) {
        try {
            Ticket ticket = getTicketById(id);
            if (ticket != null) {
                ticket.setStatus(Ticket.Status.CLOSED);
                DatabaseUtil.updateTicket(ticket);
                System.out.println("Ticket ID " + id + " resolved.");
            } else {
                System.out.println("Ticket ID " + id + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error resolving ticket: " + e.getMessage());
        }
    }

    /*
     * Adds a message to a ticket.
     */
    public void addMessageToTicket(int ticketId, Message message) {
        try {
            Ticket ticket = getTicketById(ticketId);
            if (ticket != null) {
                ticket.addMessage(message);
                DatabaseUtil.updateTicket(ticket);
                System.out.println("Message added to ticket ID " + ticketId);
            } else {
                System.out.println("Ticket ID " + ticketId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding message to ticket: " + e.getMessage());
        }
    }

    /*
     * Retrieves all messages associated with a specific ticket.
     */
    public List<Message> getMessagesForTicket(int ticketId) {
        Ticket ticket = getTicketById(ticketId);
        if (ticket != null) {
            return ticket.getMessages();
        } else {
            System.out.println("Ticket ID " + ticketId + " not found.");
        }
        return null;
    }

    /*
     * Finds all tickets associated with a specific customer.
     */
    public List<Ticket> findTicketsByCustomer(Customer customer) {
    try {
        List<Ticket> allTickets = DatabaseUtil.getAllTickets();
        // Use the customer's ID for comparison
        List<Ticket> customerTickets = allTickets.stream()
        .filter(ticket -> ticket.getCustomer() != null && ticket.getCustomer().getId() == customer.getId())
        .collect(Collectors.toList());

        System.out.println("Searching for tickets for customer ID: " + customer.getId());
        if (customerTickets.isEmpty()) {
        System.out.println("No tickets found for customer ID " + customer.getId());
        } else {
        System.out.println("Tickets found: " + customerTickets.size());
    }

        return customerTickets;
    } catch (SQLException e) {
        System.out.println("Error finding tickets by customer: " + e.getMessage());
    }
    return null;
    }
    
    public List<Ticket> getTicketsAssignedToAgent(SupportStaffMember agent) {
        try {
            List<Ticket> allTickets = DatabaseUtil.getAllTickets();
            // Filter tickets that are assigned to the provided agent
            List<Ticket> assignedTickets = allTickets.stream()
                    .filter(ticket -> ticket.getAssignedAgent() != null && ticket.getAssignedAgent().equals(agent))
                    .collect(Collectors.toList());

            if (assignedTickets.isEmpty()) {
                System.out.println("No tickets found for agent: " + agent.getUsername());
            }
            return assignedTickets;
        } catch (SQLException e) {
            System.out.println("Error retrieving tickets for agent: " + e.getMessage());
        }
        return null;
    }
    
    public int getAssignedTicketCount(SupportStaffMember agent) {
        try {
            // Retrieve all tickets from the database
            List<Ticket> tickets = DatabaseUtil.getAllTickets();
            // Count the number of tickets assigned to the specified agent
            return (int) tickets.stream()
                    .filter(ticket -> ticket.getAssignedAgent() != null && ticket.getAssignedAgent().equals(agent))
                    .count();
        } catch (SQLException e) {
            System.out.println("Error retrieving assigned ticket count: " + e.getMessage());
            return 0; // Return 0 if an error occurs
        }
    }
}
