/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package main;

import main.AgentRegistrationHandler;
import main.CustomerRegistrationHandler;
import main.UserSession;
import org.junit.Before;
import org.junit.Test;
import services.PersonService;
import service.desk.system.Customer;
import service.desk.system.SupportStaffMember;
import static org.junit.Assert.*;
import util.DatabaseUtil;

/**
 *
 * @author rayyanabzal
 */
public class RegistrationHandlerTests {

    private PersonService<Customer> customerService;
    private PersonService<SupportStaffMember> agentService;
    private AgentRegistrationHandler agentRegistrationHandler;
    private CustomerRegistrationHandler customerRegistrationHandler;

    @Before
    public void setUp() {
        DatabaseUtil.clearTable("CUSTOMERS");
        DatabaseUtil.clearTable("SUPPORTSTAFF");
        customerService = new PersonService<>(Customer.class);
        agentService = new PersonService<>(SupportStaffMember.class);
        agentRegistrationHandler = new AgentRegistrationHandler(agentService, message -> System.out.println(message));
        customerRegistrationHandler = new CustomerRegistrationHandler(customerService, message -> System.out.println(message));
    }

    @Test
    public void testAgentRegistrationSuccessful() {
        SupportStaffMember agent = new SupportStaffMember(0, "Ethan", "Walker", "ethan_" + System.currentTimeMillis(), "ethan.walker" + System.currentTimeMillis() + "@example.com", "password123");
        agentService.addPerson(agent);
        assertNotNull(agentService.findPersonByUsername(agent.getUsername()));
    }

    @Test
    public void testCustomerRegistrationFailed() {
        Customer customer = new Customer(0, "Olivia", "Brown", "olivia.brown" + System.currentTimeMillis() + "@example.com", "password123");
        customerService.addPerson(customer);
        assertNull(customerService.findPersonByUsername("oliviaB"));
    }

    @Test
    public void testUserSessionStoresCorrectInfo() {
        Customer customer = new Customer(0, "Liam", "Johnson", "liam.johnson" + System.currentTimeMillis() + "@example.com", "strongPass123");
        customerService.addPerson(customer);
        UserSession session = UserSession.getInstance();
        session.setUserInfo("Customer", customer.getEmail(), "Liam Johnson", "liamJ", customer.getId());
        assertEquals("Customer", session.getRole());
        assertEquals("Liam Johnson", session.getName());
        assertEquals(customer.getEmail(), session.getEmail());
        assertEquals("liamJ", session.getUsername());
    }

    @Test
    public void testAgentLoginSuccessful() {
        SupportStaffMember agent = new SupportStaffMember(0, "Noah", "Williams", "noah_" + System.currentTimeMillis(), "noah.williams" + System.currentTimeMillis() + "@example.com", "password123");
        agentService.addPerson(agent);
        agentService = new PersonService<>(SupportStaffMember.class);
        boolean loginSuccessful = agentService.findPersonByUsername(agent.getUsername()) != null && agent.getPassword().equals("password123");
        assertTrue(loginSuccessful);
    }

    @Test
    public void testCustomerLoginSuccessful() {
        Customer customer = new Customer(0, "Emma", "Davis", "emma.davis" + System.currentTimeMillis() + "@example.com", "password123");
        customerService.addPerson(customer);
        customerService = new PersonService<>(Customer.class);
        boolean loginSuccessful = customerService.findPersonByEmail(customer.getEmail()) != null && customer.getPassword().equals("password123");
        assertTrue(loginSuccessful);
    }
}