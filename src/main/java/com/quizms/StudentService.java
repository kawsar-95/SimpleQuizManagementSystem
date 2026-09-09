package com.quizms;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Implements the student flow: pick 10 random questions from the bank, score
 * the responses and print a tailored evaluation message.
 */
public final class StudentService {

    private static final int QUESTIONS_PER_QUIZ = 10;

    private final AppContext context;
    private final JsonStore store = new JsonStore();
    private final Random random = new Random();

    public StudentService(AppContext context) {
        this.context = context;
    }

    public void run() {
        Scanner scanner = ScannerProvider.get();
        List<Question> bank = store.readQuestions(context.getQuizFile());

        if (bank.isEmpty()) {
            System.out.println("System:> The question bank is empty. Ask the admin to add questions first.");
            return;
        }

        // Per the assignment: keep prompting with 's' until the student is ready.
        while (true) {
            System.out.print("Student:> ");
            String ready = scanner.nextLine().trim();
            if (ready.equalsIgnoreCase("s")) {
                break;
            }
            if (ready.equalsIgnoreCase("q")) {
                System.out.println("System:> Maybe next time. Goodbye!");
                return;
            }
            System.out.println("System:> Press 's' to start, or 'q' to quit.");
        }

        int score = takeQuiz(bank);
        printResult(score);

        System.out.println("System:> Would you like to start again? press s for start or q for quit");
        String again = scanner.nextLine().trim();
        if (again.equalsIgnoreCase("s")) {
            run();
        } else {
            System.out.println("System:> Thanks for taking the quiz. Goodbye!");
        }
    }

    private int takeQuiz(List<Question> bank) {
        int score = 0;
        for (int i = 0; i < QUESTIONS_PER_QUIZ; i++) {
            Question question = bank.get(random.nextInt(bank.size()));
            System.out.println();
            System.out.println("[Question " + (i + 1) + "] " + question.getQuestion());
            System.out.println();
            System.out.println("1. " + question.getOption1());
            System.out.println("2. " + question.getOption2());
            System.out.println("3. " + question.getOption3());
            System.out.println("4. " + question.getOption4());
            System.out.println();
            System.out.print("Student:> ");

            int answer = readAnswerKey();
            if (answer == question.getAnswerKey()) {
                score++;
            } else {
                System.out.println("System:> Wrong answer. The correct answer was option "
                        + question.getAnswerKey() + ".");
            }
        }
        return score;
    }

    private int readAnswerKey() {
        Scanner scanner = ScannerProvider.get();
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < 1 || value > 4) {
                    System.out.println("System:> Invalid input. Please enter 1, 2, 3 or 4.");
                    System.out.print("Student:> ");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("System:> Invalid input. Please enter 1, 2, 3 or 4.");
                System.out.print("Student:> ");
            }
        }
    }

    private void printResult(int score) {
        System.out.println();
        if (score >= 8) {
            System.out.println("System:> Excellent! You have got " + score + " out of 10");
        } else if (score >= 5) {
            System.out.println("System:> Good. You have got " + score + " out of 10");
        } else if (score >= 2) {
            System.out.println("System:> Very poor! You have got " + score + " out of 10");
        } else {
            System.out.println("System:> Very sorry you are failed. You have got " + score + " out of 10");
        }
    }
}
