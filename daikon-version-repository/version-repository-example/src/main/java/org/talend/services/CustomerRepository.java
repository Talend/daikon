package org.talend.services;

import org.talend.daikon.annotation.Service;
import org.talend.daikon.version.service.RepositoryService;

@Service(name = "CustomerRepository")
public interface CustomerRepository extends RepositoryService<Customer> {
}
