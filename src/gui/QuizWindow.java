package gui;

import dao.ResultDAO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import model.Question;
import model.Quiz;
import model.Student;

public class QuizWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private Student student;
    private Quiz quiz;
    private List<Question> questions;
    private String[] answers;
    private int current = 0;
    private int totalSeconds;
    private int secondsLeft;
    private long startMillis;
    private long endMillis;
    private boolean finished = false;
    private Timer timer;

    private JLabel lblProgress, lblTimer, lblAnswered;
    private JTextArea txtQuestion;
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup group = new ButtonGroup();
    private JButton btnPrev, btnNext, btnSubmit;

    public QuizWindow(Student student, Quiz quiz, List<Question> questionList) {
        this.student = student;
        this.quiz = quiz;

        // Shuffle a copy so every attempt has a different order
        this.questions = new ArrayList<>(questionList);
        Collections.shuffle(this.questions);
        this.answers = new String[questions.size()];

        // The countdown follows the clock (not a tick count), so it stays accurate even if
        // the computer is busy or goes to sleep
        totalSeconds = quiz.getDurationMinutes() * 60;
        secondsLeft = totalSeconds;
        startMillis = System.currentTimeMillis();
        endMillis = startMillis + totalSeconds * 1000L;

        setTitle(quiz.getTitle() + " - " + student.getName());
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Top: progress and timer ---
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        lblProgress = new JLabel();
        lblProgress.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTimer = new JLabel();
        lblTimer.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblProgress, BorderLayout.WEST);
        top.add(lblTimer, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // --- Center: question and options ---
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        txtQuestion = new JTextArea(3, 40);
        txtQuestion.setEditable(false);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtQuestion.setBackground(getBackground());
        center.add(txtQuestion, BorderLayout.NORTH);

        JPanel optionPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            group.add(options[i]);
            optionPanel.add(options[i]);

            final String letter = String.valueOf((char) ('A' + i));
            options[i].addActionListener(e -> {
                answers[current] = letter;
                updateAnsweredLabel();
            });
        }
        center.add(optionPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // --- Bottom: navigation ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        lblAnswered = new JLabel();
        JPanel buttons = new JPanel(new FlowLayout());
        btnPrev = new JButton("Previous");
        btnNext = new JButton("Next");
        btnSubmit = new JButton("Submit Quiz");
        buttons.add(btnPrev);
        buttons.add(btnNext);
        buttons.add(btnSubmit);
        bottom.add(lblAnswered, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        btnPrev.addActionListener(e -> {
            current--;
            showQuestion();
        });
        btnNext.addActionListener(e -> {
            current++;
            showQuestion();
        });
        btnSubmit.addActionListener(e -> finish(false));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (finished) return;
                int choice = JOptionPane.showConfirmDialog(QuizWindow.this,
                    "Leave the quiz? Your answers will not be saved.",
                    "Leave quiz", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION && !finished) {
                    finished = true;
                    timer.stop();
                    dispose();
                }
            }

            // Whatever closes the window, the countdown must not keep running
            @Override
            public void windowClosed(WindowEvent e) {
                timer.stop();
            }
        });

        timer = new Timer(250, e -> tick());

        updateTimerLabel();
        showQuestion();
        timer.start();
    }

    private void showQuestion() {
        Question q = questions.get(current);
        lblProgress.setText("Question " + (current + 1) + " of " + questions.size());
        txtQuestion.setText(q.getQuestionText());
        options[0].setText("A.  " + q.getOptionA());
        options[1].setText("B.  " + q.getOptionB());
        options[2].setText("C.  " + q.getOptionC());
        options[3].setText("D.  " + q.getOptionD());

        group.clearSelection();
        if (answers[current] != null) {
            options[answers[current].charAt(0) - 'A'].setSelected(true);
        }

        btnPrev.setEnabled(current > 0);
        btnNext.setEnabled(current < questions.size() - 1);
        updateAnsweredLabel();
    }

    private int countAnswered() {
        int count = 0;
        for (String a : answers) {
            if (a != null) count++;
        }
        return count;
    }

    private void updateAnsweredLabel() {
        lblAnswered.setText("Answered: " + countAnswered() + " / " + questions.size());
    }

    private void tick() {
        if (finished) return;
        long remainingMs = endMillis - System.currentTimeMillis();
        secondsLeft = (int) Math.max(0, (remainingMs + 999) / 1000);
        updateTimerLabel();
        if (remainingMs <= 0) {
            finish(true);
        }
    }

    private void updateTimerLabel() {
        int shown = Math.max(secondsLeft, 0);
        lblTimer.setText("Time left: " + String.format("%02d:%02d", shown / 60, shown % 60));
        lblTimer.setForeground(secondsLeft <= 30 ? Color.RED : Color.BLACK);
    }

    private void finish(boolean timeUp) {
        if (finished) return;
        long submittedAt = System.currentTimeMillis();

        if (!timeUp) {
            int unanswered = questions.size() - countAnswered();
            String message = unanswered > 0
                ? "You have " + unanswered + " unanswered question(s). Submit anyway?"
                : "Submit your answers?";
            int choice = JOptionPane.showConfirmDialog(this, message, "Submit quiz", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
            // The time may have run out while the question was open, and the quiz is already submitted
            if (finished) return;
        }

        finished = true;
        timer.stop();

        // Evaluate: one mark for each correct answer, unanswered counts as wrong
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectAnswer().equalsIgnoreCase(answers[i])) {
                score++;
            }
        }
        int total = questions.size();
        int timeTaken = timeUp ? totalSeconds : (int) Math.min(totalSeconds, (submittedAt - startMillis) / 1000);

        boolean saved = true;
        try {
            new ResultDAO().save(quiz.getQuizId(), student.getStudentId(), score, total, timeTaken);
        } catch (SQLException ex) {
            saved = false;
            Msg.error(this, ex);
        }

        dispose();
        if (timeUp) {
            Msg.info(null, "Time is up! Your quiz was submitted automatically.");
        }
        new ResultDialog(student.getName(), quiz.getTitle(), score, total, timeTaken, saved).setVisible(true);
    }
}
