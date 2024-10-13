/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.desk.system;

import java.time.LocalDateTime;


/**
 *
 * @author rayyanabzal
 */

/*
 * Represents a message in the service desk system.
 * A message includes details about the sender, content, and when it was sent.
 */
public class Message {
    private String senderType; // Indicates if the sender is "Customer" or "Agent"
    private String senderName; // Name of the sender
    private String content;    // Content of the message
    private LocalDateTime timestamp; // Time when the message was sent

    /*
     * Constructs a new Message with the given details.
     */
    public Message(String senderType, String senderName, String content, LocalDateTime timestamp) {
        this.senderType = senderType;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    /*
     * Retrieves the type of the sender.
     */
    public String getSenderType() {
        return senderType;
    }

    /*
     * Retrieves the name of the sender.
     */
    public String getSenderName() {
        return senderName;
    }

    /*
     * Retrieves the content of the message.
     */
    public String getContent() {
        return content;
    }

    /*
     * Retrieves the timestamp of when the message was sent.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /*
     * Returns a string representation of the message, including the timestamp, sender type, sender name, and content.
     */
    @Override
    public String toString() {
        return "[" + timestamp + "] " + senderType + " (" + senderName + "): " + content;
    }
}