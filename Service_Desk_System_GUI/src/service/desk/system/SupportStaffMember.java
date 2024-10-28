/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.desk.system;


/**
 *
 * @author rayyanabzal
 */

/*
 * Represents a support staff member in the Service Desk System.
 * Inherits from Person and adds a username and password for authentication.
 */
public class SupportStaffMember extends Person {
    private String username; // Username for the support staff
    private String password; // Holds the hashed password

    // Updated constructor to include first name and last name
    public SupportStaffMember(int id, String firstName, String lastName, String username, String email, String password) {
        super(id, firstName, lastName, email); // Call to Person constructor with first name and last name
        this.username = username;
        this.password = password; // Consider hashing the password before storing
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // Was going to hash password but ran into too many problems and wasn't required
    }

    @Override
    public String toString() {
        return "SupportStaffMember{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}