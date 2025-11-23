package com.myaccess.myaccesswebportal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected User() {
        // for JPA
    }

    protected User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
    }

    public String getDisplayRole() {
        if (this instanceof Admin) return "ADMIN";
        if (this instanceof Manager) return "MANAGER";
        if (this instanceof Employee) return "EMPLOYEE";
        return "USER";
    }

    public boolean hasAccess(String featureKey) {
        return switch (featureKey) {
            case "MANAGE_USERS" -> this instanceof Admin;

            case "VIEW_USERS", "VIEW_DEPARTMENT", "VIEW_REPORTS", "MANAGE_PROJECTS" ->
                    (this instanceof Admin) || (this instanceof Manager);

            case "VIEW_SELF", "EDIT_SELF" -> true;

            default -> false;
        };
    }

    @Transient
    public String getRoleLabel() {
        return switch (getClass().getSimpleName()) {
            case "Admin" -> "Admin";
            case "Manager" -> "Manager";
            case "Employee" -> "Employee";
            default -> "User";
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}