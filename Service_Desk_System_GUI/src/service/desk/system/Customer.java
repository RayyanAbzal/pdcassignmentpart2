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
public class Customer extends Person implements Authenticated {
    private String password; // Holds the hashed password for the customer

    public Customer(int id, String name, String email, String password) {
        super(id, name, email);
        this.password = password; // Set the hashed password
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password; // Update the hashed password
    }

    @Override
    public String getUsername() {
        // Customers don't use username, so we can return null or throw an UnsupportedOperationException
        return null; // Or throw new UnsupportedOperationException("Customer does not have a username.");
    }

    @Override
    public void setUsername(String username) {
        // Customers don't use username, so this method can be empty
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}