package src.repository;

import src.model.User;
import src.util.CryptoUtil;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final File dbFile;
    private final SecretKey key;
    private Map<String, User> users;

    public UserRepository(String filename, SecretKey key) {
        this.dbFile = new File(filename);
        this.key = key;
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!dbFile.exists()) {
            users = new HashMap<>();
            return;
        }
        try (FileInputStream fis = new FileInputStream(dbFile)) {
            byte[] cipher = fis.readAllBytes();
            byte[] plain = CryptoUtil.decrypt(cipher, key);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(plain))) {
                users = (Map<String, User>) ois.readObject();
            }
        } catch (Exception e) {
            System.out.println("[UserRepository] Could not load DB, starting fresh: " + e.getMessage());
            users = new HashMap<>();
        }
    }

    private void persist() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(users);
            oos.flush();
            byte[] plain = bos.toByteArray();
            byte[] cipher = CryptoUtil.encrypt(plain, key);
            try (FileOutputStream fos = new FileOutputStream(dbFile)) {
                fos.write(cipher);
            }
        } catch (Exception e) {
            System.out.println("[UserRepository] Persist failed: " + e.getMessage());
        }
    }

    public synchronized boolean exists(String phone) {
        return users.containsKey(phone);
    }

    public synchronized void save(User user) {
        users.put(user.getPhoneNumber(), user);
        persist();
    }

    public synchronized User findByPhone(String phone) {
        return users.get(phone);
    }

    public synchronized Map<String, User> findAll() {
        return new HashMap<>(users);
    }
}