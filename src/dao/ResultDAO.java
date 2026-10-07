package dao;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.QuizResult;
import util.DataBaseHelper;

public class ResultDAO {

    public double save(int quizId, int studentId, int score, int total, int timeTakenSec) throws SQLException {
        double percentage = QuizResult.percentage(score, total);
        String sql = "INSERT INTO quiz_results (quiz_id, student_id, score, total_questions, percentage, time_taken_sec) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, score);
            pstmt.setInt(4, total);
            pstmt.setDouble(5, percentage);
            pstmt.setInt(6, timeTakenSec);
            pstmt.executeUpdate();
        }
        return percentage;
    }

    private QuizResult readRow(ResultSet rs) throws SQLException {
        String date = rs.getTimestamp("attempted_at").toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return new QuizResult(
            rs.getString("name"),
            rs.getString("title"),
            rs.getInt("score"),
            rs.getInt("total_questions"),
            rs.getDouble("percentage"),
            rs.getInt("time_taken_sec"),
            date);
    }

    // Best percentage first, then the faster attempt
    public List<QuizResult> getLeaderboard(int quizId) throws SQLException {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT s.name, q.title, r.score, r.total_questions, r.percentage, r.time_taken_sec, r.attempted_at "
                   + "FROM quiz_results r "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN quizzes q ON q.quiz_id = r.quiz_id "
                   + "WHERE r.quiz_id=? "
                   + "ORDER BY r.percentage DESC, r.time_taken_sec ASC, r.result_id ASC FETCH FIRST 10 ROWS ONLY";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(readRow(rs));
                }
            }
        }
        return results;
    }

    public List<QuizResult> getByStudent(int studentId) throws SQLException {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT s.name, q.title, r.score, r.total_questions, r.percentage, r.time_taken_sec, r.attempted_at "
                   + "FROM quiz_results r "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN quizzes q ON q.quiz_id = r.quiz_id "
                   + "WHERE r.student_id=? "
                   + "ORDER BY r.result_id DESC";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(readRow(rs));
                }
            }
        }
        return results;
    }

    public int countAttempts() throws SQLException {
        return DaoUtil.count("SELECT COUNT(*) FROM quiz_results");
    }
}
