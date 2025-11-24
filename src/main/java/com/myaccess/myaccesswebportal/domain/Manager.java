package com.myaccess.myaccesswebportal.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANAGER")
public class Manager extends User {

    protected Manager() {
        super();
    }

    public Manager(String email, String passwordHash) {
        super(email, passwordHash);
    }
}