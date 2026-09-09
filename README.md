# Simple Java Quiz Management System

A console-based Java SE 17 application that lets an **admin** populate a bank
of multiple-choice SQA questions and lets a **student** take a randomised
10-question quiz from that bank. State is persisted to plain JSON files
(`users.json` and `quiz.json`) — no database required.

> **Course:** Java · **Batch:** 15 · **Topic:** Quiz Management System

---

## Features

* Username / password authentication backed by `users.json` (admin + student roles)
* Admin can keep adding MCQ questions to the bank until they press `q`
* Student takes 10 random questions, each scored 1 mark with no negative marking
* Final score is graded into four buckets: Excellent / Good / Very poor / Failed
* All persistence is plain JSON — easy to inspect and edit by hand
* Bundled with 30 pre-loaded SQA questions so the system is usable on first run

---

## Project Structure

```
SimpleQuizManagementSystem/
├── build.gradle                          # Gradle build script (java + application plugins)
├── settings.gradle                       # Project name
├── gradlew                               # POSIX wrapper script
├── gradlew.bat                           # Windows wrapper script
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties     # Pins Gradle 8.10.2
├── LICENSE                               # MIT license
├── .gitignore                            # Ignores .idea, .gradle, build, ...
├── .gitattributes                        # Normalises line endings
├── README.md                             # ← you are here
├── integration_test.py                   # End-to-end driver (Python)
└── src/
    └── main/
        ├── java/com/quizms/              # Java sources (8 classes)
        │   ├── Main.java                 # Entry point
        │   ├── AppContext.java           # Locates users.json / quiz.json on disk
        │   ├── User.java                 # Immutable user record
        │   ├── Question.java             # Immutable question record
        │   ├── JsonStore.java            # Reads / writes JSON files
        │   ├── ScannerProvider.java      # Shared Scanner singleton over System.in
        │   ├── AuthService.java          # Login flow + credential check
        │   ├── AdminService.java         # Add-question loop
        │   └── StudentService.java       # Quiz + scoring + grading
        ├── resources/
        │   ├── users.json                # Seed credentials (admin / salman)
        │   └── quiz.json                 # Seed question bank (30 SQA MCQs)
        └── ../test/java/com/quizms/      # JUnit 5 unit tests
            ├── QuestionTest.java         # Tests the Question record
            ├── JsonStoreTest.java        # Round-trips JSON, schema & error paths
            └── SeedQuestionsTest.java    # Asserts ≥30 SQA questions & valid keys
```

At runtime the application creates a `data/` folder in the current working
directory and copies the bundled seed JSON files into it on first run. The
runtime copies are the ones that get read/written — the seed copies in
`src/main/resources/` are only used to bootstrap.

---

## Prerequisites

| Tool        | Version        | Why                                         |
| ----------- | -------------- | ------------------------------------------- |
| JDK         | 17 or newer    | Compiles the application                    |
| Gradle      | 8.10.2 (auto)  | Build tool — fetched automatically          |
| Git         | any recent     | Clone the repo                              |

Gradle is **not** required to be installed locally because the project ships a
`gradlew` wrapper that downloads the exact Gradle version declared in
`gradle/wrapper/gradle-wrapper.properties`.

---

## How to Build & Run

### Option A — Using the Gradle wrapper (recommended)

```bash
# from the project root
./gradlew clean build        # compiles + builds build/libs/SimpleQuizManagementSystem-1.0.0.jar
./gradlew run                # compiles and launches the program
```

### Option B — Build a fat JAR and run it directly

```bash
./gradlew clean build
java -jar build/libs/SimpleQuizManagementSystem-1.0.0.jar
```

### Option C — Plain `javac` / `java` (no Gradle)

```bash
# Download json-simple once
curl -O https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar

mkdir -p build/classes build/resources/main
cp src/main/resources/*.json build/resources/main/
javac -encoding UTF-8 -cp json-simple-1.1.1.jar -d build/classes $(find src/main/java -name '*.java')
java  -cp build/resources/main:build/classes:json-simple-1.1.1.jar com.quizms.Main
```

---

## Sample Walkthrough

