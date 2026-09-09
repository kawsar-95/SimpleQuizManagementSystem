package com.quizms;

import java.util.List;
import java.util.Scanner;

/**
 * Handles username/password entry and credential verification against
 * {@code users.json}.
 */
public final class AuthService {

    private final AppContext context;
    private final JsonStore store = new JsonStore();

    public AuthService(AppContext context) {
        this.context = context;
    }

    /**
     * Returns an {@link AuthenticatedUser} on success or {@code null} if the
     * user explicitly chose to quit the login prompt.
     */
    public AuthenticatedUser login() {
        Scanner scanner = ScannerProvider.get();
        List<User> users = store.readUsers(context.getUsersFile());

        while (true) {
            System.out.println("System:> Enter your username");
            System.out.print("User:> ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("q")) {
                System.out.println("System:> Goodbye!");
                System.exit(0);
            }

            System.out.println("System:> Enter password");
            System.out.print("User:> ");
            String password = scanner.nextLine().trim();

            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(username)
                        && user.getPassword().equals(password)) {
                    return new AuthenticatedUser(user.getUsername(), user.getRole());
                }
            }
            System.out.println("System:> Invalid credentials. Please try again (or press 'q' to quit).");
        }
    }

    /**
     * Lightweight value object that drops the password from a successful login
     * so the rest of the app does not need to carry sensitive data around.
     */
    public static final class AuthenticatedUser {
        private final String username;
        private final String role;

        public AuthenticatedUser(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }
}
