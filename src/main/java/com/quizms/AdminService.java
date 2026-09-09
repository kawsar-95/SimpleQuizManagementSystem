package com.quizms;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implements the admin flow: keep prompting for new questions until the admin
 * presses {@code q}. Each saved question is appended to {@code quiz.json}.
 */
public final class AdminService {

    private final AppContext context;
    private final JsonStore store = new JsonStore();

    public AdminService(AppContext context) {
        this.context = context;
    }

    public void run() {
        Scanner scanner = ScannerProvider.get();
        List<Question> questions = new ArrayList<>(store.readQuestions(context.getQuizFile()));

        while (true) {
            System.out.println("System:> Input your question");
            System.out.print("Admin:> ");
            String questionText = scanner.nextLine().trim();
            if (questionText.equalsIgnoreCase("q")) {
                System.out.println("System:> Question bank has " + questions.size()
                        + " question(s). Returning to login.");
                return;
            }

            System.out.println("System:> Input option 1:");
            System.out.print("Admin:> ");
            String opt1 = scanner.nextLine().trim();

            System.out.println("System:> Input option 2:");
            System.out.print("Admin:> ");
            String opt2 = scanner.nextLine().trim();

            System.out.println("System:> Input option 3:");
            System.out.print("Admin:> ");
            String opt3 = scanner.nextLine().trim();

            System.out.println("System:> Input option 4:");
            System.out.print("Admin:> ");
            String opt4 = scanner.nextLine().trim();

            int answerKey = promptAnswerKey(scanner);

            questions.add(new Question(questionText, opt1, opt2, opt3, opt4, answerKey));
            store.writeQuestions(context.getQuizFile(), questions);

            System.out.println("System:> Saved successfully! Do you want to add more questions? "
                    + "(press s for start and q for quit)");

            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("q")) {
                System.out.println("System:> Question bank has " + questions.size()
                        + " question(s). Returning to login.");
                return;
            }
            // 's' and any other input loops back to add another question.
        }
    }

    private int promptAnswerKey(Scanner scanner) {
        while (true) {
            System.out.println("System:> What is the answer key? (1-4)");
            System.out.print("Admin:> ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < 1 || value > 4) {
                    System.out.println("System:> Answer key must be between 1 and 4.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("System:> Please enter a numeric value between 1 and 4.");
            }
        }
    }
}
