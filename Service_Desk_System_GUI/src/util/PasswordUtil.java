/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String PASSWORD_PATTERN = 
        "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /*
     * Hashes a plain password using SHA-256.
     * returns the hashed password as a hexadecimal string.
     */
    
    //Got help from chatgpt for this method
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Verifies if the plain password matches the hashed password.
     */
    public static boolean verifyPassword(String hashedPassword, String plainPassword) {
        return hashedPassword.equals(hashPassword(plainPassword));
    }

    /*
     * Validates the strength of a password based on predefined rules.
     */
    public static String validatePassword(String password) {
        Matcher matcher = pattern.matcher(password);
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        } else if (!matcher.find()) {
            if (!password.matches(".*[A-Z].*")) {
                return "Password must contain at least one uppercase letter.";
            }
            if (!password.matches(".*\\d.*")) {
                return "Password must contain at least one number.";
            }
            if (!password.matches(".*[@#$%^&+=!].*")) {
                return "Password must contain at least one special character.";
            }
        }
        return null;
    }
}