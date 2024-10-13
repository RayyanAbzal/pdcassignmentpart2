/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import service.desk.system.SupportStaffMember;
import service.desk.system.Customer;

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
    private final List<T> persons = new ArrayList<>();
    private final Class<T> type;

    /*
     * Constructor for PersonService.
     * Initializes with the class type of T (e.g., Customer or SupportStaffMember).
     */
    public PersonService(Class<T> type) {
        this.type = type;
    }

    /**
     * Adds a person to the list of managed persons.
     */
    public void addPerson(T person) {
        persons.add(person);
    }

    /*
     * Retrieves all persons from the list.
     * A copy is returned to maintain encapsulation.
     */
    public List<T> getAllPersons() {
        return new ArrayList<>(persons); // Return a copy to ensure encapsulation
    }

    /*
     * Finds a person by their ID.
     * Checks whether the person is a Customer or SupportStaffMember to determine the correct ID.
     */
    public T getPersonById(int id) {
        return persons.stream()
            .filter(person -> {
                if (type.equals(Customer.class)) {
                    return ((Customer) person).getId() == id;
                } else if (type.equals(SupportStaffMember.class)) {
                    return ((SupportStaffMember) person).getId() == id;
                }
                return false;
            })
            .findFirst()
            .orElse(null);
    }

    /*
     * Finds a person by their email address.
     * Checks whether the person is a Customer or SupportStaffMember to determine the correct email.
     */
    public T findPersonByEmail(String email) {
        return persons.stream()
            .filter(person -> {
                if (type.equals(Customer.class)) {
                    return ((Customer) person).getEmail().equals(email);
                } else if (type.equals(SupportStaffMember.class)) {
                    return ((SupportStaffMember) person).getEmail().equals(email);
                }
                return false;
            })
            .findFirst()
            .orElse(null);
    }

    /*
     * Finds a person by their username.
     * This method is only applicable for SupportStaffMember.
     */
    public T findPersonByUsername(String username) {
        if (type.equals(SupportStaffMember.class)) {
            return persons.stream()
                .filter(person -> ((SupportStaffMember) person).getUsername().equals(username))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /*
     * Checks if an email address already exists in the list of persons.
     */
    public boolean emailExists(String email) {
        return persons.stream()
            .anyMatch(person -> {
                if (type.equals(Customer.class)) {
                    return ((Customer) person).getEmail().equals(email);
                } else if (type.equals(SupportStaffMember.class)) {
                    return ((SupportStaffMember) person).getEmail().equals(email);
                }
                return false;
            });
    }

    /*
     * Checks if a username already exists in the list of persons.
     * This check is only relevant for SupportStaffMember.
     */
    public boolean usernameExists(String username) {
        return type.equals(SupportStaffMember.class) && persons.stream()
            .anyMatch(person -> ((SupportStaffMember) person).getUsername().equals(username));
    }
}