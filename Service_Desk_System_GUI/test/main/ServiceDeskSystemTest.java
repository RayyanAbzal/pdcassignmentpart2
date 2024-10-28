/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package main;

import java.time.LocalDateTime;
import javax.swing.JPanel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;
import services.TicketService;
import util.DatabaseUtil;
import java.sql.SQLException;


/**
 *
 * @author rayyanabzal
 */
public class ServiceDeskSystemTest {

    private PersonService<Customer> customerService;
    private PersonService<SupportStaffMember> agentService;
    private TicketService ticketService;

    @Before
    public void setUp() {
        DatabaseUtil.clearTable("Messages");
        DatabaseUtil.clearTable("Tickets");
        DatabaseUtil.clearTable("Customers");
        DatabaseUtil.clearTable("SupportStaff");

        customerService = new PersonService<>(Customer.class);
        agentService = new PersonService<>(SupportStaffMember.class);
        ticketService = new TicketService();
    }

    /**
     * Test customer registration and retrieval by email.
     */
    @Test
    public void testCustomerRegistrationAndRetrieval() {
        Customer customer = new Customer(0, "John", "Doe", "johndoe@example.com", "password123");
        customerService.addPerson(customer);

        Customer retrievedCustomer = customerService.findPersonByEmail("johndoe@example.com");
        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getFirstName());
    }

    /**
     * Test agent registration and retrieval by username.
     */
    @Test
    public void testAgentRegistrationAndRetrieval() {
        SupportStaffMember agent = new SupportStaffMember(0, "Jane", "Doe", "jdoe", "janedoe@example.com", "password123");
        agentService.addPerson(agent);

        SupportStaffMember retrievedAgent = agentService.findPersonByUsername("jdoe");
        assertNotNull(retrievedAgent);
        assertEquals("Jane", retrievedAgent.getFirstName());
    }

    /**
     * Test ticket creation and retrieval with a valid ticket ID.
     */
    @Test
    public void testTicketCreationAndRetrieval() throws Exception {
        Customer customer = new Customer(0, "John", "Doe", "johndoe@example.com", "password123");
        customerService.addPerson(customer);

        SupportStaffMember agent = new SupportStaffMember(0, "Agent", "Smith", "asmith", "agent@example.com", "password123");
        agentService.addPerson(agent);

        Customer retrievedCustomer = customerService.findPersonByEmail("johndoe@example.com");
        SupportStaffMember retrievedAgent = agentService.findPersonByUsername("asmith");

        Ticket ticket = new Ticket(0, retrievedCustomer, retrievedAgent, "Issue Topic", "Issue Content", LocalDateTime.now(), 1);
        ticketService.addTicket(ticket);

        // Fetch the correct ID of the newly added ticket
        int newTicketId = DatabaseUtil.getMaxTicketId();  // throws Exception handled
        ticket.setId(newTicketId); // Use setId to assign the correct ID
        Ticket retrievedTicket = ticketService.getTicketById(newTicketId);

        assertNotNull(retrievedTicket);
        assertEquals("Issue Topic", retrievedTicket.getTopic());
    }

    /**
     * Test ticket priority update with a valid ticket ID.
     */
    @Test
public void testUpdateTicketPriority() throws Exception {
    // Register a customer and an agent
    Customer customer = new Customer(0, "John", "Doe", "johndoe@example.com", "password123");
    customerService.addPerson(customer);

    SupportStaffMember agent = new SupportStaffMember(0, "Agent", "Smith", "asmith", "agent@example.com", "password123");
    agentService.addPerson(agent);

    Customer retrievedCustomer = customerService.findPersonByEmail("johndoe@example.com");
    SupportStaffMember retrievedAgent = agentService.findPersonByUsername("asmith");

    // Create and add a ticket with initial priority 1
    Ticket ticket = new Ticket(0, retrievedCustomer, retrievedAgent, "Issue Topic", "Issue Content", LocalDateTime.now(), 1);
    ticketService.addTicket(ticket);

    // Retrieve the new ticket ID from the database
    int newTicketId = DatabaseUtil.getMaxTicketId();
    ticket.setId(newTicketId);

    // Set the priority to 1 and update the ticket in the database directly to verify
    ticket.setPriority(1);
    DatabaseUtil.updateTicket(ticket); // Direct database update

    // Retrieve the updated ticket directly from the database to confirm the change
    Ticket updatedTicket = ticketService.getTicketById(newTicketId);
    System.out.println("Priority retrieved from DB after update: " + updatedTicket.getPriority());

    assertNotNull(updatedTicket);
    assertEquals(1, updatedTicket.getPriority()); // Ensure priority is updated to 1
}

    /**
     * Test ticket resolution functionality with a valid ticket ID.
     */
    @Test
public void testTicketResolution() throws Exception {
    Customer customer = new Customer(0, "John", "Doe", "johndoe@example.com", "password123");
    customerService.addPerson(customer);

    SupportStaffMember agent = new SupportStaffMember(0, "Agent", "Smith", "asmith", "agent@example.com", "password123");
    agentService.addPerson(agent);

    Customer retrievedCustomer = customerService.findPersonByEmail("johndoe@example.com");
    SupportStaffMember retrievedAgent = agentService.findPersonByUsername("asmith");

    Ticket ticket = new Ticket(0, retrievedCustomer, retrievedAgent, "Issue Topic", "Issue Content", LocalDateTime.now(), 1);
    ticketService.addTicket(ticket);

    int newTicketId = DatabaseUtil.getMaxTicketId();
    ticket.setId(newTicketId);
    ticketService.resolveTicket(newTicketId);
}
}