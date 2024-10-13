/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import service.desk.system.Ticket;
import util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Integer, Ticket> tickets = new HashMap<>();
    private Map<Integer, List<Message>> ticketMessages = new HashMap<>();

    /*
     * Adds a new ticket to the system.
     * Initializes an empty list of messages for the new ticket.
     */
    public void addTicket(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
        ticketMessages.put(ticket.getId(), new ArrayList<>());
    }

    /*
     * Retrieves a ticket by its ID.
     */
    public Ticket getTicketById(int id) {
        return tickets.get(id);
    }

    /*
     * Retrieves all tickets.
     */
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    /*
     * Retrieves all open tickets.
     */
    public List<Ticket> getOpenTickets() {
        List<Ticket> openTickets = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getStatus() == Ticket.Status.OPEN) {
                openTickets.add(ticket);
            }
        }
        return openTickets;
    }

    /*
     * Resolves a ticket by setting its status to CLOSED.
     */
    public void resolveTicket(int id) {
        Ticket ticket = tickets.get(id);
        if (ticket != null) {
            ticket.setStatus(Ticket.Status.CLOSED);
        }
    }

    /*
     * Adds a message to a ticket and updates the associated message list.
     */
    public void addMessageToTicket(int ticketId, Message message) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.addMessage(message);
            ticketMessages.get(ticketId).add(message);
        }
    }

    /*
     * Retrieves all messages associated with a specific ticket.
     */
    public List<Message> getMessagesForTicket(int ticketId) {
        return ticketMessages.get(ticketId);
    }

    /*
     * Finds all tickets associated with a specific customer..
     */
    public List<Ticket> findTicketsByCustomer(Customer customer) {
        List<Ticket> customerTickets = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getCustomer().equals(customer)) {
                customerTickets.add(ticket);
            }
        }
        return customerTickets;
    }
}