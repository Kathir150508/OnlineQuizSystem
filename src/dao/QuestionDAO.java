package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Question;
import util.DataBaseHelper;


public class QuestionDAO {

    // 1. READ: Fetch all questions from the database
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY question_id";

        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("question_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching questions.");
            e.printStackTrace();
        }
        return questions;
    }

    // 2. CREATE: Add a new question to the database
    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, q.getQuestionText());
            pstmt.setString(2, q.getOptionA());
            pstmt.setString(3, q.getOptionB());
            pstmt.setString(4, q.getOptionC());
            pstmt.setString(5, q.getOptionD());
            pstmt.setString(6, q.getCorrectAnswer());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding question.");
            e.printStackTrace();
            return false;
        }
    }

    // 3. UPDATE: Modify an existing question
    public boolean updateQuestion(Question q) {
        String sql = "UPDATE questions SET question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_answer=? WHERE question_id=?";
        
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
        } catch (SQLException e) {
            System.err.println("Error updating question.");
            e.printStackTrace();
            return false;
        }
    }

    // 4. DELETE: Remove a question from the database
    public boolean deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE question_id=?";
        
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question.");
            e.printStackTrace();
            return false;
        }
    }
}
