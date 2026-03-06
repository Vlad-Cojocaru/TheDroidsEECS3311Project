package com.thedroids.booking.domain.payment;

import java.time.LocalDateTime;

public class Payment {

    private String id;
    private String bookingId;
    private String clientId;
    private String paymentMethodId;
    private double amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime timestamp;

    public Payment(String id, String bookingId, String clientId,
                   String paymentMethodId, double amount) {
        this.id = id;
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.paymentMethodId = paymentMethodId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getClientId() { return clientId; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @Override
    public String toString() {
        return "Payment{id='" + id + "', amount=$" + amount
        + ", status=" + status + ", txn='" + transactionId + "'}";
    }
}
