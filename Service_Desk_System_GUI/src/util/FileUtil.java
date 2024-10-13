/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import service.desk.system.Message;

/**
 *
 * @author rayyanabzal
 */

/*
 * Utility class for reading and writing data to files.
 * Handles customers, support agents, tickets, and messages.
 */
public class FileUtil {

    /*
     * Reads customer data from a file.
     */
    public static List<Customer> readCustomersFromFile(String filename) {
        return readFile(filename, parts ->
            new Customer(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3])
        );
    }

    /*
     * Reads support agents from a file.
     */
    public static List<SupportStaffMember> readSupportAgentsFromFile(String filePath) {
        List<SupportStaffMember> agents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String username = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    agents.add(new SupportStaffMember(id, username, email, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return agents;
    }

    /*
     * Reads tickets from a file and associates them with customers and agents.
     */
    public static List<Ticket> readTicketsFromFile(String filePath, List<Customer> customers, List<SupportStaffMember> agents) {
        List<Ticket> tickets = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Ticket currentTicket = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TicketID:")) {
                    int id = Integer.parseInt(line.split(":")[1].trim());
                    String customerEmail = br.readLine().trim().split(":")[1].trim();
                    Customer customer = customers.stream().filter(c -> c.getEmail().equals(customerEmail)).findFirst().orElse(null);
                    String agentUsername = br.readLine().trim().split(":")[1].trim();
                    SupportStaffMember agent = agents.stream().filter(a -> a.getUsername().equals(agentUsername)).findFirst().orElse(null);
                    String topic = br.readLine().trim().split(":")[1].trim();
                    String issue = br.readLine().trim().split(":")[1].trim();
                    String createdAtString = br.readLine().trim().split(":")[1].trim();
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtString);
                    int priority = Integer.parseInt(br.readLine().trim().split(":")[1].trim());
                    Ticket.Status status = Ticket.Status.valueOf(br.readLine().trim().split(":")[1].trim().toUpperCase());
                    currentTicket = new Ticket(id, customer, agent, topic, issue, createdAt, priority);
                    currentTicket.setStatus(status);
                    tickets.add(currentTicket);
                } else if (line.equals("Message:") && currentTicket != null) {
                    String senderType = br.readLine().trim().split(":")[1].trim();
                    String senderName = br.readLine().trim().split(":")[1].trim();
                    String content = br.readLine().trim().split(":")[1].trim();
                    String timestampString = br.readLine().trim().split(":")[1].trim();
                    LocalDateTime timestamp = LocalDateTime.parse(timestampString);
                    Message message = new Message(senderType, senderName, content, timestamp);
                    currentTicket.addMessage(message);
                } else if (line.equals("EndTicket") && currentTicket != null) {
                    currentTicket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            System.err.println("DateTimeParseException: " + e.getMessage());
        }
        return tickets;
    }

    /*
     * Writes customer data to a file.
     */
    public static void writeCustomersToFile(String filename, List<Customer> customers) {
        writeFile(filename, customers, customer ->
            String.format("%d,%s,%s,%s%n", customer.getId(), customer.getName(), customer.getEmail(), customer.getPassword())
        );
    }

    /*
     * Writes support agents to a file.
     */
    public static void writeSupportAgentsToFile(String filePath, List<SupportStaffMember> agents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (SupportStaffMember agent : agents) {
                writer.write(agent.getId() + "," + agent.getUsername() + "," + agent.getEmail() + "," + agent.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Appends ticket data to an open tickets file.
     */
    public static void appendTicketsToOpenFile(String filePath, List<Ticket> tickets) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            for (Ticket ticket : tickets) {
                bw.write("TicketID: " + ticket.getId());
                bw.newLine();
                bw.write("CustomerEmail: " + ticket.getCustomer().getEmail());
                bw.newLine();
                bw.write("AgentUsername: " + (ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getUsername() : "Unassigned"));
                bw.newLine();
                bw.write("Topic: " + ticket.getTopic());
                bw.newLine();
                bw.write("Issue: " + ticket.getContent());
                bw.newLine();
                bw.write("CreatedAt: " + ticket.getCreatedAt().toString());
                bw.newLine();
                bw.write("Priority: " + ticket.getPriority());
                bw.newLine();
                bw.write("Status: " + ticket.getStatus());
                bw.newLine();
                for (Message msg : ticket.getMessages()) {
                    bw.write("Message:");
                    bw.newLine();
                    bw.write("SenderType: " + msg.getSenderType());
                    bw.newLine();
                    bw.write("SenderName: " + msg.getSenderName());
                    bw.newLine();
                    bw.write("Content: " + msg.getContent());
                    bw.newLine();
                    bw.write("Timestamp: " + msg.getTimestamp().toString());
                    bw.newLine();
                }
                bw.write("EndTicket");
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Appends ticket data to a closed tickets file.
     */
    public static void appendTicketsToClosedFile(String filePath, List<Ticket> tickets) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            for (Ticket ticket : tickets) {
                bw.write("TicketID: " + ticket.getId());
                bw.newLine();
                bw.write("CustomerEmail: " + ticket.getCustomer().getEmail());
                bw.newLine();
                bw.write("AgentUsername: " + (ticket.getAssignedAgent() != null ? ticket.getAssignedAgent().getUsername() : "Unassigned"));
                bw.newLine();
                bw.write("Topic: " + ticket.getTopic());
                bw.newLine();
                bw.write("Issue: " + ticket.getContent());
                bw.newLine();
                bw.write("CreatedAt: " + ticket.getCreatedAt().toString());
                bw.newLine();
                bw.write("Priority: " + ticket.getPriority());
                bw.newLine();
                bw.write("Status: " + ticket.getStatus());
                bw.newLine();
                for (Message msg : ticket.getMessages()) {
                    bw.write("Message:");
                    bw.newLine();
                    bw.write("SenderType: " + msg.getSenderType());
                    bw.newLine();
                    bw.write("SenderName: " + msg.getSenderName());
                    bw.newLine();
                    bw.write("Content: " + msg.getContent());
                    bw.newLine();
                    bw.write("Timestamp: " + msg.getTimestamp().toString());
                    bw.newLine();
                }
                bw.write("EndTicket");
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Appends a customer to a file.
     */
    public static void appendCustomerToFile(String filename, Customer customer) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(customer.getId() + "," + customer.getName() + "," + customer.getEmail() + "," + customer.getPassword());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Appends a support agent to a file.
     */
    public static void appendAgentToFile(String filePath, SupportStaffMember agent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(agent.getId() + "," + agent.getUsername() + "," + agent.getEmail() + "," + agent.getPassword());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Reads data from a file and maps it to a list of objects.
     */
    private static <T> List<T> readFile(String filename, Mapper<T> mapper) {
        List<T> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    result.add(mapper.map(parts));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * Writes a list of objects to a file.
     */
    private static <T> void writeFile(String filename, List<T> data, Formatter<T> formatter) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (T item : data) {
                writer.write(formatter.format(item));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface Mapper<T> {
        T map(String[] parts);
    }

    @FunctionalInterface
    private interface Formatter<T> {
        String format(T item);
    }
}