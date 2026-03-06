package com.thedroids.booking.domain.user;

public abstract class User {

    private final String id;
    private String name;
    private String email;
    private final String password;

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

    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + "{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
