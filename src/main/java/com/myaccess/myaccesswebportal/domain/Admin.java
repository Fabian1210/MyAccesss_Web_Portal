package com.myaccess.myaccesswebportal.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String email, String passwordHash, boolean enabled) {
        super(email, passwordHash);
    }
}