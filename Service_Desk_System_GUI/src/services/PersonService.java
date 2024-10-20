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

    public PersonService(Class<T> type) {
        this.type = type;
        try {
            this.connection = DatabaseUtil.getConnection(); // Initialize database connection
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public void addPerson(T person) {
        String insertSQL = getInsertSQL();
        if (insertSQL == null) {
            return; // Invalid type, nothing to insert
        }

        try {
            if (person instanceof Customer) {
                // Insert the customer into the database
                DatabaseUtil.insertCustomer((Customer) person);
            } else if (person instanceof SupportStaffMember) {
                // Insert the support staff into the database
                DatabaseUtil.insertSupportStaff((SupportStaffMember) person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public List<SupportStaffMember> getAllSupportStaff() {
        List<SupportStaffMember> supportStaff = new ArrayList<>();
        String querySQL = "SELECT id, username, email, password FROM supportstaff"; // Modify based on your table structure
        
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(querySQL)) {
            while (rs.next()) {
                // Assuming createPersonFromResultSet can handle this type
                supportStaff.add((SupportStaffMember) createPersonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supportStaff;
    }

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

    public T findPersonByEmail(String email) {
        String querySQL = getSelectSQL() + " WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
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

    public T findPersonByUsername(String username) {
        if (!type.equals(SupportStaffMember.class)) {
            return null; // Only valid for SupportStaffMember
        }

        String querySQL = "SELECT id, username, email, password FROM supportstaff WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
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

    public boolean emailExists(String email) {
        return exists("email", email);
    }

    public boolean usernameExists(String username) {
        return exists("username", username);
    }

    private boolean exists(String columnName, String value) {
        String querySQL = String.format("SELECT 1 FROM %s WHERE %s = ?", getTableName(), columnName);
        try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If there's a result, the value exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getInsertSQL() {
        if (type.equals(Customer.class)) {
            // Remove ID from the insert statement
            return "INSERT INTO customers (name, email, password) VALUES (?, ?, ?)";
        } else if (type.equals(SupportStaffMember.class)) {
            // Remove ID from the insert statement
            return "INSERT INTO supportstaff (username, email, password) VALUES (?, ?, ?)";
        }
        return null; // Invalid type
    }

    private String getSelectSQL() {
        if (type.equals(Customer.class)) {
            return "SELECT id, name, email, password FROM customers";
        } else if (type.equals(SupportStaffMember.class)) {
            return "SELECT id, username, email, password FROM supportstaff";
        }
        return null; // Invalid type
    }

    private void setPersonParameters(PreparedStatement stmt, T person) throws SQLException {
        if (person instanceof Customer) {
            Customer customer = (Customer) person;
            // No need to set the ID here as it is auto-generated
            stmt.setString(1, customer.getName()); // Set name
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
        } else if (person instanceof SupportStaffMember) {
            SupportStaffMember staff = (SupportStaffMember) person;
            // No need to set the ID here as it is auto-generated
            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getEmail());
            stmt.setString(3, staff.getPassword());
        }
    }

    private T createPersonFromResultSet(ResultSet rs) throws SQLException {
        if (type.equals(Customer.class)) {
            return type.cast(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
            ));
        } else if (type.equals(SupportStaffMember.class)) {
            return type.cast(new SupportStaffMember(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password")
            ));
        }
        return null; // Invalid type
    }

    private String getTableName() {
        if (type.equals(Customer.class)) {
            return "customers";
        } else if (type.equals(SupportStaffMember.class)) {
            return "supportstaff";
        }
        return null; // Invalid type
    }
    
    public Customer findCustomerById(int id) {
        return (Customer) getPersonById(id);
    }

    public SupportStaffMember findAgentById(int id) {
        return (SupportStaffMember) getPersonById(id);
    }
}