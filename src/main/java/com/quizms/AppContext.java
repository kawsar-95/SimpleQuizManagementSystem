package com.quizms;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds the resolved filesystem locations of the data files used by the app.
 *
 * <p>Resolution order for {@code users.json} and {@code quiz.json}:</p>
 * <ol>
 *   <li>Current working directory (lets the operator ship their own copies).</li>
 *   <li>The classpath (so {@code ./gradlew run} picks up the bundled seeds).</li>
 * </ol>
 *
 * <p>The chosen location is reported at startup so users always know where
 * their data is being persisted.</p>
 */
public final class AppContext {

    private final Path dataDir;
    private final Path usersFile;
    private final Path quizFile;

    private AppContext(Path dataDir, Path usersFile, Path quizFile) {
        this.dataDir = dataDir;
        this.usersFile = usersFile;
        this.quizFile = quizFile;
    }

    /**
     * Resolve the data files, copying the bundled seed copies on first run so
     * the user has a working bank immediately.
     */
    public static AppContext bootstrap() {
        Path cwd = Paths.get("").toAbsolutePath();
        Path dataDir = cwd.resolve("data");
        Path usersFile = dataDir.resolve("users.json");
        Path quizFile = dataDir.resolve("quiz.json");

        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create data directory: " + dataDir, e);
        }

        copySeedIfMissing(usersFile, "/users.json");
        copySeedIfMissing(quizFile, "/quiz.json");

        System.out.println("System:> Using data directory: " + dataDir);
        return new AppContext(dataDir, usersFile, quizFile);
    }

    private static void copySeedIfMissing(Path target, String resource) {
        if (Files.exists(target)) {
            return;
        }
        try (var in = AppContext.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Seed resource not found on classpath: " + resource);
            }
            Files.copy(in, target);
        } catch (Exception e) {
            throw new IllegalStateException("Could not seed " + target + " from " + resource, e);
        }
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path getUsersFile() {
        return usersFile;
    }

    public Path getQuizFile() {
        return quizFile;
    }
}
