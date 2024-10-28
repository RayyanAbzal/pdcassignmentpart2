/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import service.desk.system.SupportStaffMember;
import service.desk.system.Customer;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rayyanabzal
 */

/*
 * A service class for managing a list of persons of type T.
 * Handles adding persons, retrieving all persons, and finding a person by ID or email.
 */
public class PersonService<T> {
    private final Class<T> type;
    private Connection connection;

    // Initializes PersonService with the specified type and establishes a database connection
    public PersonService(Class<T> type) {
        this.type = type;
        try {
            this.connection = DatabaseUtil.getConnection(); // Initialize database connection
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    // Adds a person to the database by checking their type and using the appropriate insert method
    public void addPerson(T person) {
        String insertSQL = getInsertSQL();
        if (insertSQL == null) {
            return; // Invalid type, nothing to insert
        }

        try {
            if (person instanceof Customer) {
                DatabaseUtil.insertCustomer((Customer) person); // Insert customer
            } else if (person instanceof SupportStaffMember) {
                DatabaseUtil.insertSupportStaff((SupportStaffMember) person); // Insert support staff
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieves all persons of type T from the database
    public List<T> getAllPersons() {
        List<T> persons = new ArrayList<>();
        String querySQL = getSelectSQL();
        if (querySQL == null) {
            return persons; // Invalid type, no data to retrieve
        }

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(querySQL)) {
            while (rs.next()) {
                persons.add(createPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }

    // Retrieves all support staff from the database
    public List<SupportStaffMember> getAllSupportStaff() {
        List<SupportStaffMember> staffList = new ArrayList<>();
        String querySQL = "SELECT * FROM SupportStaff";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");

                staffList.add(new SupportStaffMember(id, firstName, lastName, username, email, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }

    // Retrieves a person by their unique ID
    public T getPersonById(int id) {
        String querySQL = getSelectSQL() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPersonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Finds a person by their email address
    public T findPersonByEmail(String email) {
        String querySQL = getSelectSQL() + " WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(querySQL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPersonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Finds a support staff member by their username
    public T findPersonByUsername(String username) {
        if (!type.equals(SupportStaffMember.class)) {
            return null; // Only valid for SupportStaffMember
        }

        String querySQL = "SELECT id, firstName, lastName, username, email, password FROM supportstaff WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(querySQL)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPersonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Checks if an email already exists in the database
    public boolean emailExists(String email) {
        return exists("email", email);
    }

    // Checks if a username already exists in the database
    public boolean usernameExists(String username) {
        return exists("username", username);
    }

    // Helper method to check if a specific column value exists in the table
    private boolean exists(String columnName, String value) {
        String querySQL = String.format("SELECT 1 FROM %s WHERE %s = ?", getTableName(), columnName);
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Returns the SQL statement for inserting data based on the type
    private String getInsertSQL() {
        if (type.equals(Customer.class)) {
            return "INSERT INTO customers (firstName, lastName, email, password) VALUES (?, ?, ?, ?)";
        } else if (type.equals(SupportStaffMember.class)) {
            return "INSERT INTO supportstaff (firstName, lastName, username, email, password) VALUES (?, ?, ?, ?, ?)";
        }
        return null;
    }

    // Returns the SQL statement for selecting data based on the type
    private String getSelectSQL() {
        if (type.equals(Customer.class)) {
            return "SELECT id, firstName, lastName, email, password FROM customers";
        } else if (type.equals(SupportStaffMember.class)) {
            return "SELECT id, firstName, lastName, username, email, password FROM supportstaff";
        }
        return null;
    }

    // Creates a person object from a ResultSet based on the type
    private T createPersonFromResultSet(ResultSet rs) throws SQLException {
        if (type.equals(Customer.class)) {
            return type.cast(new Customer(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("password")
            ));
        } else if (type.equals(SupportStaffMember.class)) {
            return type.cast(new SupportStaffMember(
                    rs.getInt("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
            ));
        }
        return null;
    }

    // Returns the name of the table based on the type
    private String getTableName() {
        if (type.equals(Customer.class)) {
            return "customers";
        } else if (type.equals(SupportStaffMember.class)) {
            return "supportstaff";
        }
        return null;
    }
    
    // Finds a customer by their ID
    public Customer findCustomerById(int id) {
        return (Customer) getPersonById(id);
    }

    // Finds a support staff member by their ID
    public SupportStaffMember findAgentById(int id) {
        return (SupportStaffMember) getPersonById(id);
    }
}