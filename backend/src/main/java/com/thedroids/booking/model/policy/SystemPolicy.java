package com.thedroids.booking.model.policy;

import jakarta.persistence.*;

@Entity
@Table(name = "system_policies")
public class SystemPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    private int cancellationWindowHours;
    private double cancellationFeePercent;
    private double refundPercent;
    private boolean notificationsEnabled;

    public SystemPolicy() {
        this.cancellationWindowHours = 24;
        this.cancellationFeePercent = 10.0;
        this.refundPercent = 90.0;
        this.notificationsEnabled = true;
    }

    public Long getPolicyId() { return policyId; }
    public int getCancellationWindowHours() { return cancellationWindowHours; }
    public double getCancellationFeePercent() { return cancellationFeePercent; }
    public double getRefundPercent() { return refundPercent; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }

    public void setCancellationWindowHours(int hours) { this.cancellationWindowHours = hours; }
    public void setCancellationFeePercent(double percent) { this.cancellationFeePercent = percent; }
    public void setRefundPercent(double percent) { this.refundPercent = percent; }
    public void setNotificationsEnabled(boolean enabled) { this.notificationsEnabled = enabled; }

    @Override
    public String toString() {
        return String.format(
            "Cancellation window: %dh | Fee: %.0f%% | Refund: %.0f%% | Notifications: %s",
            cancellationWindowHours, cancellationFeePercent, refundPercent,
            notificationsEnabled ? "ON" : "OFF");
    }
}
