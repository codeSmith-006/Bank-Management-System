package src.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        SEND_MONEY, CASH_OUT, PAYMENT, RECHARGE, TOPUP
    }

    private String id;
    private Type type;
    private String from;
    private String to;
    private double amount;
    private LocalDateTime timestamp;
    private String description;

    public Transaction(Type type, String from, String to, double amount, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s -> %s : %.2f (%s) - %s", 
            timestamp.format(formatter),
            from == null ? "SYSTEM" : from, 
            to == null ? "SYSTEM" : to, 
            amount, 
            type.name(),
            description);
    }
}