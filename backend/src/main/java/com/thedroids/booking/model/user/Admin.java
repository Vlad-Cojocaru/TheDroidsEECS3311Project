package com.thedroids.booking.model.user;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    protected Admin() {}

    public Admin(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
