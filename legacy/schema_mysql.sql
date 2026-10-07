CREATE DATABASE IF NOT EXISTS quiz_db;
USE quiz_db;

CREATE TABLE IF NOT EXISTS quizzes (
    quiz_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes >= 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    option_a VARCHAR(200) NOT NULL,
    option_b VARCHAR(200) NOT NULL,
    option_c VARCHAR(200) NOT NULL,
    option_d VARCHAR(200) NOT NULL,
    correct_answer CHAR(1) NOT NULL CHECK (correct_answer IN ('A','B','C','D')),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS quiz_results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    student_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    time_taken_sec INT NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Sample data (run this file only once)
INSERT INTO quizzes (title, category, duration_minutes) VALUES
('Java Basics', 'Programming', 5),
('General Knowledge - India', 'General', 5);

INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
(1, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B'),
(1, 'Which of these is not a primitive data type?', 'int', 'boolean', 'String', 'char', 'C'),
(1, 'What is the size of an int in Java?', '8 bits', '16 bits', '32 bits', '64 bits', 'C'),
(1, 'Which method is the entry point of a Java program?', 'start()', 'main()', 'run()', 'init()', 'B'),
(1, 'Which JDBC interface is used to run a parameterised SQL query?', 'Statement', 'PreparedStatement', 'ResultSet', 'DriverManager', 'B'),
(2, 'What is the capital of India?', 'Mumbai', 'Kolkata', 'New Delhi', 'Chennai', 'C'),
(2, 'Who wrote the Indian national anthem?', 'Bankim Chandra Chatterjee', 'Rabindranath Tagore', 'Sarojini Naidu', 'Mahatma Gandhi', 'B'),
(2, 'On which date is Republic Day celebrated in India?', '15 August', '26 January', '2 October', '14 November', 'B'),
(2, 'Which is the largest Indian state by area?', 'Madhya Pradesh', 'Maharashtra', 'Rajasthan', 'Uttar Pradesh', 'C'),
(2, 'In which year did the Constitution of India come into effect?', '1947', '1948', '1950', '1952', 'C');
