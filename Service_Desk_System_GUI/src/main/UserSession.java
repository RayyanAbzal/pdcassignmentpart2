/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author rayyanabzal
 */

/*
 * Singleton class to manage the session information of the currently logged-in user.
 * Stores details: user's role, email, name, username, and user ID, and provides 
 * a single instance for session management across the application.
 */
public class UserSession {
    private static UserSession instance;
    private String role;
    private String email;
    private String name;
    private String username;
    private int id;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Returns the single instance of UserSession, creating it if it doesn't exist
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Sets the user's information for the session
    public void setUserInfo(String role, String email, String name, String username, int id) {
        this.role = role;
        this.email = email;
        this.name = name;
        this.username = username;
        this.id = id;
    }

    // Getters for session details
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getId() { return id; }

    // Clears the current session data by setting the instance to null
    public void clearSession() {
        instance = null;
    }
}