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
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;

/**
 *
 * @author rayyanabzal
 */
/**
 * Utility class to handle database connections and operations for the Service Desk System.
 */
public class DatabaseUtil {
    // Database connection parameters
    private static final String USER_NAME = "pdc"; // your DB username
    private static final String PASSWORD = "pdc"; // your DB password
    private static final String DB_URL = "jdbc:derby://localhost:1527/servicedesksystem"; // URL of the DB host
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

    // Establish a connection to the database
    private static void establishConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
                connection.setAutoCommit(true); // Enable auto-commit
                System.out.println("Database connection established successfully with auto-commit enabled.");
            } catch (SQLException ex) {
                System.out.println("Connection failed: " + ex.getMessage());
            }
        }
    }

    // Get the database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            establishConnection();
        }
        return connection;
    }

    // Close the database connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
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
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                        "name VARCHAR(100)," +
                        "email VARCHAR(100) UNIQUE," +
                        "password VARCHAR(100))";
                stmt.executeUpdate(createCustomersTable);
                System.out.println("Customers table created successfully.");
            }

            // Check and create SupportStaff table if it doesn't exist
            if (!tableExists("SupportStaff")) {
                String createSupportStaffTable = "CREATE TABLE SupportStaff (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                        "username VARCHAR(50)," +
                        "email VARCHAR(100) UNIQUE," +
                        "password VARCHAR(100))";
                stmt.executeUpdate(createSupportStaffTable);
                System.out.println("SupportStaff table created successfully.");
            }

            // Check and create Tickets table if it doesn't exist
            if (!tableExists("Tickets")) {
                String createTicketsTable = "CREATE TABLE Tickets (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
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
        String insertCustomerSQL = "INSERT INTO Customers (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPassword());  // No hashing, plain password saved
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
        String query = "INSERT INTO SupportStaff (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, staff.getUsername());
            pstmt.setString(2, staff.getEmail());
            pstmt.setString(3, staff.getPassword());  // No hashing, plain password saved
            pstmt.executeUpdate();
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
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                customers.add(new Customer(id, name, email, password));
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
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                staffMembers.add(new SupportStaffMember(id, username, email, password));
            }
        }
        return staffMembers;
    }

    // Retrieve all tickets from the database
    public static List<Ticket> getAllTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int customerId = rs.getInt("customerId");
                int agentId = rs.getInt("agentId");
                String topic = rs.getString("topic");
                String content = rs.getString("content");
                LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                int priority = rs.getInt("priority");
                String status = rs.getString("status");
               // tickets.add(new Ticket(id, customerId, agentId, topic, content, createdAt, priority, Ticket.Status.valueOf(status)));
            }
        }
        return tickets;
    }
}