```
System:> Using data directory: /.../data
System:> Enter your username
User:> admin
System:> Enter password
User:> 1234
System:> Welcome admin! Please create new questions in the question bank.

System:> Input your question
Admin:> Which is not part of system testing?
System:> Input option 1:
Admin:> Regression Testing
... (option 2 / 3 / 4)
System:> What is the answer key? (1-4)
Admin:> 4
System:> Saved successfully! Do you want to add more questions? (press s for start and q for quit)
Admin:> q
System:> Question bank has 31 question(s). Returning to login.

System:> Enter your username
User:> salman
System:> Enter password
User:> 1234
System:> Welcome salman to the quiz! We will throw you 10 questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' for start.
Student:> s

[Question 1] <randomly chosen from the bank>
1. ...
2. ...
3. ...
4. ...

Student:> 2
... (9 more questions) ...
System:> Excellent! You have got 8 out of 10
System:> Would you like to start again? press s for start or q for quit
Student:> q
System:> Thanks for taking the quiz. Goodbye!
```

---

## Default Credentials

| Username | Password | Role    |
| -------- | -------- | ------- |
| `admin`  | `1234`   | admin   |
| `salman` | `1234`   | student |

You can add more users or change passwords by editing `data/users.json`. The
file is copied from `src/main/resources/users.json` on first run, after which
the runtime always reads the working copy.

---

## Question Bank Format

`data/quiz.json` is a JSON array of objects with this shape (matching the
assignment spec exactly):

```json
[
  {
    "question": "Which is not part of system testing?",
    "option 1": "Regression Testing",
    "option 2": "Sanity Testing",
    "option 3": "Load Testing",
    "option 4": "Unit Testing",
    "answerkey": 4
  }
]
```

* `answerkey` must be an integer in `[1, 4]`.
* Duplicates are allowed in principle, but adding more unique questions makes
  random sampling less likely to repeat them for a student.

---

## Scoring Rules

| Score | Verdict message                                    |
| ----- | -------------------------------------------------- |
| ≥ 8   | `Excellent! You have got X out of 10`              |
| 5–7   | `Good. You have got X out of 10`                   |
| 2–4   | `Very poor! You have got X out of 10`              |
| 0–1   | `Very sorry you are failed. You have got X out of 10` |

Each MCQ is worth 1 mark and there is no negative marking. An invalid
(non-1-4, non-numeric) input is scored as 0, the program prints the correct
answer key, and the next question is presented.

---

## Architecture Notes

* **Single shared `Scanner`** — `ScannerProvider` keeps one `Scanner` on
  `System.in`. Multiple scanners on the same stream tend to "skip" lines as
  each instance buffers independently; centralising it avoids that pitfall.
* **Layered services** — `AuthService`, `AdminService` and `StudentService`
  each focus on one user role; `JsonStore` is the only class that knows about
  JSON. Swapping to a database later is a localised change.
* **No secrets in memory longer than necessary** — `AuthService` returns an
  `AuthenticatedUser` value object that drops the password, so the rest of the
  app does not need to carry it around.

---

## Testing

### JUnit 5 unit tests

```bash
./gradlew test
```

The suite covers:

| Test class           | What it asserts                                                  |
| -------------------- | ---------------------------------------------------------------- |
| `QuestionTest`       | The `Question` record returns its constructor values              |
| `JsonStoreTest`      | Round-trips questions through JSON; missing/malformed files fail; output schema matches the assignment (`question`, `option 1`-`option 4`, `answerkey`) |
| `SeedQuestionsTest`  | The bundled `quiz.json` contains **≥ 30** questions, every answer key is in `[1, 4]`, and every option is non-blank |

All 9 tests pass on the reference machine.

### End-to-end driver (Python)

```bash
python3 integration_test.py
```

Re-seeds the data files, runs the fat JAR, simulates an admin adding a new
question and a student answering the quiz, then asserts that:

1. `data/quiz.json` grew by exactly one question after the admin flow.
2. The appended question matches what the admin typed.
3. A `... out of 10` score line is printed.

---

## License

This is a student project for Batch 15 of the Java course. Feel free to use
and adapt it for learning purposes.
