/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author rayyanabzal
 */
public class UserSession {
    private static UserSession instance;
    private String role;
    private String email;
    private String name;
    private String username;
    private int id;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserInfo(String role, String email, String name, String username, int id) {
        this.role = role;
        this.email = email;
        this.name = name;
        this.username = username;
        this.id = id;
    }

    // Getters
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public int getId() { return id; }

    public void clearSession() {
        instance = null;
    }
}
