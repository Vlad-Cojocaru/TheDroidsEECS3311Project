package com.thedroids.booking.model.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;

    protected User() {}

    protected User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Transient
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + "{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
