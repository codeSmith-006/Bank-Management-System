package src.service;

import src.model.Transaction;
import java.util.List;

public interface BankService {
    boolean register(String phone, String name, String pin);
    boolean login(String phone, String pin);
    boolean sendMoney(String fromPhone, String toPhone, double amount, String pin);
    boolean cashOut(String phone, double amount, String pin);
    boolean payment(String fromPhone, String merchantId, double amount, String pin);
    boolean recharge(String phone, String topupNumber, double amount, String pin);
    double checkBalance(String phone);
    List<Transaction> getTransactions(String phone);
    boolean changePin(String phone, String oldPin, String newPin);
}