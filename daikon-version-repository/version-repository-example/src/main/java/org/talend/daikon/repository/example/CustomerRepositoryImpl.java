package org.talend.daikon.repository.example;

import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.version.service.RepositoryServiceImplementation;
import org.talend.services.Customer;
import org.talend.services.CustomerRepository;

@ServiceImplementation
public class CustomerRepositoryImpl extends RepositoryServiceImplementation<Customer> implements CustomerRepository {
}
