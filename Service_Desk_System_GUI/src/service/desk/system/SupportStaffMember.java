/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.desk.system;
import java.util.regex.Pattern;


/**
 *
 * @author rayyanabzal
 */

/*
 * Represents a support staff member in the Service Desk System.
 * Inherits from Person and adds a username and password for authentication.
 */
public class SupportStaffMember extends Person implements Authenticated {
    private String username; // Username for the support staff
    private String password; // Holds the hashed password

    public SupportStaffMember(int id, String username, String email, String password) {
        super(id, null, email); // Set name to null
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
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