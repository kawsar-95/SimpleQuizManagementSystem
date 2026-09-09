package com.quizms;

import java.util.Scanner;

/**
 * Shared {@link Scanner} singleton over {@code System.in}.
 *
 * <p>Creating more than one {@code Scanner} on {@code System.in} is a common
 * pitfall: each new scanner buffers input independently, so subsequent
 * {@code nextLine()} calls appear to "skip" lines. Centralising the scanner
 * here avoids that bug entirely.</p>
 */
public final class ScannerProvider {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ScannerProvider() {
    }

    public static Scanner get() {
        return SCANNER;
    }
}
