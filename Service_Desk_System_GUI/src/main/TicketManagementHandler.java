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
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import util.DatabaseUtil;

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

    public TicketManagementHandler(TicketService ticketService, PersonService<SupportStaffMember> agentService,
                                   PersonService<Customer> customerService) {
        this.ticketService = ticketService;
        this.agentService = agentService;
        this.customerService = customerService;
    }

    public void handleViewMyTickets(JFrame frame) {
        // Retrieve the current user session
        UserSession session = UserSession.getInstance();

        // Ensure the user is a customer
        if (!session.getRole().equals("Customer")) {
            JOptionPane.showMessageDialog(frame, "Only customers can view their tickets.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve customer tickets from the service
        Customer customer = customerService.findPersonByEmail(session.getEmail());
        if (customer == null) {
            JOptionPane.showMessageDialog(frame, "Customer not found.", "Ticket Retrieval Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Ticket> tickets = ticketService.findTicketsByCustomer(customer);
        if (tickets == null || tickets.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No tickets found.", "Ticket Retrieval", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a JList to display the tickets
        DefaultListModel<Ticket> listModel = new DefaultListModel<>();
        for (Ticket ticket : tickets) {
            listModel.addElement(ticket);
        }
        JList<Ticket> ticketList = new JList<>(listModel);
        ticketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add a mouse listener to handle double-clicks on the tickets
        ticketList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ticket selectedTicket = ticketList.getSelectedValue();
                if (selectedTicket != null) {
                    showTicketDetails(frame, selectedTicket, listModel); // Pass the list model to the method
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(ticketList);
        JOptionPane.showMessageDialog(frame, scrollPane, "Your Tickets", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTicketDetails(JFrame frame, Ticket ticket, DefaultListModel<Ticket> listModel) {
        // Create a panel to show ticket details
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Ticket ID: " + ticket.getId()));
        panel.add(new JLabel("Topic: " + ticket.getTopic()));
        panel.add(new JLabel("Content: " + ticket.getContent()));
        panel.add(new JLabel("Status: " + ticket.getStatus()));
        panel.add(new JLabel("Created on: " + ticket.getCreatedAt()));

        JButton closeButton = new JButton("Close Ticket");
        closeButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this ticket?", "Close Ticket", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ticketService.resolveTicket(ticket.getId()); // Use resolveTicket method to close the ticket
                listModel.removeElement(ticket); // Remove the closed ticket from the list
                JOptionPane.showMessageDialog(frame, "Ticket closed successfully.");
            }
        });

        JButton messageButton = new JButton("Message Agent");
        messageButton.addActionListener(e -> {
            String message = JOptionPane.showInputDialog(frame, "Enter your message:");
            if (message != null && !message.trim().isEmpty()) {
                // Implement message sending logic here
                JOptionPane.showMessageDialog(frame, "Message sent to the agent.");
            }
        });

        panel.add(closeButton);
        panel.add(messageButton);

        JOptionPane.showMessageDialog(frame, panel, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleTicketCreation(JFrame frame) {
    // Retrieve the current user session
    UserSession session = UserSession.getInstance();

    // Ensure that the current user is a customer
    if (!session.getRole().equals("Customer")) {
        JOptionPane.showMessageDialog(frame, "Only customers can create tickets.", "Access Denied", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Get customer from session
    Customer customer = customerService.findPersonByEmail(session.getEmail());

    if (customer == null) {
        JOptionPane.showMessageDialog(frame, "Customer not found.", "Ticket Creation Failed", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create UI elements for entering ticket details (topic and content)
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    JLabel topicLabel = new JLabel("Enter ticket topic:");
    JTextField topicField = new JTextField();
    JLabel contentLabel = new JLabel("Enter ticket content:");
    JTextArea contentArea = new JTextArea(5, 20);

    panel.add(topicLabel);
    panel.add(topicField);
    panel.add(contentLabel);
    panel.add(new JScrollPane(contentArea));

    int option = JOptionPane.showConfirmDialog(frame, panel, "Create Ticket", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (option == JOptionPane.OK_OPTION) {
        String topic = topicField.getText();
        String content = contentArea.getText();

        SupportStaffMember agent = getAvailableAgent();
        if (agent == null) {
            JOptionPane.showMessageDialog(frame, "No available agents. Please try again later.", "Ticket Creation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Retrieve the maximum ticket ID from the database and increment it for the new ticket
        int ticketId;
        try {
            ticketId = DatabaseUtil.getMaxTicketId() + 1; // Increment the maximum ID
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving the ticket ID: " + e.getMessage(), "Ticket Creation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ticket ticket = new Ticket(ticketId, customer, agent, topic, content, LocalDateTime.now(), 1);
        ticketService.addTicket(ticket); // Ensure this method properly saves the ticket

        JOptionPane.showMessageDialog(frame, "Ticket created successfully. Ticket ID: " + ticketId, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

    private SupportStaffMember getAvailableAgent() {
        List<SupportStaffMember> agents = agentService.getAllSupportStaff(); // Ensure this method exists and returns a list
        SupportStaffMember selectedAgent = null;
        int minTickets = Integer.MAX_VALUE;

        for (SupportStaffMember agent : agents) {
            int ticketCount = ticketService.getAssignedTicketCount(agent); // Ensure this method exists and returns an int
            if (ticketCount < minTickets) {
                minTickets = ticketCount;
                selectedAgent = agent;
            } else if (ticketCount == minTickets && selectedAgent != null) {
                // Randomly select between the current selected agent and this agent
                selectedAgent = Math.random() < 0.5 ? selectedAgent : agent;
            }
        }

        return selectedAgent;
    }

    @FunctionalInterface
    public interface GenerateIdCallback {
        int generateId();
    }
}