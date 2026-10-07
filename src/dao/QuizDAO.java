package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Quiz;
import util.DataBaseHelper;

public class QuizDAO {

    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.quiz_id, q.title, q.category, q.duration_minutes, "
                   + "COUNT(qs.question_id) AS question_count "
                   + "FROM quizzes q LEFT JOIN questions qs ON qs.quiz_id = q.quiz_id "
                   + "GROUP BY q.quiz_id, q.title, q.category, q.duration_minutes "
                   + "ORDER BY q.quiz_id";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                quizzes.add(new Quiz(
                    rs.getInt("quiz_id"),
                    rs.getString("title"),
                    rs.getString("category"),
                    rs.getInt("duration_minutes"),
                    rs.getInt("question_count")));
            }
        }
        return quizzes;
    }

    public void addQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, category, duration_minutes) VALUES (?, ?, ?)";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getCategory());
            pstmt.setInt(3, quiz.getDurationMinutes());
            pstmt.executeUpdate();
        }
    }

    public boolean updateQuiz(Quiz quiz) throws SQLException {
        String sql = "UPDATE quizzes SET title=?, category=?, duration_minutes=? WHERE quiz_id=?";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getCategory());
            pstmt.setInt(3, quiz.getDurationMinutes());
            pstmt.setInt(4, quiz.getQuizId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Questions and results of this quiz are removed too (ON DELETE CASCADE)
    public boolean deleteQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE quiz_id=?";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int countQuizzes() throws SQLException {
        return DaoUtil.count("SELECT COUNT(*) FROM quizzes");
    }
}
