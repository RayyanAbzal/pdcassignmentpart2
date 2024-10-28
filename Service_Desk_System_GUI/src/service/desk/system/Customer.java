/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.desk.system;

/**
 *
 * @author rayyanabzal
 */
/**
 * This class represents a customer in the Service Desk System.
 * It extends Person and adds a password for authentication purposes.
 */
public class Customer extends Person {
    private String password; // Holds the hashed password for the customer

    // Updated constructor to include first name, last name, and email
    public Customer(int id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email); // Call to Person constructor with first name and last name
        this.password = password; // Set the hashed password
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // Update the hashed password
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + getFirstName() + " " + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}