package src.repository;

import src.model.Transaction;
import src.util.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionRepository {
    private final File dbFile;
    private final SecretKey key;
    private List<Transaction> transactions;

    public TransactionRepository(String filename, SecretKey key) {
        this.dbFile = new File(filename);
        this.key = key;
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!dbFile.exists()) {
            transactions = new ArrayList<>();
            return;
        }
        try (FileInputStream fis = new FileInputStream(dbFile)) {
            byte[] cipher = fis.readAllBytes();
            byte[] plain = CryptoUtil.decrypt(cipher, key);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(plain))) {
                transactions = (List<Transaction>) ois.readObject();
            }
        } catch (Exception e) {
            System.out.println("[TransactionRepository] Could not load DB, starting fresh: " + e.getMessage());
            transactions = new ArrayList<>();
        }
    }

    private void persist() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(transactions);
            oos.flush();
            byte[] plain = bos.toByteArray();
            byte[] cipher = CryptoUtil.encrypt(plain, key);
            try (FileOutputStream fos = new FileOutputStream(dbFile)) {
                fos.write(cipher);
            }
        } catch (Exception e) {
            System.out.println("[TransactionRepository] Persist failed: " + e.getMessage());
        }
    }

    public synchronized void save(Transaction tx) {
        transactions.add(tx);
        persist();
    }

    public synchronized List<Transaction> findByPhone(String phone) {
        return transactions.stream()
                .filter(tx -> phone.equals(tx.getFrom()) || phone.equals(tx.getTo()))
                .collect(Collectors.toList());
    }

    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }
}