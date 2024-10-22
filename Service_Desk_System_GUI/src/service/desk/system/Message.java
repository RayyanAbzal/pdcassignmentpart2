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
    private int id;
    private int ticketId;
    private String senderType;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;

    public Message(int id, int ticketId, String senderType, String senderName, String content, LocalDateTime timestamp) {
        this.id = id;
        this.ticketId = ticketId;
        this.senderType = senderType;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}