package com.studentms;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Entry point for the Student Management System.
 *
 * Provides a menu-driven console UI. All input is validated before
 * being forwarded to the repository layer.
 *
 * Requires Java 17+ (uses text blocks and switch expressions).
 */
public class Main {

    private static final Scanner            scanner = new Scanner(System.in);
    private static final IStudentRepository repo    = new StudentRepository();

    // ── Startup ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("  ➤  Enter choice: ");
            System.out.println();
            switch (choice) {
                case 1  -> addStudent();
                case 2  -> searchStudent();
                case 3  -> updateStudent();
                case 4  -> deleteStudent();
                case 5  -> viewAllStudents();
                case 0  -> { running = false; System.out.println("  Goodbye! 👋\n"); }
                default -> System.out.println("  [!] Invalid option. Please choose 0–5.");
            }
        }
        scanner.close();
    }

    // ── 1. ADD ────────────────────────────────────────────────────────────────────

    private static void addStudent() {
        System.out.println("  ── ADD STUDENT ──────────────────────────────");
        String id     = readString("  Student ID  : ");
        String name   = readString("  Full Name   : ");
        int    age    = readInt   ("  Age         : ");
        String course = readString("  Course      : ");
        String email  = readString("  Email       : ");
        double gpa    = readDouble("  GPA (0-4.0) : ");

        Student student = new Student(id, name, age, course, email, gpa);

        if (repo.addStudent(student)) {
            System.out.println("  ✓ Student added successfully!");
        } else {
            System.out.println("  ✗ Student ID '" + id + "' is already taken. Use a unique ID.");
        }
    }

    // ── 2. SEARCH ─────────────────────────────────────────────────────────────────

    private static void searchStudent() {
        System.out.println("  ── SEARCH STUDENT ───────────────────────────");
        System.out.println("  1. Search by Student ID");
        System.out.println("  2. Search by Name");
        int opt = readInt("  ➤  Choose: ");

        switch (opt) {
            case 1 -> {
                String id = readString("  Student ID: ");
                repo.findById(id)
                    .ifPresentOrElse(
                        s -> { System.out.println("  Found:"); System.out.println(s); },
                        () -> System.out.println("  ✗ No student found with ID: " + id)
                    );
            }
            case 2 -> {
                String name    = readString("  Name (partial OK): ");
                List<Student> results = repo.findByName(name);
                if (results.isEmpty()) {
                    System.out.println("  ✗ No students found matching: \"" + name + "\"");
                } else {
                    System.out.println("  ✓ Found " + results.size() + " result(s):");
                    results.forEach(System.out::println);
                }
            }
            default -> System.out.println("  [!] Invalid search option.");
        }
    }

    // ── 3. UPDATE ─────────────────────────────────────────────────────────────────

    private static void updateStudent() {
        System.out.println("  ── UPDATE STUDENT ───────────────────────────");
        String id = readString("  Student ID to update: ");

        Optional<Student> opt = repo.findById(id);
        if (opt.isEmpty()) {
            System.out.println("  ✗ Student not found.");
            return;
        }

        Student current = opt.get();
        System.out.println("  Current record:" + current);
        System.out.println("\n  Enter new values. Press Enter to keep the current value.");

        String name   = readOptional  ("  Name   [" + current.getName()           + "]: ", current.getName());
        int    age    = readOptInt    ("  Age    [" + current.getAge()             + "]: ", current.getAge());
        String course = readOptional  ("  Course [" + current.getCourse()          + "]: ", current.getCourse());
        String email  = readOptional  ("  Email  [" + current.getEmail()           + "]: ", current.getEmail());
        double gpa    = readOptDouble ("  GPA    [" + current.getGpa()             + "]: ", current.getGpa());

        Student updated = new Student(id, name, age, course, email, gpa);
        if (repo.updateStudent(id, updated)) {
            System.out.println("  ✓ Student updated successfully!");
        }
    }

    // ── 4. DELETE ─────────────────────────────────────────────────────────────────

    private static void deleteStudent() {
        System.out.println("  ── DELETE STUDENT ───────────────────────────");
        String id = readString("  Student ID to delete: ");

        repo.findById(id).ifPresentOrElse(student -> {
            System.out.println("  Record found:" + student);
            String confirm = readString("\n  Type 'yes' to permanently delete: ");
            if ("yes".equalsIgnoreCase(confirm)) {
                repo.deleteStudent(id);
                System.out.println("  ✓ Student deleted.");
            } else {
                System.out.println("  Delete cancelled.");
            }
        }, () -> System.out.println("  ✗ Student not found."));
    }

    // ── 5. VIEW ALL ───────────────────────────────────────────────────────────────

    private static void viewAllStudents() {
        List<Student> all = repo.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("  No students on record yet.");
        } else {
            System.out.println("  ── ALL STUDENTS (" + all.size() + " total) ────────────────────");
            all.forEach(System.out::println);
        }
    }

    // ── Display Helpers ───────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("""
            
            ╔══════════════════════════════════════════════╗
            ║        STUDENT  MANAGEMENT  SYSTEM           ║
            ║              Java Edition v1.0               ║
            ╚══════════════════════════════════════════════╝
            """);
    }

    private static void printMenu() {
        System.out.println("""
              ┌──────────────────────────────┐
              │         MAIN  MENU           │
              ├──────────────────────────────┤
              │  1.  Add Student             │
              │  2.  Search Student          │
              │  3.  Update Student          │
              │  4.  Delete Student          │
              │  5.  View All Students       │
              │  0.  Exit                    │
              └──────────────────────────────┘""");
    }

    // ── Input Helpers ─────────────────────────────────────────────────────────────

    /** Reads a non-empty string from stdin. */
    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  [!] Input cannot be empty.");
        }
    }

    /** Reads a validated integer from stdin, retrying on bad input. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number.");
            }
        }
    }

    /** Reads a validated double from stdin, retrying on bad input. */
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number (e.g. 3.5).");
            }
        }
    }

    /** Optional string input — returns defaultVal if user presses Enter. */
    private static String readOptional(String prompt, String defaultVal) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultVal : input;
    }

    /** Optional int input — returns defaultVal if user presses Enter. */
    private static int readOptInt(String prompt, int defaultVal) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return defaultVal;
        try { return Integer.parseInt(input); }
        catch (NumberFormatException e) {
            System.out.println("  [!] Invalid number. Keeping current value.");
            return defaultVal;
        }
    }

    /** Optional double input — returns defaultVal if user presses Enter. */
    private static double readOptDouble(String prompt, double defaultVal) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return defaultVal;
        try { return Double.parseDouble(input); }
        catch (NumberFormatException e) {
            System.out.println("  [!] Invalid number. Keeping current value.");
            return defaultVal;
        }
    }
}
