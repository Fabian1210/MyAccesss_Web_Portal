package com.myaccess.myaccesswebportal.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {

    protected Employee() {
        super();
    }

    public Employee(String email, String passwordHash) {
        super(email, passwordHash);
    }
}