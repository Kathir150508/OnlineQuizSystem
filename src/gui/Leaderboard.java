package gui;

import dao.QuizDAO;
import dao.ResultDAO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Quiz;
import model.QuizResult;

public class Leaderboard extends JFrame {

    private static final long serialVersionUID = 1L;

    private ResultDAO resultDAO = new ResultDAO();
    private QuizDAO quizDAO = new QuizDAO();
    private DefaultTableModel tableModel;
    private JComboBox<Quiz> cmbQuiz;
    private JLabel lblInfo;
    private boolean loading = false;

    public Leaderboard() {
        setTitle("Leaderboard - Top 10");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cmbQuiz = new JComboBox<>();
        lblInfo = new JLabel(" ");
        top.add(new JLabel("Quiz:"));
        top.add(cmbQuiz);
        top.add(lblInfo);
        add(top, BorderLayout.NORTH);

        String[] columns = {"Rank", "Student", "Score", "Percentage", "Time", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
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

        cmbQuiz.addActionListener(e -> {
            if (!loading) loadRows(false);
        });

        // Show new attempts and new quizzes when the window is brought back to the front
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                loadQuizzes(true);
            }
        });

        loadQuizzes(false);
    }

    // quiet = true is used for the automatic refresh, which must not pop up error dialogs
    private void loadQuizzes(boolean quiet) {
        Quiz previous = (Quiz) cmbQuiz.getSelectedItem();
        List<Quiz> quizzes;
        try {
            quizzes = quizDAO.getAllQuizzes();
        } catch (SQLException e) {
            if (!quiet) {
                Msg.error(this, e);
                lblInfo.setText("No quizzes available, so there are no attempts to show.");
            }
            return;
        }

        loading = true;
        cmbQuiz.removeAllItems();
        for (Quiz q : quizzes) {
            cmbQuiz.addItem(q);
        }
        if (previous != null) {
            for (int i = 0; i < cmbQuiz.getItemCount(); i++) {
                if (cmbQuiz.getItemAt(i).getQuizId() == previous.getQuizId()) {
                    cmbQuiz.setSelectedIndex(i);
                }
            }
        }
        loading = false;
        loadRows(quiet);
    }

    private void loadRows(boolean quiet) {
        Quiz quiz = (Quiz) cmbQuiz.getSelectedItem();
        if (quiz == null) {
            tableModel.setRowCount(0);
            lblInfo.setText("No quizzes available, so there are no attempts to show.");
            return;
        }

        List<QuizResult> results;
        try {
            results = resultDAO.getLeaderboard(quiz.getQuizId());
        } catch (SQLException e) {
            if (!quiet) {
                Msg.error(this, e);
            }
            lblInfo.setText("Could not load the leaderboard.");
            return;
        }

        tableModel.setRowCount(0);
        int rank = 1;
        for (QuizResult r : results) {
            tableModel.addRow(new Object[]{
                rank++, r.getStudentName(), r.getScore() + " / " + r.getTotalQuestions(),
                r.getPercentage() + "%", QuizResult.formatTime(r.getTimeTakenSec()), r.getAttemptedAt()
            });
        }
        lblInfo.setText(results.isEmpty() ? "No attempts yet for this quiz." : " ");
    }
}
