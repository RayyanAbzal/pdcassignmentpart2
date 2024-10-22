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
        return getAllTickets().stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst()
                .orElse(null);
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
        return List.of(); // Return an empty list instead of null
    }

    /*
     * Retrieves all open tickets from the database.
     */
    public List<Ticket> getOpenTickets() {
        return getAllTickets().stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.OPEN)
                .collect(Collectors.toList());
    }

    /*
     * Resolves a ticket by setting its status to CLOSED in the database.
     */
    public void resolveTicket(int id) {
        Ticket ticket = getTicketById(id);
        if (ticket != null) {
            ticket.setStatus(Ticket.Status.CLOSED);
            try {
                DatabaseUtil.updateTicket(ticket);
                System.out.println("Ticket ID " + id + " resolved.");
            } catch (SQLException e) {
                System.out.println("Error updating ticket: " + e.getMessage());
            }
        } else {
            System.out.println("Ticket ID " + id + " not found.");
        }
    }

    /*
     * Adds a message to a ticket.
     */
    public void addMessageToTicket(int ticketId, Message message) {
        Ticket ticket = getTicketById(ticketId);
        if (ticket != null) {
            ticket.addMessage(message);
            try {
                DatabaseUtil.updateTicket(ticket);
                System.out.println("Message added to ticket ID " + ticketId);
            } catch (SQLException e) {
                System.out.println("Error updating ticket: " + e.getMessage());
            }
        } else {
            System.out.println("Ticket ID " + ticketId + " not found.");
        }
    }

    /*
     * Retrieves all messages associated with a specific ticket.
     */
    public List<Message> getMessagesForTicket(int ticketId) {
        Ticket ticket = getTicketById(ticketId);
        return ticket != null ? ticket.getMessages() : null;
    }

    /*
     * Finds all tickets associated with a specific customer.
     */
    public List<Ticket> findTicketsByCustomer(Customer customer) {
        return getAllTickets().stream()
                .filter(ticket -> ticket.getCustomer() != null && ticket.getCustomer().getId() == customer.getId())
                .peek(ticket -> System.out.println("Searching for tickets for customer ID: " + customer.getId()))
                .collect(Collectors.toList());
    }

    /*
     * Retrieves tickets assigned to a specific agent.
     */
    public List<Ticket> getTicketsAssignedToAgent(SupportStaffMember agent) {
        return getAllTickets().stream()
                .filter(ticket -> ticket.getAssignedAgent() != null && ticket.getAssignedAgent().getId() == agent.getId())
                .peek(ticket -> System.out.println("Searching for tickets for agent: " + agent.getUsername()))
                .collect(Collectors.toList());
    }

    /*
     * Counts the number of tickets assigned to a specific agent.
     */
    public int getAssignedTicketCount(SupportStaffMember agent) {
        return (int) getAllTickets().stream()
                .filter(ticket -> ticket.getAssignedAgent() != null && ticket.getAssignedAgent().getId() == agent.getId())
                .count();
    }
}