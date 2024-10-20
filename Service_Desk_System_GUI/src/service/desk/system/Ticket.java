/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service.desk.system;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rayyanabzal
 */
/**
 * Represents a support ticket in the Service Desk System.
 * Each ticket is linked to a customer and a support staff member, and tracks the issue and its status.
 */
public class Ticket {
    public enum Status {
        OPEN, CLOSED
    }

    private int id; // Unique identifier for the ticket
    private Customer customer; // Customer who created the ticket
    private SupportStaffMember assignedAgent; // Agent assigned to handle the ticket
    private String topic; // Topic of the ticket
    private String content; // Detailed description of the issue
    private LocalDateTime createdAt; // Time when the ticket was created
    private int priority; // Priority level: 1 (low), 2 (medium), 3 (high)
    private List<Message> messages = new ArrayList<>(); // List of messages associated with the ticket
    private Status status; // Status of the ticket (OPEN or CLOSED)

    // Constructor with all fields
    public Ticket(int id, Customer customer, SupportStaffMember assignedAgent, String topic, String content, LocalDateTime createdAt, int priority) {
        this.id = id;
        this.customer = customer;
        this.assignedAgent = assignedAgent;
        this.topic = topic;
        this.content = content;
        this.createdAt = createdAt;
        this.priority = priority;
        this.status = Status.OPEN; // Default status
    }

    // Getters and Setters

    /*
     * Retrieves the unique identifier of the ticket.
     */
    public int getId() {
        return id;
    }

    /*
     * Retrieves the customer who created the ticket.
     */
    public Customer getCustomer() {
        return customer;
    }

    /*
     * Retrieves the agent assigned to the ticket.
     */
    public SupportStaffMember getAssignedAgent() {
        return assignedAgent;
    }

    /*
     * Sets the agent assigned to the ticket.
     */
    public void setAssignedAgent(SupportStaffMember assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    /*
     * Retrieves the topic of the ticket.
     */
    public String getTopic() {
        return topic;
    }

    /*
     * Retrieves the content of the ticket.
     */
    public String getContent() {
        return content;
    }

    /*
     * Retrieves the creation time of the ticket.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /*
     * Retrieves the priority level of the ticket.
     */
    public int getPriority() {
        return priority;
    }

    /*
     * Retrieves the list of messages associated with the ticket.
     */
    public List<Message> getMessages() {
        return messages;
    }

    /*
     * Sets the status of the ticket.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /*
     * Retrieves the current status of the ticket.
     */
    public Status getStatus() {
        return status;
    }

    /*
     * Adds a message to the list of messages associated with the ticket.
     */
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    /*
     * Sets the priority of the ticket.
     * Priority must be between 1 and 3.
     */
    public void setPriority(int priority) {
        if (priority < 1 || priority > 3) {
            throw new IllegalArgumentException("Priority must be between 1 and 3.");
        }
        this.priority = priority;
    }

    /*
     * Returns a string representation of the ticket, including all relevant details.
     */
    @Override
    public String toString() {
        return "Ticket ID: " + id + "\n" +
               "Topic: " + topic + "\n" +
               "Content: " + content + "\n" +
               "Priority: " + (priority == 1 ? "Low" : priority == 2 ? "Medium" : "High") + "\n" +
               "Status: " + status + "\n" +
               "Created At: " + getCreatedAt() + "\n" +
               "Assigned Agent: " + (assignedAgent != null ? assignedAgent.getName() : "None") + "\n" +
               "Customer: " + customer.getName() + "\n" +
               "Messages: \n" + getMessagesString();
    }

    /*
     * Converts the list of messages into a formatted string.
     */
    private String getMessagesString() {
        StringBuilder sb = new StringBuilder();
        for (Message message : messages) {
            sb.append(message.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}