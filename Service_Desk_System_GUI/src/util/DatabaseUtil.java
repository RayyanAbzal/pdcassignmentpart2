/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import service.desk.system.Customer;
import service.desk.system.Message;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;

/**
 *
 * @author rayyanabzal
 */
/**
 * Utility class to handle database connections and operations for the Service Desk System.
 */
public class DatabaseUtil {
    // Embedded database URL for Apache Derby
    //private static final String DB_URL = "jdbc:derby:servicedesksystem_ebd;create=true";
    private static final String DB_URL = "jdbc:derby:/Users/rayyanabzal/Documents/pdcassignmentpart2/Service_Desk_System_GUI/servicedesksystem_ebd;create=true";
    

    private static Connection connection;

    public static void main(String[] args) {
        try {
            initializeDatabase();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        } finally {
            closeConnection(); // Ensure the connection is closed afterwards
        }
    }

    // Establish a connection to the embedded database
    private static void establishConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
    }

    public static Connection getConnection() throws SQLException {
        establishConnection(); // Ensure the connection is re-established if closed
        return connection;
    }

    // Close the database connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Reset connection to null after closing
                System.out.println("Connection closed.");
            } catch (SQLException ex) {
                System.out.println("Failed to close connection: " + ex.getMessage());
            }
        }
    }


    // Create the tables for customers, agents, and tickets if they do not exist
    public static void initializeDatabase() throws SQLException {
        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();

            // Check and create Customers table if it doesn't exist
            if (!tableExists("Customers")) {
                String createCustomersTable = "CREATE TABLE Customers (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        "firstName VARCHAR(100)," + // Changed from name to firstName
                        "lastName VARCHAR(100)," +  // Added lastName
                        "email VARCHAR(100) UNIQUE," +
                        "password VARCHAR(100))";
                stmt.executeUpdate(createCustomersTable);
                System.out.println("Customers table created successfully.");
            }

            // Check and create SupportStaff table if it doesn't exist
            if (!tableExists("SupportStaff")) {
                String createSupportStaffTable = "CREATE TABLE SupportStaff (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        "firstName VARCHAR(100)," + // Added firstName
                        "lastName VARCHAR(100)," +  // Added lastName
                        "username VARCHAR(50)," +
                        "email VARCHAR(100) UNIQUE," +
                        "password VARCHAR(100))";
                stmt.executeUpdate(createSupportStaffTable);
                System.out.println("SupportStaff table created successfully.");
            }

            // Check and create Tickets table if it doesn't exist
            if (!tableExists("Tickets")) {
                String createTicketsTable = "CREATE TABLE Tickets (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                        "customerId INT," +
                        "agentId INT," +
                        "topic VARCHAR(100)," +
                        "content VARCHAR(255)," +
                        "createdAt TIMESTAMP," +
                        "priority INT," +
                        "status VARCHAR(10)," +
                        "FOREIGN KEY (customerId) REFERENCES Customers(id)," +
                        "FOREIGN KEY (agentId) REFERENCES SupportStaff(id))";
                stmt.executeUpdate(createTicketsTable);
                System.out.println("Tickets table created successfully.");
            }
            
            if (!tableExists("Messages")) {
            String createMessagesTable = "CREATE TABLE Messages (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    "ticket_id INT," +
                    "sender_type VARCHAR(50)," +
                    "sender_name VARCHAR(100)," +
                    "content VARCHAR(255)," +
                    "timestamp TIMESTAMP," +
                    "FOREIGN KEY (ticket_id) REFERENCES Tickets(id))";
            stmt.executeUpdate(createMessagesTable);
            System.out.println("Messages table created successfully.");
        }

        } catch (SQLException e) {
            throw new SQLException("Error initializing the database: " + e.getMessage(), e);
        } finally {
            // Ensure that the statement is closed
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Failed to close statement: " + e.getMessage());
                }
            }
        }
    }

    // Utility method to check if a table exists in the database
    private static boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }

    // Method to check if email already exists
    public static boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE email = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Insert a new customer into the database
    public static int insertCustomer(Customer customer) throws SQLException {
        String insertCustomerSQL = "INSERT INTO Customers (firstName, lastName, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getFirstName()); // Get first name from Customer
            pstmt.setString(2, customer.getLastName()); // Get last name from Customer
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());  // No hashing, plain password saved
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    // Insert a new support staff member into the database
    public static void insertSupportStaff(SupportStaffMember staff) throws SQLException {
        String insertSupportStaffSQL = "INSERT INTO SupportStaff (firstName, lastName, username, email, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertSupportStaffSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, staff.getFirstName()); // Get first name from SupportStaffMember
            pstmt.setString(2, staff.getLastName()); // Get last name from SupportStaffMember
            pstmt.setString(3, staff.getUsername());
            pstmt.setString(4, staff.getEmail());
            pstmt.setString(5, staff.getPassword());  // Save plain password directly
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);  // Get the generated ID if needed
                    System.out.println("New Support Staff Member ID: " + newId);
                } else {
                    throw new SQLException("Creating support staff member failed, no ID obtained.");
                }
            }
        }
    }

    // Insert a new ticket into the database
    public static void insertTicket(Ticket ticket) throws SQLException {
        String query = "INSERT INTO Tickets (customerId, agentId, topic, content, createdAt, priority, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, ticket.getCustomer().getId());
            pstmt.setObject(2, ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getId() : null, Types.INTEGER);
            pstmt.setString(3, ticket.getTopic());
            pstmt.setString(4, ticket.getContent());
            pstmt.setTimestamp(5, Timestamp.valueOf(ticket.getCreatedAt()));
            pstmt.setInt(6, ticket.getPriority());
            pstmt.setString(7, ticket.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to insert ticket into the database.", e);
        }
    }

    // Update an existing ticket in the database
    public static void updateTicket(Ticket ticket) throws SQLException {
        String query = "UPDATE Tickets SET customerId = ?, agentId = ?, topic = ?, content = ?, createdAt = ?, priority = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, ticket.getCustomer().getId());
            pstmt.setObject(2, ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getId() : null, Types.INTEGER);
            pstmt.setString(3, ticket.getTopic());
            pstmt.setString(4, ticket.getContent());
            pstmt.setTimestamp(5, Timestamp.valueOf(ticket.getCreatedAt()));
            pstmt.setInt(6, ticket.getPriority());
            pstmt.setString(7, ticket.getStatus().toString());
            pstmt.setInt(8, ticket.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Failed to update ticket in the database.", e);
        }
    }

    // Retrieve all customers from the database
    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName"); // Retrieve first name
                String lastName = rs.getString("lastName");   // Retrieve last name
                String email = rs.getString("email");
                String password = rs.getString("password");
                customers.add(new Customer(id, firstName, lastName, email, password)); // Create Customer with first and last name
            }
        }
        return customers;
    }

    // Retrieve all support staff members from the database
    public static List<SupportStaffMember> getAllSupportStaff() throws SQLException {
        List<SupportStaffMember> staffMembers = new ArrayList<>();
        String query = "SELECT * FROM SupportStaff";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName"); // Retrieve first name
                String lastName = rs.getString("lastName");   // Retrieve last name
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                staffMembers.add(new SupportStaffMember(id, firstName, lastName, username, email, password)); // Create SupportStaffMember with first and last name
            }
        }
        return staffMembers;
    }

    // Retrieve all tickets from the database
    public static List<Ticket> getAllTickets() throws SQLException {
    List<Ticket> tickets = new ArrayList<>();
    String query = "SELECT * FROM Tickets WHERE status = 'OPEN'"; // Only fetch OPEN tickets

    try (Statement stmt = DatabaseUtil.getConnection().createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            int id = rs.getInt("id");
            int customerId = rs.getInt("customerId");
            int agentId = rs.getInt("agentId");
            String topic = rs.getString("topic");
            String content = rs.getString("content");
            LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
            int priority = rs.getInt("priority");

            // Use PersonService to find Customer and SupportStaffMember
            PersonService<Customer> customerService = new PersonService<>(Customer.class);
            PersonService<SupportStaffMember> agentService = new PersonService<>(SupportStaffMember.class);

            Customer customer = customerService.getPersonById(customerId); // Get Customer by ID
            SupportStaffMember assignedAgent = agentService.getPersonById(agentId); // Get Agent by ID

            // Create and add the Ticket object to the list
            Ticket ticket = new Ticket(id, customer, assignedAgent, topic, content, createdAt, priority);
            tickets.add(ticket);
        }
    }
    return tickets;
}
    
    public static int getMaxTicketId() throws SQLException {
        String query = "SELECT MAX(id) FROM Tickets";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1); // Returns the maximum ID, or null if no tickets exist
            }
        }
        return 0; // Return 0 if no tickets exist
    }
    
    public static void insertMessage(Message message) throws SQLException {
    // Validate input
    if (message.getSenderType() == null || message.getSenderType().isEmpty()) {
        throw new SQLException("Sender type cannot be null or empty.");
    }
    if (message.getSenderName() == null || message.getSenderName().isEmpty()) {
        throw new SQLException("Sender name cannot be null or empty.");
    }
    if (message.getContent() == null || message.getContent().isEmpty()) {
        throw new SQLException("Message content cannot be null or empty.");
    }

    String insertMessageSQL = "INSERT INTO Messages (ticket_id, sender_type, sender_name, content, timestamp) VALUES (?, ?, ?, ?, ?)";

    // Using try-with-resources for connection and prepared statement
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(insertMessageSQL, Statement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setInt(1, message.getTicketId());
        pstmt.setString(2, message.getSenderType());
        pstmt.setString(3, message.getSenderName());
        pstmt.setString(4, message.getContent());
        pstmt.setTimestamp(5, Timestamp.valueOf(message.getTimestamp()));

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Inserting message failed, no rows affected.");
        }

        // Retrieve the generated message ID
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int messageId = generatedKeys.getInt(1);
                message.setId(messageId);
                System.out.println("Message inserted successfully.");
            } else {
                throw new SQLException("Inserting message failed, no ID obtained.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error executing insertMessage: " + e.getMessage());
        throw new SQLException("Failed to insert message into the database.", e);
    }
}
    
    public static boolean ticketExists(int ticketId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Tickets WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if the ticket exists
            }
        }
        return false; // Return false if the ticket does not exist
    }


// Retrieve all messages for a specific ticket
public static List<Message> getMessagesForTicket(int ticketId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Messages WHERE ticket_id = ? ORDER BY timestamp ASC";
        establishConnection();  // Ensure the connection is open
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String senderType = rs.getString("sender_type");
                    String senderName = rs.getString("sender_name");
                    String content = rs.getString("content");
                    LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    messages.add(new Message(id, ticketId, senderType, senderName, content, timestamp));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing getMessagesForTicket: " + e.getMessage());
            throw new SQLException("Error retrieving messages for ticket ID: " + ticketId, e);
        }
        return messages;
    }
public static void clearTable(String tableName) {
    try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("DELETE FROM " + tableName);
        stmt.executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}