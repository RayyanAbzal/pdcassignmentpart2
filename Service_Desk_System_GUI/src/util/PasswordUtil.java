    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package util;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;

    /**
     *
     * @author rayyanabzal
     */

    /*
     * Utility class for handling password operations.
     * Provides methods to hash passwords, verify hashed passwords,
     * and validate password strength based on specific rules.
     */
public class PasswordUtil {

    // Method to validate password based on criteria (e.g., length, special characters)
    public static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false; // Password must be at least 8 characters long
        }
        if (!password.matches(".*[A-Z].*")) {
            return false; // Password must contain at least one uppercase letter
        }
        if (!password.matches(".*[a-z].*")) {
            return false; // Password must contain at least one lowercase letter
        }
        if (!password.matches(".*\\d.*")) {
            return false; // Password must contain at least one number
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return false; // Password must contain at least one special character (!@#$%^&*)
        }
        return true; // Valid password
    }
}