package com.thedroids.booking.domain.user;

public class Consultant extends User {

    private String specialization;
    private double hourlyRate;
    private boolean approved;

    public Consultant(String id, String name, String email, String password,
                      String specialization, double hourlyRate) {
        super(id, name, email, password);
        this.specialization = specialization;
        this.hourlyRate = hourlyRate;
        this.approved = false;
    }

    @Override
    public String getRole() {
        return "CONSULTANT";
    }

    public String getSpecialization() { return specialization; }
    public double getHourlyRate() { return hourlyRate; }
    public boolean isApproved() { return approved; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
