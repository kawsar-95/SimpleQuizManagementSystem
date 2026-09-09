package com.quizms;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round-trips a small bank of questions through the JSON file format to make
 * sure the schema matches the assignment specification.
 */
class JsonStoreTest {

    private Path tempFile;
    private final JsonStore store = new JsonStore();

    @BeforeEach
    void setUp() throws Exception {
        tempFile = Files.createTempFile("quiz", ".json");
        Files.writeString(tempFile, "[]");
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void writesAndReadsQuestionsRoundTrip() {
        List<Question> original = Arrays.asList(
                new Question("Q1", "A", "B", "C", "D", 1),
                new Question("Q2", "A2", "B2", "C2", "D2", 4)
        );

        store.writeQuestions(tempFile, original);
        List<Question> loaded = store.readQuestions(tempFile);

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            Question o = original.get(i);
            Question l = loaded.get(i);
            assertEquals(o.getQuestion(), l.getQuestion());
            assertEquals(o.getOption1(), l.getOption1());
            assertEquals(o.getOption2(), l.getOption2());
            assertEquals(o.getOption3(), l.getOption3());
            assertEquals(o.getOption4(), l.getOption4());
            assertEquals(o.getAnswerKey(), l.getAnswerKey());
        }
    }

    @Test
    void readingMissingFileThrows() {
        Path missing = tempFile.resolveSibling("does-not-exist.json");
        assertThrows(IllegalStateException.class, () -> store.readQuestions(missing));
    }

    @Test
    void readingMalformedJsonThrows() throws Exception {
        Files.writeString(tempFile, "{not json}");
        assertThrows(IllegalStateException.class, () -> store.readQuestions(tempFile));
    }

    @Test
    void persistedJsonUsesAssignmentSchema() throws Exception {
        List<Question> bank = List.of(new Question("x", "a", "b", "c", "d", 2));
        store.writeQuestions(tempFile, bank);
        String text = Files.readString(tempFile);

        // Spec mandates these exact field names.
        assertTrue(text.contains("\"question\""), "field 'question' missing");
        assertTrue(text.contains("\"option 1\""), "field 'option 1' missing");
        assertTrue(text.contains("\"option 2\""), "field 'option 2' missing");
        assertTrue(text.contains("\"option 3\""), "field 'option 3' missing");
        assertTrue(text.contains("\"option 4\""), "field 'option 4' missing");
        assertTrue(text.contains("\"answerkey\""), "field 'answerkey' missing");
    }
}
