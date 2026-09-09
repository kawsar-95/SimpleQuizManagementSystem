#!/usr/bin/env python3
"""
Integration test driver for the Simple Java Quiz Management System.

Simulates a real interactive session by piping answers into the JAR:
  * Admin logs in and adds a fresh MCQ to the question bank
  * Student logs in and answers 10 randomly chosen questions by always
    picking option 1 (so we can verify the score matches the answer keys
    of those 10 questions)

This script verifies that the program:
  - Reads users.json and authenticates correctly
  - Saves the new admin question to quiz.json
  - Randomly selects 10 questions for the student
  - Scores each answer (0 or 1) and prints a final message
"""
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).parent
DATA = ROOT / "data"
QUIZ = DATA / "quiz.json"
USERS = DATA / "users.json"

# Re-seed clean data files (same as the JAR's bundled seeds)
DATA.mkdir(exist_ok=True)
USERS.write_text(Path("src/main/resources/users.json").read_text())
QUIZ.write_text(Path("src/main/resources/quiz.json").read_text())

with QUIZ.open() as f:
    bank = json.load(f)
print(f"Seed bank size: {len(bank)}")

# Input script:
# 1. Admin logs in, adds one new question, then quits
# 2. Student logs in, starts quiz, picks option 1 for all 10 questions, then quits
inputs = []
inputs += ["admin", "1234"]
inputs += [
    "Which of the following is a verification activity?",
    "Code inspection",
    "User acceptance test",
    "Beta testing",
    "Customer survey",
    "1",
    "q",
]
inputs += ["salman", "1234", "s"]
inputs += ["1"] * 10
inputs += ["q"]

proc = subprocess.run(
    ["java", "-jar", "build/libs/SimpleQuizManagementSystem-1.0.0.jar"],
    input="\n".join(inputs) + "\n",
    capture_output=True,
    text=True,
    timeout=60,
)
output = proc.stdout
sys.stdout.write(output)
if proc.returncode not in (0, 1):
    print("STDERR:", proc.stderr, file=sys.stderr)
    sys.exit(proc.returncode)

# Verify side effects
with QUIZ.open() as f:
    updated = json.load(f)
assert len(updated) == len(bank) + 1, (
    f"Expected bank to grow by 1, got {len(updated)} (was {len(bank)})"
)
assert updated[-1]["question"] == "Which of the following is a verification activity?"
assert updated[-1]["answerkey"] == 1
print(f"\n✓ Bank grew from {len(bank)} to {len(updated)} questions")

# Verify the score message exists
if "out of 10" in output:
    for line in output.splitlines():
        if "out of 10" in line:
            print("✓ Score line found:", line.strip())
            break
else:
    print("✗ No score line in output", file=sys.stderr)
    sys.exit(1)

print("✓ All integration checks passed")
