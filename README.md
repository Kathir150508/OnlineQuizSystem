# Online Quiz Management System 📝

A full-stack, desktop-based Online Quiz Management System built with **Java (Swing)** and an **Oracle SQL Database**. This application provides an administrative interface to seamlessly manage quiz questions and an interactive portal for students to take tests, evaluate answers, and view their final scores.

## 🚀 Features

* **Comprehensive Question Management (CRUD):** Allows administrators to dynamically add, view, search, modify, and delete quiz questions.
* **Interactive Student Portal:** Provides a user-friendly graphical interface for students to attend quizzes in real-time.
* **Automated Evaluation:** Instantly processes student submissions, cross-references correct answers, and calculates the final score.
* **Robust Data Storage:** Utilizes JDBC to securely store and retrieve all questions, multiple-choice options, and quiz-related information from a relational database.

## 🛠️ Tech Stack

* **Frontend:** Java Swing (AWT, JFrame, JPanel, JTable)
* **Backend:** Java (JDK) implementing the DAO (Data Access Object) design pattern
* **Database:** Oracle Database (SQL)
* **Connectivity:** JDBC (`ojdbc` driver)
* **Version Control:** Git / GitHub

## 🗄️ Database Architecture

The system utilizes a clean relational database schema to maintain quiz integrity:
1. `questions`: Stores the question text, multiple-choice options (A, B, C, D), and the correct answer key.
2. `quiz_results`: Logs the student details, timestamp, and their final evaluated quiz scores.

## ⚙️ Setup & Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/Kathir150508/OnlineQuizSystem.git](https://github.com/Kathir150508/OnlineQuizSystem.git)
