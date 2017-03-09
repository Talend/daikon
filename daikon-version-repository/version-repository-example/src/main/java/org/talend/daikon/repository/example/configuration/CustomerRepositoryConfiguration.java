package org.talend.daikon.repository.example.configuration;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;
import static org.talend.daikon.version.VersionedRepositoryConfiguration.versionedItem;

import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.talend.daikon.version.api.DataEvent;
import org.talend.daikon.version.api.Journal;
import org.talend.daikon.version.api.VersionedRepository;
import org.talend.services.Customer;

@Configuration
public class CustomerRepositoryConfiguration {

    @Bean
    public Journal journal(CrudRepository<DataEvent, String> eventStorage) {
        return new Journal() {

            @Override
            public void append(DataEvent dataEvent) {
                eventStorage.save(dataEvent);
            }

            @Override
            public Stream<DataEvent> log(String id) {
                return createStreamFromIterator(eventStorage.findAll().iterator()).filter(e -> id.equals(e.getResourceId()));
            }
        };
    }

    @Bean
    public VersionedRepository<Customer> versionedRepository(Journal journal, CrudRepository<Customer, String> storage) {
        return versionedItem(Customer.class) //
                .journal(journal) //
                .storage(storage) //
                .build();
    }

}
