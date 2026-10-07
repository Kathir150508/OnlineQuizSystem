package gui;

import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {

    private static final long serialVersionUID = 1L;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        QuizManagerPanel quizPanel = new QuizManagerPanel();
        QuestionManagerPanel questionPanel = new QuestionManagerPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Quizzes", quizPanel);
        tabs.addTab("Questions", questionPanel);

        // Reload data when switching tabs so both tabs always show current data
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == questionPanel) {
                questionPanel.loadQuizzes();
            } else {
                quizPanel.refresh();
            }
        });

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }
}
