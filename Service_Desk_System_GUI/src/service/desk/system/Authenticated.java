/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service.desk.system;

/**
 *
 * @author rayyanabzal
 */

/*
 * Defines the methods required for authentication-related operations.
 * Any class implementing this interface must provide implementations for
 * getting and setting both the username and password.
 */
public interface Authenticated {
    
    /*
     * Retrieves the password for this authenticated entity.
     */
    String getPassword();
    
    /**
     * Sets a new password for this authenticated entity.
     */
    void setPassword(String password);
    
    /*
     * Retrieves the username for this authenticated entity.
     */
    String getUsername();
    
    /*
     * Sets a new username for this authenticated entity.
     */
    void setUsername(String username);
}
