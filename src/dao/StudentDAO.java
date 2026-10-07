package dao;

import java.sql.*;
import model.Student;
import util.DataBaseHelper;

public class StudentDAO {

    // Returns the existing student with this email, or registers a new one
    public Student findOrCreate(String name, String email) throws SQLException {
        Student existing = findByEmail(email);
        if (existing != null) {
            return existing;
        }

        String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Another window may have registered the same email a moment ago
            try {
                Student other = findByEmail(email);
                if (other != null) {
                    return other;
                }
            } catch (SQLException again) {
                e.addSuppressed(again);
            }
            throw e;
        }

        // The email is unique, so reading it back gives the new row and its id
        Student created = findByEmail(email);
        if (created == null) {
            throw new SQLException("The student was saved but could not be read back.");
        }
        return created;
    }

    // Only looks up, never creates. Returns null when the email is not registered.
    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT student_id, name, email FROM students WHERE email=?";
        try (Connection conn = DataBaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(rs.getInt("student_id"), rs.getString("name"), rs.getString("email"));
                }
            }
        }
        return null;
    }
}
