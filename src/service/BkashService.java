package src.service;

import src.service.BankService;
import src.model.Transaction;
import src.model.Transaction.Type;
import src.model.User;
import src.repository.TransactionRepository;
import src.repository.UserRepository;
import src.util.SecurityUtil;

import java.util.List;

public class BkashService implements BankService {
    private final UserRepository userRepo;
    private final TransactionRepository txRepo;

    private static final double SEND_FEE = 3.0;
    private static final double CASHOUT_FEE = 5.0;

    public BkashService(UserRepository userRepo, TransactionRepository txRepo) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
    }

    @Override
    public boolean register(String phone, String name, String pin) {
        if (userRepo.exists(phone)) return false;
        String pinHash = SecurityUtil.hashPin(pin);
        User u = new User(phone, name, pinHash, 0.0, "USER");
        userRepo.save(u);
        return true;
    }

    @Override
    public boolean login(String phone, String pin) {
        User u = userRepo.findByPhone(phone);
        if (u == null) return false;
        return SecurityUtil.verifyPin(pin, u.getPinHash());
    }

    @Override
    public boolean addMoney(String phone, double amount, String pin) {
        if (amount <= 0) return false;
        User u = userRepo.findByPhone(phone);
        if (u == null) return false;
        if (!SecurityUtil.verifyPin(pin, u.getPinHash())) return false;

        synchronized (this) {
            u.deposit(amount);
            userRepo.save(u);

            Transaction t = new Transaction(Type.TOPUP, null, phone, amount, "Add money via agent/bank");
            txRepo.save(t);
            u.addTransaction(t.getId());
            userRepo.save(u);
            return true;
        }
    }

    @Override
    public boolean sendMoney(String fromPhone, String toPhone, double amount, String pin) {
        if (amount <= 0) return false;
        User from = userRepo.findByPhone(fromPhone);
        User to = userRepo.findByPhone(toPhone);
        if (from == null || to == null) return false;
        if (!SecurityUtil.verifyPin(pin, from.getPinHash())) return false;

        double total = amount + SEND_FEE;
        synchronized (this) {
            if (!from.withdraw(total)) return false;
            to.deposit(amount);
            userRepo.save(from);
            userRepo.save(to);

            Transaction t = new Transaction(Type.SEND_MONEY, fromPhone, toPhone, amount, "Send money");
            txRepo.save(t);
            from.addTransaction(t.getId());
            to.addTransaction(t.getId());
            userRepo.save(from);
            userRepo.save(to);
            return true;
        }
    }

    @Override
    public boolean cashOut(String phone, double amount, String pin) {
        if (amount <= 0) return false;
        User u = userRepo.findByPhone(phone);
        if (u == null) return false;
        if (!SecurityUtil.verifyPin(pin, u.getPinHash())) return false;

        double total = amount + CASHOUT_FEE;
        synchronized (this) {
            if (!u.withdraw(total)) return false;
            userRepo.save(u);

            Transaction t = new Transaction(Type.CASH_OUT, phone, null, amount, "Cash out via agent");
            txRepo.save(t);
            u.addTransaction(t.getId());
            userRepo.save(u);
            return true;
        }
    }

    @Override
    public boolean payment(String fromPhone, String merchantId, double amount, String pin) {
        if (amount <= 0) return false;
        User u = userRepo.findByPhone(fromPhone);
        if (u == null) return false;
        if (!SecurityUtil.verifyPin(pin, u.getPinHash())) return false;

        synchronized (this) {
            if (!u.withdraw(amount)) return false;
            userRepo.save(u);

            Transaction t = new Transaction(Type.PAYMENT, fromPhone, merchantId, amount, "Payment to merchant");
            txRepo.save(t);
            u.addTransaction(t.getId());
            userRepo.save(u);
            return true;
        }
    }

    @Override
    public boolean recharge(String phone, String topupNumber, double amount, String pin) {
        if (amount <= 0) return false;
        User u = userRepo.findByPhone(phone);
        if (u == null) return false;
        if (!SecurityUtil.verifyPin(pin, u.getPinHash())) return false;

        synchronized (this) {
            if (!u.withdraw(amount)) return false;
            userRepo.save(u);

            Transaction t = new Transaction(Type.RECHARGE, phone, topupNumber, amount, "Mobile recharge");
            txRepo.save(t);
            u.addTransaction(t.getId());
            userRepo.save(u);
            return true;
        }
    }

    @Override
    public double checkBalance(String phone) {
        User u = userRepo.findByPhone(phone);
        if (u == null) return -1;
        return u.getBalance();
    }

    @Override
    public List<Transaction> getTransactions(String phone) {
        return txRepo.findByPhone(phone);
    }

    @Override
    public boolean changePin(String phone, String oldPin, String newPin) {
        User u = userRepo.findByPhone(phone);
        if (u == null) return false;
        if (!SecurityUtil.verifyPin(oldPin, u.getPinHash())) return false;

        u.setPinHash(SecurityUtil.hashPin(newPin));
        userRepo.save(u);
        return true;
    }
}