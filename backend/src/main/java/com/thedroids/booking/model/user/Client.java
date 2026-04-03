package com.thedroids.booking.model.user;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {

    protected Client() {}

    public Client(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "CLIENT";
    }
}
