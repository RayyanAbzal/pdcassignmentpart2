/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.regex.Pattern;

/**
 *
 * @author rayyanabzal
 */
public class ValidationUtil {
    // Basic email format/pattern 
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Validates if the email matches the expected pattern
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    // Checks if the password meets length, uppercase, lowercase, number, and special character requirements
    public static String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return "Password must contain at least one special character (!@#$%^&*).";
        }
        return null; // Password is valid
    }
}