package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Question;
import util.DataBaseHelper;

public class QuestionDAO {

    private Question readRow(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("question_id"),
            rs.getInt("quiz_id"),
            rs.getString("question_text"),
            rs.getString("option_a"),
            rs.getString("option_b"),
            rs.getString("option_c"),
            rs.getString("option_d"),
            rs.getString("correct_answer"));
    }

    public List<Question> getByQuiz(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id=? ORDER BY question_id";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(readRow(rs));
                }
            }
        }
        return questions;
    }

    private static String condition(String column) {
        return "UPPER(" + column + ") LIKE UPPER(?) ESCAPE '\\'";
    }

    // Turns the typed text into a LIKE pattern. % and _ typed by the user are searched literally.
    public static String likePattern(String keyword) {
        String escaped = keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + escaped + "%";
    }

    // Looks for the keyword in the question text and in all four options (case-insensitive)
    public List<Question> search(int quizId, String keyword) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id=? AND ("
                   + condition("question_text") + " OR " + condition("option_a") + " OR "
                   + condition("option_b") + " OR " + condition("option_c") + " OR "
                   + condition("option_d") + ") ORDER BY question_id";
        String pattern = likePattern(keyword);

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            for (int i = 2; i <= 6; i++) {
                pstmt.setString(i, pattern);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(readRow(rs));
                }
            }
        }
        return questions;
    }

    public void addQuestion(Question q) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, q.getQuizId());
            pstmt.setString(2, q.getQuestionText());
            pstmt.setString(3, q.getOptionA());
            pstmt.setString(4, q.getOptionB());
            pstmt.setString(5, q.getOptionC());
            pstmt.setString(6, q.getOptionD());
            pstmt.setString(7, q.getCorrectAnswer());
            pstmt.executeUpdate();
        }
    }

    public boolean updateQuestion(Question q) throws SQLException {
        String sql = "UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_answer=? "
                   + "WHERE question_id=?";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q.getQuestionText());
            pstmt.setString(2, q.getOptionA());
            pstmt.setString(3, q.getOptionB());
            pstmt.setString(4, q.getOptionC());
            pstmt.setString(5, q.getOptionD());
            pstmt.setString(6, q.getCorrectAnswer());
            pstmt.setInt(7, q.getQuestionId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id=?";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int countQuestions() throws SQLException {
        return DaoUtil.count("SELECT COUNT(*) FROM questions");
    }
}
