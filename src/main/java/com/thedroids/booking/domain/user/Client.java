package com.thedroids.booking.domain.user;

public class Client extends User {

    public Client(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() {
        return "CLIENT";
    }
}
