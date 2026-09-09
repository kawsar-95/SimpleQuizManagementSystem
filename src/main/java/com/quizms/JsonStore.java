package com.quizms;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Thin wrapper around {@code json-simple} that knows how to read and write the
 * {@code users.json} and {@code quiz.json} files used by the application.
 *
 * <p>Centralising IO here keeps the rest of the codebase free of JSON parsing
 * details and makes it trivial to swap to a different storage format later.</p>
 */
public final class JsonStore {

    private final JSONParser parser = new JSONParser();

    public List<User> readUsers(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            List<User> users = new ArrayList<>();
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                String username = (String) json.get("username");
                String password = (String) json.get("password");
                String role = (String) json.get("role");
                users.add(new User(username, password, role));
            }
            return users;
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Could not read users from " + file, e);
        }
    }

    public List<Question> readQuestions(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            List<Question> questions = new ArrayList<>();
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                String question = (String) json.get("question");
                String opt1 = (String) json.get("option 1");
                String opt2 = (String) json.get("option 2");
                String opt3 = (String) json.get("option 3");
                String opt4 = (String) json.get("option 4");
                long key = (long) json.get("answerkey");
                questions.add(new Question(question, opt1, opt2, opt3, opt4, (int) key));
            }
            return questions;
        } catch (IOException | ParseException e) {
            throw new IllegalStateException("Could not read questions from " + file, e);
        }
    }

    public void writeQuestions(Path file, List<Question> questions) {
        JSONArray array = new JSONArray();
        for (Question q : questions) {
            JSONObject json = new JSONObject();
            json.put("question", q.getQuestion());
            json.put("option 1", q.getOption1());
            json.put("option 2", q.getOption2());
            json.put("option 3", q.getOption3());
            json.put("option 4", q.getOption4());
            json.put("answerkey", q.getAnswerKey());
            array.add(json);
        }
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            array.writeJSONString(writer);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new IllegalStateException("Could not write questions to " + file, e);
        }
    }
}
