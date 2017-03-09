package org.talend.daikon.repository.example.configuration;

import org.springframework.data.repository.CrudRepository;
import org.talend.services.Customer;

public interface CustomerStorage extends CrudRepository<Customer, String> {
}
