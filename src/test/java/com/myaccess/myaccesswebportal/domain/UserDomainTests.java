package com.myaccess.myaccesswebportal.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDomainTests {

    // Test that a user's role is displayed correctly
    @Test
    void displayRoleMatchesConcreteUserType() {
        User admin = new Admin("admin@example.com", "hash", true);
        User manager = new Manager("manager@example.com", "hash");
        User employee = new Employee("employee@example.com", "hash");

        assertEquals("ADMIN", admin.getDisplayRole());
        assertEquals("MANAGER", manager.getDisplayRole());
        assertEquals("EMPLOYEE", employee.getDisplayRole());
    }

    // Test that a user's specified role reflects their permissions
    @Test
    void hasAccessReflectsDifferentPermissions() {
        User admin = new Admin("admin@example.com", "hash", true);
        User employee = new Employee("employee@example.com", "hash");

        // Admin should have elevated permissions
        assertTrue(admin.hasAccess("MANAGE_USERS"));
        assertTrue(admin.hasAccess("VIEW_REPORTS"));

        // Employee should NOT have admin-level permissions
        assertFalse(employee.hasAccess("MANAGE_USERS"));
        assertFalse(employee.hasAccess("VIEW_REPORTS"));

        // But should be able to view/edit their own info
        assertTrue(employee.hasAccess("VIEW_SELF"));
        assertTrue(employee.hasAccess("EDIT_SELF"));
    }
}