package dao;

import util.DataBaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public static class QuizResult {
        private String studentName;
        private int score;
        private int totalQuestions;
        private String quizDate;

        public QuizResult(String studentName, int score, int totalQuestions, String quizDate) {
            this.studentName = studentName;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.quizDate = quizDate;
        }

        public String getStudentName() { return studentName; }
        public int getScore() { return score; }
        public int getTotalQuestions() { return totalQuestions; }
        public String getQuizDate() { return quizDate; }
    }

    public List<QuizResult> getAllResults() {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT student_name, score, total_questions, quiz_date FROM quiz_results ORDER BY result_id DESC";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                results.add(new QuizResult(
                    rs.getString("student_name"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getString("quiz_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quiz results.");
            e.printStackTrace();
        }
        return results;
    }
}