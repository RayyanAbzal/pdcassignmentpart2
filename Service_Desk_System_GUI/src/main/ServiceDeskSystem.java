/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import services.PersonService;
import util.DatabaseUtil;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import service.desk.system.Ticket;
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
    private JFrame frame;
    private PersonService<Customer> customerService;
    private PersonService<SupportStaffMember> supportStaffService;
    private TicketService ticketService;
    private CustomerRegistrationHandler customerRegistrationHandler;
    private CustomerLoginHandler customerLoginHandler;
    private TicketManagementHandler ticketManagementHandler;
    private AgentRegistrationHandler agentRegistrationHandler;
    private AgentLoginHandler agentLoginHandler;

    public ServiceDeskSystem() {
        frame = new JFrame("Service Desk System");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize the PersonServices
        customerService = new PersonService<>(Customer.class);
        supportStaffService = new PersonService<>(SupportStaffMember.class);
        ticketService = new TicketService(); // Initialize ticketService here

        // Initialize handlers
        ticketManagementHandler = new TicketManagementHandler(ticketService, supportStaffService, customerService);
        customerRegistrationHandler = new CustomerRegistrationHandler(customerService, this::setLastMessage);
        customerLoginHandler = new CustomerLoginHandler(customerService, this::setLastMessage, this);
        agentRegistrationHandler = new AgentRegistrationHandler(supportStaffService, this::setLastMessage);
        agentLoginHandler = new AgentLoginHandler(supportStaffService, this::setLastMessage, this);

        // Create the main menu panel with a better layout and styling
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(54, 57, 63)); // Set granite background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title Label
        JLabel titleLabel = new JLabel("Service Desk System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.LIGHT_GRAY); // Light grey text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add space around title
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Buttons
        JButton customerLoginButton = createStyledButton("Customer Login");
        customerLoginButton.addActionListener(e -> customerLoginHandler.handleLogin(frame));

        JButton agentLoginButton = createStyledButton("Agent Login");
        agentLoginButton.addActionListener(e -> agentLoginHandler.handleLogin(frame));

        JButton registerCustomerButton = createStyledButton("Register as Customer");
        registerCustomerButton.addActionListener(e -> customerRegistrationHandler.handleRegistration(frame));

        JButton registerAgentButton = createStyledButton("Register as Agent");
        registerAgentButton.addActionListener(e -> agentRegistrationHandler.handleRegistration(frame));

        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> {
            DatabaseUtil.closeConnection();
            System.exit(0);
        });

        // Adding buttons to the panel
        gbc.gridy = 1;
        panel.add(customerLoginButton, gbc);
        gbc.gridy = 2;
        panel.add(agentLoginButton, gbc);
        gbc.gridy = 3;
        panel.add(registerCustomerButton, gbc);
        gbc.gridy = 4;
        panel.add(registerAgentButton, gbc);
        gbc.gridy = 5;
        panel.add(exitButton, gbc);

        // Adding the panel to the frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void setLastMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    // Method to create a styled JButton with light grey borders
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180)); // Steel blue for buttons
        button.setForeground(Color.LIGHT_GRAY); // Light grey text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), // Light grey border
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        return button;
    }

    public void showCustomerMenu(Customer customer) {
        frame.getContentPane().removeAll();
        frame.repaint();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(54, 57, 63)); // Granite background
        JButton createTicketButton = createStyledButton("Create Ticket");
        createTicketButton.addActionListener(e -> ticketManagementHandler.handleTicketCreation(frame));

        JButton viewTicketsButton = createStyledButton("View Tickets");
        viewTicketsButton.addActionListener(e -> ticketManagementHandler.handleViewMyTickets(frame));

        JButton logoutButton = createStyledButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            showMainMenu();
        });

        buttonPanel.add(createTicketButton);
        buttonPanel.add(viewTicketsButton);
        buttonPanel.add(logoutButton);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(new Color(54, 57, 63)); // Granite background
        displayUserInfo(userInfoPanel);

        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public void showAgentMenu(SupportStaffMember agent) {
        frame.getContentPane().removeAll();
        frame.repaint();

        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(54, 57, 63)); // Granite background
        JButton viewTicketsButton = createStyledButton("View Assigned Tickets");
        viewTicketsButton.addActionListener(e -> ticketManagementHandler.handleViewAssignedTickets(frame));

        JButton logoutButton = createStyledButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            showMainMenu();
        });

        buttonPanel.add(viewTicketsButton);
        buttonPanel.add(logoutButton);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(new Color(54, 57, 63)); // Granite background
        displayUserInfo(userInfoPanel);

        panel.add(userInfoPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    public void displayUserInfo(JPanel panel) {
        UserSession session = UserSession.getInstance();
        String userInfo = String.format("Role: %s | Email: %s | Name: %s | Username: %s | ID: %d",
                session.getRole(),
                session.getEmail(),
                session.getName() != null ? session.getName() : "N/A",
                session.getUsername() != null ? session.getUsername() : "N/A",
                session.getId());

        JLabel userInfoLabel = new JLabel(userInfo);
        userInfoLabel.setForeground(Color.LIGHT_GRAY); // Light grey text
        panel.add(userInfoLabel, 0);
    }

    private void showMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(54, 57, 63)); // Granite background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Service Desk System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.LIGHT_GRAY); // Light grey text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JButton customerLoginButton = createStyledButton("Customer Login");
        customerLoginButton.addActionListener(e -> customerLoginHandler.handleLogin(frame));

        JButton agentLoginButton = createStyledButton("Agent Login");
        agentLoginButton.addActionListener(e -> agentLoginHandler.handleLogin(frame));

        JButton registerCustomerButton = createStyledButton("Register as Customer");
        registerCustomerButton.addActionListener(e -> customerRegistrationHandler.handleRegistration(frame));

        JButton registerAgentButton = createStyledButton("Register as Agent");
        registerAgentButton.addActionListener(e -> agentRegistrationHandler.handleRegistration(frame));

        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> {
            DatabaseUtil.closeConnection();
            System.exit(0);
        });

        gbc.gridy = 1;
        panel.add(customerLoginButton, gbc);
        gbc.gridy = 2;
        panel.add(agentLoginButton, gbc);
        gbc.gridy = 3;
        panel.add(registerCustomerButton, gbc);
        gbc.gridy = 4;
        panel.add(registerAgentButton, gbc);
        gbc.gridy = 5;
        panel.add(exitButton, gbc);

        frame.add(panel, BorderLayout.CENTER);
        frame.revalidate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServiceDeskSystem::new);
    }
} 