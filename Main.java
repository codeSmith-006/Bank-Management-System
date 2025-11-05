import repository.UserRepository;
import repository.TransactionRepository;
import service.BkashService;
import controller.BankingController;
import util.CryptoUtil;
import util.SecurityUtil;

import javax.crypto.SecretKey;
import java.io.Console;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            Console console = System.console();
            if (console == null) {
                System.out.println("❌ Console not available. Run from terminal.");
                System.exit(1);
            }

            // Hidden passphrase input
            char[] passChars = console.readPassword("Enter encryption passphrase: ");
            String passphrase = new String(passChars);

            // Derive AES key from passphrase
            byte[] keyBytes = passphrase.getBytes(StandardCharsets.UTF_8);
            SecretKey key = CryptoUtil.keyFromBytes(keyBytes);

            // Initialize repositories with the derived key
            UserRepository userRepo = new UserRepository("users.dat", key);
            TransactionRepository txRepo = new TransactionRepository("transactions.dat", key);
            BkashService service = new BkashService(userRepo, txRepo);

            // Seed admin account if missing
            if (!userRepo.exists("9999")) {
                var admin = new model.User("9999", "Admin", SecurityUtil.hashPin("admin"), 0.0, "ADMIN");
                userRepo.save(admin);
                System.out.println("✅ Admin seeded (phone=9999, PIN=admin)");
            }

            // Start the banking system
            BankingController controller = new BankingController(service, userRepo, txRepo);
            controller.start();

        } catch (Exception e) {
            System.out.println("❌ Failed to initialize system: " + e.getMessage());
            System.exit(1);
        }
    }
}
