import src.controller.BankingController;
import src.model.User;
import src.repository.TransactionRepository;
import src.repository.UserRepository;
import src.service.BkashService;
import src.util.CryptoUtil;
import src.util.SecurityUtil;

import javax.crypto.SecretKey;
import java.io.Console;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║        JavaPay Mobile Banking System v1.0             ║");
            System.out.println("║        Secure • Encrypted • Fast                       ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");

            // Get encryption passphrase
            Console console = System.console();
            String passphrase;

            if (console == null) {
                System.out.println("⚠️  Console not available. Using default passphrase for testing.");
                System.out.println("   For production use, run from terminal for secure input.");
                passphrase = "change_this_to_a_strong_passphrase_for_demo_only";
            } else {
                char[] passChars = console.readPassword("🔐 Enter encryption passphrase: ");
                passphrase = new String(passChars);
                java.util.Arrays.fill(passChars, ' '); // Clear password from memory
            }

            // Derive AES key from passphrase
            byte[] keyBytes = passphrase.getBytes(StandardCharsets.UTF_8);
            SecretKey key = CryptoUtil.keyFromBytes(keyBytes);

            // Initialize repositories with encryption
            UserRepository userRepo = new UserRepository("users.dat", key);
            TransactionRepository txRepo = new TransactionRepository("transactions.dat", key);
            BkashService service = new BkashService(userRepo, txRepo);

            // Seed admin account if missing
            if (!userRepo.exists("9999")) {
                String adminPinHash = SecurityUtil.hashPin("admin");
                User admin = new User("9999", "System Admin", adminPinHash, 0.0, "ADMIN");
                userRepo.save(admin);
                System.out.println("✅ Admin account created (phone: 9999, PIN: admin)");
            }

            // Create demo user with initial balance
            if (!userRepo.exists("01700000000")) {
                String demoPinHash = SecurityUtil.hashPin("1234");
                User demo = new User("01700000000", "Demo User", demoPinHash, 2000.0, "USER");
                userRepo.save(demo);
                System.out.println("✅ Demo user created (phone: 01700000000, PIN: 1234, Balance: 2000 BDT)");
            }

            System.out.println("\n🔒 Database files encrypted with AES-256-GCM");
            System.out.println("🔐 All PINs hashed with SHA-256");

            // Start the banking controller
            BankingController controller = new BankingController(service, userRepo, txRepo);
            controller.start();

        } catch (Exception e) {
            System.out.println("\n❌ System initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}