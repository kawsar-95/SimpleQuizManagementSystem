package com.quizms;

/**
 * Application entry point for the Simple Java Quiz Management System.
 *
 * <p>The program is a console-based application that lets an admin populate a
 * question bank of MCQs (saved to {@code quiz.json}) and lets a student take a
 * randomly generated 10-question quiz from that bank. User credentials are
 * loaded from {@code users.json}.</p>
 *
 * <p>Run with: {@code java -jar SimpleQuizManagementSystem-1.0.0.jar}
 * or via Gradle: {@code ./gradlew run}.</p>
 */
public final class Main {

    private Main() {
        // utility class
    }

    public static void main(String[] args) {
        // The application loop: log in -> play role -> ask to repeat.
        // Both AdminService and StudentService expose a single entry method,
        // so Main just routes by role and keeps the top-level UX consistent.
        AppContext context = AppContext.bootstrap();

        boolean running = true;
        while (running) {
            AuthService authService = new AuthService(context);
            AuthService.AuthenticatedUser user = authService.login();
            if (user == null) {
                // login loop already exited cleanly, just restart from the top
                continue;
            }

            switch (user.getRole()) {
                case "admin" -> {
                    System.out.println("System:> Welcome " + user.getUsername()
                            + "! Please create new questions in the question bank.");
                    new AdminService(context).run();
                }
                case "student" -> {
                    System.out.println("System:> Welcome " + user.getUsername()
                            + " to the quiz! We will throw you 10 questions. "
                            + "Each MCQ mark is 1 and no negative marking. "
                            + "Are you ready? Press 's' for start.");
                    new StudentService(context).run();
                }
                default -> System.out.println("System:> Unknown role '" + user.getRole()
                        + "'. Contact the administrator.");
            }
        }
    }
}
