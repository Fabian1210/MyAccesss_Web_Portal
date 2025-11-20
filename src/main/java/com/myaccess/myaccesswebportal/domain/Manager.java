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

    @Override
    public boolean hasAccess(String featureKey) {
        return switch (featureKey) {
            case "VIEW_USERS", "VIEW_DEPARTMENT", "VIEW_REPORTS",
                 "MANAGE_PROJECTS" -> true;
            default -> false;
        };
    }
}