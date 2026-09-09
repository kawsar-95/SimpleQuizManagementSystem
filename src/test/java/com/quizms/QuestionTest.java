package com.quizms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    @Test
    void gettersReturnConstructorValues() {
        Question q = new Question(
                "Which is white-box testing?",
                "Equivalence Partitioning",
                "Boundary Value Testing",
                "Decision Table Testing",
                "Adhoc Testing",
                3);

        assertEquals("Which is white-box testing?", q.getQuestion());
        assertEquals("Equivalence Partitioning", q.getOption1());
        assertEquals("Boundary Value Testing", q.getOption2());
        assertEquals("Decision Table Testing", q.getOption3());
        assertEquals("Adhoc Testing", q.getOption4());
        assertEquals(3, q.getAnswerKey());
    }

    @Test
    void questionRecordIsNotNull() {
        Question q = new Question("q", "a", "b", "c", "d", 1);
        assertNotNull(q);
        assertTrue(q.getAnswerKey() >= 1 && q.getAnswerKey() <= 4);
        assertFalse(q.getOption1().isBlank());
    }
}
