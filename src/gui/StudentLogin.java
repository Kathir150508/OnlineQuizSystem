package gui;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.StudentDAO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import model.Question;
import model.Quiz;
import model.Student;

public class StudentLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private StudentDAO studentDAO = new StudentDAO();

    private JTextField txtName, txtEmail;
    private JComboBox<Quiz> cmbQuiz;
    private JLabel lblDetails;

    public StudentLogin() {
        setTitle("Student - Start a Quiz");
        setSize(500, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));

        txtName = new JTextField();
        txtEmail = new JTextField();
        cmbQuiz = new JComboBox<>();
        lblDetails = new JLabel(" ", SwingConstants.CENTER);

        form.add(new JLabel("Your Name:"));
        form.add(txtName);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Choose Quiz:"));
        form.add(cmbQuiz);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnStart = new JButton("Start Quiz");
        JButton btnHistory = new JButton("My Score History");
        JButton btnClose = new JButton("Close");
        buttons.add(btnStart);
        buttons.add(btnHistory);
        buttons.add(btnClose);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblDetails, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        cmbQuiz.addActionListener(e -> showDetails());
        btnStart.addActionListener(e -> startQuiz());
        btnHistory.addActionListener(e -> showHistory());
        btnClose.addActionListener(e -> dispose());

        // Pick up quizzes the admin added or changed while this window was open
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
            }
            return;
        }

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
        if (cmbQuiz.getItemCount() == 0) {
            lblDetails.setText("No quizzes available yet. Ask the admin to add one.");
        } else {
            showDetails();
        }
    }

    private void showDetails() {
        Quiz quiz = (Quiz) cmbQuiz.getSelectedItem();
        if (quiz == null) return;
        lblDetails.setText(quiz.getCategory() + "  |  " + quiz.getQuestionCount()
            + " questions  |  " + quiz.getDurationMinutes() + " min");
    }

    private boolean validName() {
        return !txtName.getText().trim().isEmpty();
    }

    private boolean validEmail() {
        return txtEmail.getText().trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    private void startQuiz() {
        if (!validName()) {
            Msg.error(this, "Please enter your name.");
            return;
        }
        if (txtName.getText().trim().length() > 100) {
            Msg.error(this, "Name must be at most 100 characters.");
            return;
        }
        if (!validEmail()) {
            Msg.error(this, "Please enter a valid email address.");
            return;
        }
        if (txtEmail.getText().trim().length() > 150) {
            Msg.error(this, "Email must be at most 150 characters.");
            return;
        }
        Quiz quiz = (Quiz) cmbQuiz.getSelectedItem();
        if (quiz == null) {
            Msg.error(this, "No quiz is available.");
            return;
        }

        try {
            List<Question> questions = questionDAO.getByQuiz(quiz.getQuizId());
            if (questions.isEmpty()) {
                Msg.error(this, "This quiz has no questions yet. Please choose another quiz.");
                return;
            }
            Student student = studentDAO.findOrCreate(txtName.getText().trim(), txtEmail.getText().trim().toLowerCase());
            new QuizWindow(student, quiz, questions).setVisible(true);
            dispose();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void showHistory() {
        if (!validEmail()) {
            Msg.error(this, "Enter the email you used for your quizzes.");
            return;
        }
        try {
            Student student = studentDAO.findByEmail(txtEmail.getText().trim().toLowerCase());
            if (student == null) {
                Msg.info(this, "No attempts found for this email.");
                return;
            }
            new ScoreHistory(student).setVisible(true);
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }
}
