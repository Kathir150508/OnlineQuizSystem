package gui;

import dao.ResultDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel tableModel;

    public StudentDashboard() {
        setTitle("Student Scoreboard & Dashboard");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Label
        JLabel lblTitle = new JLabel("Quiz Score History", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Student Name", "Score", "Total Questions", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Close Button Panel
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);

        loadResults();
    }

    private void loadResults() {
        tableModel.setRowCount(0);
        ResultDAO resultDAO = new ResultDAO();
        List<ResultDAO.QuizResult> results = resultDAO.getAllResults();
        
        for (ResultDAO.QuizResult r : results) {
            tableModel.addRow(new Object[]{
                r.getStudentName(),
                r.getScore(),
                r.getTotalQuestions(),
                r.getQuizDate()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
}