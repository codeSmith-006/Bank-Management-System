package src.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                String s = readLine(prompt);
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                String s = readLine(prompt);
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid decimal. Try again.");
            }
        }
    }
}