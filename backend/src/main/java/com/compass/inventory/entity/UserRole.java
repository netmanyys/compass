package com.compass.inventory.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String role;

    public UserRole() {}

    public UserRole(UserEntity user, String role) {
        this.user = user;
        this.role = role;
    }

    public UUID getId() { return id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
