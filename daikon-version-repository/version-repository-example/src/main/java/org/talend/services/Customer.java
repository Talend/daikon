package org.talend.services;

import java.util.UUID;

import org.talend.daikon.version.api.Identifiable;

public class Customer implements Identifiable {

    private String id = UUID.randomUUID().toString();

    private String firstname;

    private String lastname;

    public Customer() {
        // Only there for de/serialization
    }

    public Customer(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

}
