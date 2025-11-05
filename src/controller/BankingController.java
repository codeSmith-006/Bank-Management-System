package src.controller;

import src.model.Transaction;
import src.model.User;
import src.repository.TransactionRepository;
import src.repository.UserRepository;
import src.service.BankService;
import src.util.InputUtil;

import java.util.List;
import java.util.Map;

public class BankingController {
    private final BankService service;
    private final UserRepository userRepo;
    private final TransactionRepository txRepo;
    private String currentUserPhone = null;

    public BankingController(BankService service, UserRepository userRepo, TransactionRepository txRepo) {
        this.service = service;
        this.userRepo = userRepo;
        this.txRepo = txRepo;
    }

    public void start() {
        while (true) {
            showInitialMenu();
            int sel = InputUtil.readInt("Select: ");
            switch (sel) {
                case 1 -> handleRegister();
                case 2 -> handleLogin();
                case 3 -> {
                    System.out.println("\n✅ Thank you for using JavaPay! Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }

    private void showInitialMenu() {
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║   Welcome to JavaPay (*247#)   ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
    }

    private void handleRegister() {
        System.out.println("\n--- REGISTRATION ---");
        String phone = InputUtil.readLine("Enter phone (11 digits): ");
        String name = InputUtil.readLine("Enter name: ");
        String pin = InputUtil.readLine("Set PIN (4 digits): ");
        boolean ok = service.register(phone, name, pin);
        if (ok)
            System.out.println("✅ Account created successfully!");
        else
            System.out.println("❌ Account with this phone already exists.");
    }

    private void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        String phone = InputUtil.readLine("Enter phone: ");
        String pin = InputUtil.readLine("Enter PIN: ");

        User user = userRepo.findByPhone(phone);
        if (user == null) {
            System.out.println("❌ User not found!");
            return;
        }

        boolean ok = service.login(phone, pin);
        if (ok) {
            currentUserPhone = phone;
            System.out.println("✅ Login successful. Welcome, " + user.getName() + "!");

            if ("ADMIN".equals(user.getRole())) {
                adminMenu();
            } else {
                userMenu();
            }
        } else {
            System.out.println("❌ Login failed. Check phone/PIN.");
        }
    }

    private void userMenu() {
        while (true) {
            showUserMenu();
            int sel = InputUtil.readInt("Select: ");
            switch (sel) {
                case 1 -> handleSendMoney();
                case 2 -> handleCashOut();
                case 3 -> handlePayment();
                case 4 -> handleRecharge();
                case 5 -> handleBalance();
                case 6 -> handleTransactionHistory();
                case 7 -> handleChangePin();
                case 8 -> {
                    currentUserPhone = null;
                    System.out.println("✅ Logged out successfully.");
                    return;
                }
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║       JavaPay User Menu        ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println("1. Send Money");
        System.out.println("2. Cash Out");
        System.out.println("3. Payment");
        System.out.println("4. Mobile Recharge");
        System.out.println("5. Check Balance");
        System.out.println("6. Transaction History");
        System.out.println("7. Change PIN");
        System.out.println("8. Logout");
    }

    private void handleSendMoney() {
        System.out.println("\n--- SEND MONEY (Fee: 3.0) ---");
        String to = InputUtil.readLine("Enter recipient phone: ");
        double amount = InputUtil.readDouble("Enter amount: ");
        String pin = InputUtil.readLine("Enter PIN to confirm: ");
        boolean ok = service.sendMoney(currentUserPhone, to, amount, pin);
        System.out.println(ok ? "✅ Send Money: SUCCESS" : "❌ Send Money: FAILED (insufficient balance/bad details)");
    }

    private void handleCashOut() {
        System.out.println("\n--- CASH OUT (Fee: 5.0) ---");
        double amount = InputUtil.readDouble("Enter amount to cash out: ");
        String pin = InputUtil.readLine("Enter PIN to confirm: ");
        boolean ok = service.cashOut(currentUserPhone, amount, pin);
        System.out.println(ok ? "✅ Cash Out: SUCCESS" : "❌ Cash Out: FAILED (insufficient balance/bad details)");
    }

    private void handlePayment() {
        System.out.println("\n--- PAYMENT ---");
        String merchant = InputUtil.readLine("Enter merchant ID: ");
        double amount = InputUtil.readDouble("Enter amount: ");
        String pin = InputUtil.readLine("Enter PIN to confirm: ");
        boolean ok = service.payment(currentUserPhone, merchant, amount, pin);
        System.out.println(ok ? "✅ Payment: SUCCESS" : "❌ Payment: FAILED (insufficient balance/bad details)");
    }

    private void handleRecharge() {
        System.out.println("\n--- MOBILE RECHARGE ---");
        String topup = InputUtil.readLine("Enter number to top-up: ");
        double amount = InputUtil.readDouble("Enter amount: ");
        String pin = InputUtil.readLine("Enter PIN to confirm: ");
        boolean ok = service.recharge(currentUserPhone, topup, amount, pin);
        System.out.println(ok ? "✅ Recharge: SUCCESS" : "❌ Recharge: FAILED (insufficient balance/bad details)");
    }

    private void handleBalance() {
        double bal = service.checkBalance(currentUserPhone);
        System.out.printf("\n💰 Your balance: %.2f BDT\n", bal);
    }

    private void handleTransactionHistory() {
        List<Transaction> txs = service.getTransactions(currentUserPhone);
        if (txs.isEmpty()) {
            System.out.println("\n📋 No transactions found.");
            return;
        }
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   Transaction History                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        for (Transaction t : txs) {
            System.out.println(t.toString());
        }
    }

    private void handleChangePin() {
        System.out.println("\n--- CHANGE PIN ---");
        String oldPin = InputUtil.readLine("Enter current PIN: ");
        String newPin = InputUtil.readLine("Enter new PIN: ");
        boolean ok = service.changePin(currentUserPhone, oldPin, newPin);
        System.out.println(ok ? "✅ PIN changed successfully." : "❌ PIN change failed. Check current PIN.");
    }

    private void adminMenu() {
        while (true) {
            showAdminMenu();
            int sel = InputUtil.readInt("Select: ");
            switch (sel) {
                case 1 -> viewAllUsers();
                case 2 -> viewAllTransactions();
                case 3 -> {
                    currentUserPhone = null;
                    System.out.println("✅ Logged out successfully.");
                    return;
                }
                default -> System.out.println("❌ Invalid option. Try again.");
            }
        }
    }

    private void showAdminMenu() {
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║       ADMIN CONTROL PANEL      ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println("1. View All Users");
        System.out.println("2. View All Transactions");
        System.out.println("3. Logout");
    }

    private void viewAllUsers() {
        Map<String, User> users = userRepo.findAll();
        if (users.isEmpty()) {
            System.out.println("\n📋 No users in the system.");
            return;
        }
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      All Users                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.printf("%-15s %-20s %-15s %-10s\n", "Phone", "Name", "Balance", "Role");
        System.out.println("─".repeat(60));
        for (User u : users.values()) {
            System.out.printf("%-15s %-20s %-15.2f %-10s\n",
                    u.getPhoneNumber(), u.getName(), u.getBalance(), u.getRole());
        }
    }

    private void viewAllTransactions() {
        List<Transaction> txs = txRepo.findAll();
        if (txs.isEmpty()) {
            System.out.println("\n📋 No transactions in the system.");
            return;
        }
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   All Transactions                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        for (Transaction t : txs) {
            System.out.println(t.toString());
        }
    }
}