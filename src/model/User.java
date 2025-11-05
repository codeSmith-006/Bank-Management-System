package src.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String phoneNumber;
    private String name;
    private String pinHash;
    private double balance;
    private String role; // "USER" or "ADMIN"
    private LocalDateTime createdAt;
    private List<String> transactions;

    public User(String phoneNumber, String name, String pinHash, double balance, String role) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.pinHash = pinHash;
        this.balance = balance;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public double getBalance() {
        return balance;
    }

    public String getRole() {
        return role;
    }

    public synchronized void deposit(double amount) {
        this.balance += amount;
    }

    public synchronized boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public void addTransaction(String txId) {
        transactions.add(txId);
    }

    @Override
    public String toString() {
        return String.format("User[name=%s, phone=%s, balance=%.2f, role=%s]", 
            name, phoneNumber, balance, role);
    }
}