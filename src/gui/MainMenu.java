package gui;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.ResultDAO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.*;
import util.DataBaseHelper;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;

    private JLabel lblQuizzes, lblQuestions, lblAttempts, lblStatus;

    public MainMenu() {
        setTitle("Online Quiz Management System");
        setSize(560, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Header and stats ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel lblTitle = new JLabel("Online Quiz Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 10));
        stats.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        lblQuizzes = statLabel();
        lblQuestions = statLabel();
        lblAttempts = statLabel();
        stats.add(lblQuizzes);
        stats.add(lblQuestions);
        stats.add(lblAttempts);
        header.add(stats, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // --- Buttons ---
        JPanel buttons = new JPanel(new GridLayout(4, 1, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 120, 10, 120));
        JButton btnAdmin = new JButton("Admin Dashboard");
        JButton btnStudent = new JButton("Student - Take a Quiz");
        JButton btnLeaderboard = new JButton("Leaderboard");
        JButton btnExit = new JButton("Exit");
        buttons.add(btnAdmin);
        buttons.add(btnStudent);
        buttons.add(btnLeaderboard);
        buttons.add(btnExit);
        add(buttons, BorderLayout.CENTER);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);
        add(lblStatus, BorderLayout.SOUTH);

        btnAdmin.addActionListener(e -> openAdmin());
        btnStudent.addActionListener(e -> new StudentLogin().setVisible(true));
        btnLeaderboard.addActionListener(e -> new Leaderboard().setVisible(true));
        btnExit.addActionListener(e -> System.exit(0));

        // Refresh the numbers every time this window comes back into focus
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refreshStats();
            }
        });
    }

    private JLabel statLabel() {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        label.setPreferredSize(new Dimension(100, 60));
        return label;
    }

    private void refreshStats() {
        try {
            lblQuizzes.setText("<html><center><b style='font-size:18px'>" + new QuizDAO().countQuizzes() + "</b><br>Quizzes</center></html>");
            lblQuestions.setText("<html><center><b style='font-size:18px'>" + new QuestionDAO().countQuestions() + "</b><br>Questions</center></html>");
            lblAttempts.setText("<html><center><b style='font-size:18px'>" + new ResultDAO().countAttempts() + "</b><br>Attempts</center></html>");
            lblStatus.setText(" ");
        } catch (SQLException e) {
            lblQuizzes.setText("-");
            lblQuestions.setText("-");
            lblAttempts.setText("-");
            lblStatus.setText("Cannot reach the database. Check config.properties and that Oracle is running.");
        }
    }

    private void openAdmin() {
        JPasswordField pin = new JPasswordField();
        int choice = JOptionPane.showConfirmDialog(this, pin, "Enter admin PIN",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        if (new String(pin.getPassword()).equals(DataBaseHelper.getAdminPin())) {
            new AdminDashboard().setVisible(true);
        } else {
            Msg.error(this, "Wrong PIN.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
