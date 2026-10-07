package gui;

import dao.ResultDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.QuizResult;
import model.Student;

public class ScoreHistory extends JFrame {

    private static final long serialVersionUID = 1L;

    public ScoreHistory(Student student) {
        setTitle("Score History - " + student.getName());
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblInfo = new JLabel(" ", SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblInfo, BorderLayout.NORTH);

        String[] columns = {"Quiz", "Score", "Percentage", "Grade", "Time", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        try {
            java.util.List<QuizResult> results = new ResultDAO().getByStudent(student.getStudentId());
            for (QuizResult r : results) {
                tableModel.addRow(new Object[]{
                    r.getQuizTitle(), r.getScore() + " / " + r.getTotalQuestions(), r.getPercentage() + "%",
                    QuizResult.grade(r.getPercentage()), QuizResult.formatTime(r.getTimeTakenSec()), r.getAttemptedAt()
                });
            }
            lblInfo.setText(results.isEmpty() ? "No attempts yet." : " ");
        } catch (SQLException e) {
            lblInfo.setText("Could not load your attempts.");
            Msg.error(this, e);
        }
    }
}
