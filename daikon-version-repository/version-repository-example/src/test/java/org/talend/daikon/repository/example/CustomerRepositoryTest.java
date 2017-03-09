package org.talend.daikon.repository.example;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.Application;
import org.talend.daikon.annotation.Client;
import org.talend.daikon.version.api.DataEvent;
import org.talend.services.Customer;
import org.talend.services.CustomerRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerRepositoryTest {

    @Client
    private CustomerRepository repository;

    @Test
    public void testCreate() throws Exception {
        // Create
        final Customer create = new Customer("1234");
        create.setFirstname("Anonymous");
        create.setLastname("Anonymous");
        repository.create(create);

        // Update
        final Customer update = new Customer("1234");
        update.setFirstname("My first name");
        update.setLastname("My last name");
        repository.update(update);

        // Log
        final List<DataEvent> log = repository.log("1234");
        assertEquals(2, log.size());

        // Checkout
        final Customer initial = repository.checkout("1234", log.get(0).getId());
        assertEquals("Anonymous", initial.getFirstname());
        assertEquals("Anonymous", initial.getLastname());

        final Customer updated = repository.checkout("1234", log.get(1).getId());
        assertEquals("My first name", updated.getFirstname());
        assertEquals("My last name", updated.getLastname());

        // Reset
        repository.reset("1234", log.get(0).getId());
        final Customer reset = repository.get("1234");
        assertEquals("Anonymous", reset.getFirstname());
        assertEquals("Anonymous", reset.getLastname());

        final List<DataEvent> logAfterReset = repository.log("1234");
        assertEquals(3, logAfterReset.size());
    }
}
