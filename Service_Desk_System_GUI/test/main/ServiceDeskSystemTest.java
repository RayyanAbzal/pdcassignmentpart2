/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package main;

import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import service.desk.system.Ticket;
import services.PersonService;
import services.TicketService;
import util.DatabaseUtil;


/**
 *
 * @author rayyanabzal
 */

/**
 * JUnit test class for testing various functionalities of the Service Desk System.
 */
public class ServiceDeskSystemTest {

    private PersonService<Customer> customerService;
    private PersonService<SupportStaffMember> agentService;
    private TicketService ticketService;

    // Clears tables and initializes services before each test
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

    // Tests customer registration and retrieval by email
    @Test
    public void testCustomerRegistrationAndRetrieval() {
        Customer customer = new Customer(0, "John", "Doe", "johndoe@example.com", "password123");
        customerService.addPerson(customer);

        Customer retrievedCustomer = customerService.findPersonByEmail("johndoe@example.com");
        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getFirstName());
    }

    // Tests agent registration and retrieval by username
    @Test
    public void testAgentRegistrationAndRetrieval() {
        SupportStaffMember agent = new SupportStaffMember(0, "Jane", "Doe", "jdoe", "janedoe@example.com", "password123");
        agentService.addPerson(agent);

        SupportStaffMember retrievedAgent = agentService.findPersonByUsername("jdoe");
        assertNotNull(retrievedAgent);
        assertEquals("Jane", retrievedAgent.getFirstName());
    }

    // Tests ticket creation and retrieval by ticket ID
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

        int newTicketId = DatabaseUtil.getMaxTicketId();
        ticket.setId(newTicketId);
        Ticket retrievedTicket = ticketService.getTicketById(newTicketId);

        assertNotNull(retrievedTicket);
        assertEquals("Issue Topic", retrievedTicket.getTopic());
    }

    // Tests updating the priority of a ticket
    @Test
    public void testUpdateTicketPriority() throws Exception {
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

        ticket.setPriority(1);
        DatabaseUtil.updateTicket(ticket);

        Ticket updatedTicket = ticketService.getTicketById(newTicketId);
        System.out.println("Priority retrieved from DB after update: " + updatedTicket.getPriority());

        assertNotNull(updatedTicket);
        assertEquals(1, updatedTicket.getPriority());
    }

    // Tests resolving a ticket by setting its status
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