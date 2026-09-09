package com.quizms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test that confirms the bundled seed files have at least 30 SQA
 * questions and that every answer key is in the valid 1..4 range.
 *
 * <p>This protects the "30 questions" requirement of the assignment.</p>
 */
class SeedQuestionsTest {

    @BeforeEach
    void setUp() throws Exception {
        // no-op; reads happen lazily against the bundled resource
    }

    @AfterEach
    void tearDown() throws Exception {
        // no-op
    }

    @Test
    void bundledQuizJsonHasAtLeast30Questions() throws Exception {
        List<Question> bank = readBundled();
        assertNotNull(bank);
        assertTrue(bank.size() >= 30,
                "Expected at least 30 SQA questions in src/main/resources/quiz.json, found " + bank.size());
    }

    @Test
    void everyAnswerKeyIsBetween1And4() throws Exception {
        List<Question> bank = readBundled();
        for (Question q : bank) {
            int key = q.getAnswerKey();
            assertTrue(key >= 1 && key <= 4,
                    "Question has invalid answer key: " + key + " for '" + q.getQuestion() + "'");
        }
    }

    @Test
    void everyQuestionHasAllFourOptions() throws Exception {
        List<Question> bank = readBundled();
        for (Question q : bank) {
            assertNotNull(q.getOption1());
            assertNotNull(q.getOption2());
            assertNotNull(q.getOption3());
            assertNotNull(q.getOption4());
            assertTrue(!q.getOption1().isBlank());
            assertTrue(!q.getOption2().isBlank());
            assertTrue(!q.getOption3().isBlank());
            assertTrue(!q.getOption4().isBlank());
        }
    }

    private List<Question> readBundled() throws Exception {
        // Read from disk so the test still works without classpath resources
        // being on the runtime classpath in unusual IDE setups.
        Path seed = locateSeedFile();
        return new JsonStore().readQuestions(seed);
    }

    private Path locateSeedFile() throws Exception {
        Path cwd = Path.of("").toAbsolutePath();
        try (Stream<Path> stream = Files.walk(cwd, 5)) {
            return stream
                    .filter(p -> p.endsWith("src/main/resources/quiz.json"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("src/main/resources/quiz.json not found near " + cwd));
        }
    }
}
