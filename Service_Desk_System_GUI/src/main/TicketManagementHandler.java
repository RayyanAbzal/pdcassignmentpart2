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
import java.util.Comparator;
import java.util.List;
import service.desk.system.Message;
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
        UserSession session = UserSession.getInstance();

        if (!session.getRole().equals("Customer")) {
            showErrorDialog(frame, "Only customers can view their tickets.", "Access Denied");
            return;
        }

        Customer customer = customerService.findPersonByEmail(session.getEmail());
        if (customer == null) {
            showErrorDialog(frame, "Customer not found.", "Ticket Retrieval Failed");
            return;
        }

        List<Ticket> tickets = ticketService.findTicketsByCustomer(customer);
        if (tickets == null || tickets.isEmpty()) {
            showInfoDialog(frame, "No tickets found.", "Ticket Retrieval");
            return;
        }

        // Sort tickets by priority (highest to lowest)
        tickets.sort(Comparator.comparingInt(Ticket::getPriority).reversed());

        displayTickets(frame, tickets, "Your Tickets", true);
    }

    public void handleViewAssignedTickets(JFrame frame) {
        UserSession session = UserSession.getInstance();

        if (!session.getRole().equals("Agent")) {
            showErrorDialog(frame, "Only agents can view assigned tickets.", "Access Denied");
            return;
        }

        SupportStaffMember agent = agentService.findPersonByEmail(session.getEmail());
        if (agent == null) {
            showErrorDialog(frame, "Agent not found.", "Ticket Retrieval Failed");
            return;
        }

        List<Ticket> tickets = ticketService.getTicketsAssignedToAgent(agent);
        if (tickets == null || tickets.isEmpty()) {
            showInfoDialog(frame, "No assigned tickets found.", "Ticket Retrieval");
            return;
        }

        // Sort tickets by priority (highest to lowest)
        tickets.sort(Comparator.comparingInt(Ticket::getPriority).reversed());

        displayTickets(frame, tickets, "Assigned Tickets", false);
    }

    private void displayTickets(JFrame frame, List<Ticket> tickets, String title, boolean isCustomer) {
    DefaultListModel<Ticket> listModel = new DefaultListModel<>();
    tickets.forEach(listModel::addElement);

    JList<Ticket> ticketList = new JList<>(listModel);
    ticketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Set custom renderer to highlight tickets based on priority
    ticketList.setCellRenderer(new TicketListCellRenderer());

    ticketList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            Ticket selectedTicket = ticketList.getSelectedValue();
            if (selectedTicket != null) {
                if (isCustomer) {
                    showTicketDetails(frame, selectedTicket, listModel);
                } else {
                    showAgentTicketDetails(frame, selectedTicket, listModel);
                }
            }
        }
    });

    JScrollPane scrollPane = new JScrollPane(ticketList);
    JOptionPane.showMessageDialog(frame, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
}

    private void showAgentTicketDetails(JFrame frame, Ticket ticket, DefaultListModel<Ticket> listModel) {
        JPanel panel = createTicketDetailsPanel(ticket);
        JButton setPriorityButton = createPriorityButton(frame, ticket);
        JButton resolveButton = createResolveButton(frame, ticket, listModel);
        JButton messageButton = createMessageButton(frame, ticket);

        panel.add(setPriorityButton);
        panel.add(resolveButton);
        panel.add(messageButton);

        JOptionPane.showMessageDialog(frame, panel, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTicketDetails(JFrame frame, Ticket ticket, DefaultListModel<Ticket> listModel) {
        JPanel panel = createTicketDetailsPanel(ticket);
        JButton closeButton = createCloseButton(frame, ticket, listModel);
        JButton messageButton = createMessageButton(frame, ticket);

        panel.add(closeButton);
        panel.add(messageButton);

        JOptionPane.showMessageDialog(frame, panel, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createTicketDetailsPanel(Ticket ticket) {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Adding some padding

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Ticket ID: " + ticket.getId()), gbc);

    gbc.gridy++;
    panel.add(new JLabel("Topic: " + ticket.getTopic()), gbc);

    gbc.gridy++;
    panel.add(new JLabel("Content: " + ticket.getContent()), gbc);

    gbc.gridy++;
    panel.add(new JLabel("Status: " + ticket.getStatus()), gbc);

    gbc.gridy++;
    panel.add(new JLabel("Priority: " + ticket.getPriority()), gbc);

    gbc.gridy++;
    panel.add(new JLabel("Created on: " + ticket.getCreatedAt()), gbc);

    return panel;
}

    private JButton createPriorityButton(JFrame frame, Ticket ticket) {
        JButton button = new JButton("Set Priority (1-3)");
        button.addActionListener(e -> {
            String priorityStr = JOptionPane.showInputDialog(frame, "Enter priority (1-3):");
            if (priorityStr != null && priorityStr.matches("[1-3]")) {
                int priority = Integer.parseInt(priorityStr);
                ticket.setPriority(priority);
                updateTicket(frame, ticket, "Priority set to " + priority);
            } else {
                showErrorDialog(frame, "Invalid priority. Please enter a number between 1 and 3.", "Error");
            }
        });
        return button;
    }

    private JButton createResolveButton(JFrame frame, Ticket ticket, DefaultListModel<Ticket> listModel) {
        JButton button = new JButton("Resolve Ticket");
        button.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to resolve this ticket?", "Resolve Ticket", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ticketService.resolveTicket(ticket.getId());
                JOptionPane.showMessageDialog(frame, "Ticket resolved successfully.");
                listModel.removeElement(ticket);
            }
        });
        return button;
    }

    private JButton createCloseButton(JFrame frame, Ticket ticket, DefaultListModel<Ticket> listModel) {
        JButton button = new JButton("Close Ticket");
        button.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this ticket?", "Close Ticket", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ticketService.resolveTicket(ticket.getId());
                listModel.removeElement(ticket);
                JOptionPane.showMessageDialog(frame, "Ticket closed successfully.");
            }
        });
        return button;
    }

    private JButton createMessageButton(JFrame frame, Ticket ticket) {
    JButton button = new JButton("Leave/View Messages");
    button.addActionListener(e -> {
        List<Message> messages = null;
        try {
            messages = DatabaseUtil.getMessagesForTicket(ticket.getId());
        } catch (SQLException ex) {
            showErrorDialog(frame, "Error retrieving messages: " + ex.getMessage(), "Error");
            return;
        }

        // Create a modern styled panel for messages
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10)); // Add padding
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add margin

        // Text Area for displaying messages, in a more readable format
        JTextArea messageArea = new JTextArea(12, 40);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Modern font
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(new Color(240, 240, 240)); // Subtle background
        messageArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        StringBuilder messageDisplay = new StringBuilder("Messages for Ticket " + ticket.getId() + ":\n\n");
        if (messages != null && !messages.isEmpty()) {
            for (Message message : messages) {
                messageDisplay.append(message.getTimestamp())
                    .append(" - [").append(message.getSenderType())
                    .append("] ").append(message.getSenderName())
                    .append(": ").append(message.getContent())
                    .append("\n\n"); // Add spacing between messages
            }
        } else {
            messageDisplay.append("No messages yet.\n");
        }

        messageArea.setText(messageDisplay.toString());
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(null); // No visible border for cleaner look
        messagePanel.add(scrollPane, BorderLayout.CENTER);

        // TextField for new message input with better layout
        JTextField newMessageField = new JTextField();
        newMessageField.setFont(new Font("Arial", Font.PLAIN, 14));
        newMessageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Panel for input label and field
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Enter your message:"), BorderLayout.NORTH);
        inputPanel.add(newMessageField, BorderLayout.CENTER);
        messagePanel.add(inputPanel, BorderLayout.SOUTH);

        // Show the panel in a dialog with consistent padding and cleaner buttons
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 13)); // Modernize buttons
        int result = JOptionPane.showConfirmDialog(frame, messagePanel, "Messages", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newMessageContent = newMessageField.getText().trim();
            if (!newMessageContent.isEmpty()) {
                UserSession session = UserSession.getInstance();

                if (ticket.getId() <= 0) {
                    showErrorDialog(frame, "Invalid ticket ID.", "Error");
                    return;
                }

                String senderType = session.getRole();
                String senderName = session.getName();

                if (senderType == null || senderType.isEmpty()) {
                    showErrorDialog(frame, "Sender type cannot be null or empty.", "Error");
                    return;
                }

                if (senderName == null || senderName.isEmpty()) {
                    showErrorDialog(frame, "Sender name cannot be null or empty.", "Error");
                    return;
                }

                try {
                    if (!DatabaseUtil.ticketExists(ticket.getId())) {
                        showErrorDialog(frame, "Ticket does not exist.", "Error");
                        return;
                    }
                } catch (SQLException ex) {
                    showErrorDialog(frame, "Error checking ticket existence: " + ex.getMessage(), "Error");
                    return;
                }

                Message newMessage = new Message(0, ticket.getId(), senderType, senderName, newMessageContent, LocalDateTime.now());

                try {
                    DatabaseUtil.insertMessage(newMessage);
                    messages = DatabaseUtil.getMessagesForTicket(ticket.getId());
                    StringBuilder updatedDisplay = new StringBuilder("Messages for Ticket " + ticket.getId() + ":\n\n");

                    for (Message message : messages) {
                        updatedDisplay.append(message.getTimestamp())
                            .append(" - [").append(message.getSenderType())
                            .append("] ").append(message.getSenderName())
                            .append(": ").append(message.getContent())
                            .append("\n\n");
                    }

                    messageArea.setText(updatedDisplay.toString());
                    JOptionPane.showMessageDialog(frame, "Message sent successfully.");
                } catch (SQLException ex) {
                    showErrorDialog(frame, "Error sending message: " + ex.getMessage(), "Error");
                }
            }
        }
    });
    return button;
}


    public void handleTicketCreation(JFrame frame) {
        UserSession session = UserSession.getInstance();

        if (!session.getRole().equals("Customer")) {
            showErrorDialog(frame, "Only customers can create tickets.", "Access Denied");
            return;
        }

        Customer customer = customerService.findPersonByEmail(session.getEmail());
        if (customer == null) {
            showErrorDialog(frame, "Customer not found.", "Ticket Creation Failed");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField topicField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 20);
        panel.add(new JLabel("Enter ticket topic:"));
        panel.add(topicField);
        panel.add(new JLabel("Enter ticket content:"));
        panel.add(new JScrollPane(contentArea));

        int option = JOptionPane.showConfirmDialog(frame, panel, "Create Ticket", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            createTicket(frame, customer, topicField.getText(), contentArea.getText());
        }
    }

    private void createTicket(JFrame frame, Customer customer, String topic, String content) {
        SupportStaffMember agent = getAvailableAgent();
        if (agent == null) {
            showErrorDialog(frame, "No available agents. Please try again later.", "Ticket Creation Failed");
            return;
        }

        int ticketId = fetchNextTicketId(frame);
        if (ticketId == -1) return;

        Ticket ticket = new Ticket(ticketId, customer, agent, topic, content, LocalDateTime.now(), 1);
        ticketService.addTicket(ticket);
        showInfoDialog(frame, "Ticket created successfully. Ticket ID: " + ticketId, "Success");
    }

    private int fetchNextTicketId(JFrame frame) {
        try {
            return DatabaseUtil.getMaxTicketId() + 1;
        } catch (SQLException e) {
            showErrorDialog(frame, "Error retrieving the ticket ID: " + e.getMessage(), "Ticket Creation Failed");
            return -1;
        }
    }

    private SupportStaffMember getAvailableAgent() {
        List<SupportStaffMember> agents = agentService.getAllSupportStaff();
        return agents.stream()
                .reduce((a, b) -> {
                    int countA = ticketService.getAssignedTicketCount(a);
                    int countB = ticketService.getAssignedTicketCount(b);
                    return (countA < countB) ? a : (countA == countB ? Math.random() < 0.5 ? a : b : b);
                }).orElse(null);
    }

    private void updateTicket(JFrame frame, Ticket ticket, String successMessage) {
        try {
            DatabaseUtil.updateTicket(ticket);
            showInfoDialog(frame, successMessage, "Success");
        } catch (SQLException ex) {
            showErrorDialog(frame, "Failed to update ticket: " + ex.getMessage(), "Error");
        }
    }
    //help from chatgpt for the below method
    private class TicketListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Ticket) {
            Ticket ticket = (Ticket) value;
            // Change background based on priority
            if (ticket.getPriority() == 2) {
                renderer.setBackground(new Color(255, 223, 186)); // Light orange
            } else if (ticket.getPriority() == 3) {
                renderer.setBackground(new Color(255, 182, 182)); // Light red
            } else {
                renderer.setBackground(list.getBackground()); // Default background
            }
            if (isSelected) {
                renderer.setBackground(list.getSelectionBackground()); // Override to selected background if selected
            }
        }
        return renderer;
    }
}

    private void showErrorDialog(JFrame frame, String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(JFrame frame, String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @FunctionalInterface
    public interface GenerateIdCallback {
        int generateId();
    }
}