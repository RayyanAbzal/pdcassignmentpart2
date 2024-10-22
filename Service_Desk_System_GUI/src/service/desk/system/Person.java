/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package service.desk.system;

/**
 *
 * @author rayyanabzal
 */

/*
 * Abstract class for a person, including basic details like ID, name, and email.
 * This class is used as a base for specific types like Customer or SupportStaffMember.
 */
public abstract class Person {
    private int id; // Unique ID for each person
    private String firstName; // The person's first name
    private String lastName; // The person's last name
    private String email; // The person's email address

    /**
     * Constructor to create a person with an ID, first name, last name, and email.
     */
    public Person(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Gets the ID of the person.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets a new ID for the person.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the person.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets a new name for the person.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the person.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets a new email address for the person.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string that summarizes the person's details: ID, name, and email.
     */
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}