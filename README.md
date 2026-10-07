# Online Quiz Management System (Oracle edition)

Java Swing desktop application using JDBC and Oracle Database.
This is Version 2 migrated from MySQL to Oracle. Features and screens are unchanged.

Admins create quizzes and manage their questions. Students register, pick a saved quiz,
answer against a countdown timer, and see their score. Every attempt is stored in Oracle.

## Features

Admin (protected by a PIN)
- Add, edit, delete quizzes (title, category, duration)
- Add, view, search, edit, delete questions inside a quiz
- Deleting a quiz also deletes its questions and results

Student
- Register with name and email (the same email is recognised next time)
- Choose a saved quiz, answer with A-D options, move Previous / Next
- Countdown timer, auto-submit when time runs out
- Score, percentage, grade and time taken, saved to the database
- Certificate (PNG) for a score of 80% or more
- Personal score history

Everyone
- Home screen with live counts, leaderboard (top 10 per quiz)

Grades: 80%+ Excellent, 60-79% Good, 40-59% Average, below 40% Needs Improvement.

## Project structure

```
src/model   Quiz, Question, Student, QuizResult
src/util    DataBaseHelper (reads config.properties), CertificateGenerator
src/dao     QuizDAO, QuestionDAO, StudentDAO, ResultDAO (all JDBC code)
src/gui     MainMenu (start here), AdminDashboard, StudentLogin, QuizWindow, ...
schema_oracle.sql   creates the tables, constraints, indexes and sample quizzes
config.properties   connection settings and admin PIN (not committed to git)
lib/                put ojdbc17.jar here (and nothing else)
legacy/             the old MySQL script, kept for reference only
```

## Database setup (Oracle 26ai Free, service FREEPDB1)

1. Make sure the user KRUTHIKA can create tables. If it cannot, connect as SYSTEM to FREEPDB1 and run:
   ```sql
   GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO KRUTHIKA;
   ALTER USER KRUTHIKA QUOTA UNLIMITED ON USERS;
   ```
   (Use the tablespace that is KRUTHIKA's default if it is not USERS.)
2. Connect as KRUTHIKA and run the script once:
   - SQL Developer: open `schema_oracle.sql` and press Run Script (F5)
   - SQL*Plus: `sqlplus KRUTHIKA@localhost:1521/FREEPDB1` then `@schema_oracle.sql`
3. Check: `SELECT COUNT(*) FROM questions;` should return 10.

## Application setup

1. Copy `ojdbc17.jar` into the `lib` folder. Use the jar you already have; do not add MySQL Connector/J.
2. Open `config.properties` and set the password of the KRUTHIKA Oracle user:
   ```
   db.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
   db.user=KRUTHIKA
   db.password=your_oracle_password
   admin.pin=1234
   ```
   The URL uses the service name (`/FREEPDB1`). Do not use `:xe`, that SID does not exist here.

## Run

Windows: double-click `run.bat`    Mac / Linux: `sh run.sh`

Start from the project folder, because `config.properties` is read from there.

### Using Eclipse
1. Remove any old jar entries first: right-click the project > Build Path > Configure Build Path > Libraries,
   delete every MySQL or broken ojdbc entry (red cross).
2. Add External Archives (or Add JARs from `lib`) > `ojdbc17.jar`. Add it only once.
3. Run `gui.MainMenu`. The working directory must be the project folder (Eclipse default).

## What changed from the MySQL version

- `config.properties`: Oracle URL, user KRUTHIKA, password placeholder
- `DataBaseHelper`: loads `oracle.jdbc.OracleDriver`, clear message if the jar is missing
- `ResultDAO`: `LIMIT 10` became `FETCH FIRST 10 ROWS ONLY`
- `StudentDAO`: after inserting a student the row is read back by its unique email, so nothing depends on
  database-specific generated-key behaviour (Oracle would hand back a ROWID)
- `QuestionDAO`: search uses `UPPER(...) LIKE UPPER(?)` so it stays case-insensitive like before,
  and `%` or `_` typed by the user are searched literally
- `MainMenu` and `Msg`: Oracle wording, plus hints for the common ORA-01017, ORA-12514 and ORA-00942 errors
- New `schema_oracle.sql`: identity columns, `VARCHAR2`/`NUMBER`/`TIMESTAMP`, same keys and cascades,
  and indexes on the foreign keys (Oracle does not create these automatically)

## Oracle first-run checklist

- Home screen shows 2 quizzes, 10 questions, 0 attempts (no red message at the bottom)
- Admin PIN opens the dashboard; add, edit, delete a quiz; add, search (try lowercase), edit, delete a question
- Student: register a new email, finish a quiz, see the result, then check `SELECT * FROM quiz_results;`
- Register a student, then run `SELECT * FROM students;`: the new row appears with an id (checks the read-back)
- Search for a word in lowercase, then search for `%`: only questions that contain a literal % are listed
- Leaderboard and My Score History show the attempt with the right date and time
- Certificate: score 80% or more, save the PNG
- Delete a quiz that has results: its questions and results disappear

## Input limits

Forms reject text that is too long before it reaches the database: quiz title 150, category 80,
question 500, each option 200, student name 100, email 150 characters.

## Notes

- Students are identified by name and email only. There is no student password.
- Admin access is the single PIN in `config.properties`.
- The leaderboard lists the 10 best attempts, so one student can appear more than once.

- Correct answers are loaded onto the student's computer while a quiz runs. Fine for a desktop
  project, not secure enough for a real exam.
- Do not commit `config.properties`; it holds your password (it is in `.gitignore`).
