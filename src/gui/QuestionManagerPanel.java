package gui;

import dao.QuestionDAO;
import dao.QuizDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Question;
import model.Quiz;

public class QuestionManagerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<Quiz> cmbQuiz;
    private JTextField txtSearch, txtId, txtQuestion, txtOptA, txtOptB, txtOptC, txtOptD;
    private JComboBox<String> cmbAnswer;
    private JLabel lblCount;
    private boolean loading = false;

    public QuestionManagerPanel() {
        setLayout(new BorderLayout());

        // --- Top: quiz picker and search ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cmbQuiz = new JComboBox<>();
        cmbQuiz.setPreferredSize(new Dimension(220, 26));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        lblCount = new JLabel("");

        searchBar.add(new JLabel("Quiz:"));
        searchBar.add(cmbQuiz);
        searchBar.add(new JLabel("Search:"));
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnShowAll);
        searchBar.add(lblCount);

        // --- Form ---
        txtId = new JTextField();
        txtId.setEditable(false);
        txtQuestion = new JTextField();
        txtOptA = new JTextField();
        txtOptB = new JTextField();
        txtOptC = new JTextField();
        txtOptD = new JTextField();
        cmbAnswer = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        JPanel idRow = new JPanel(new GridLayout(1, 4, 10, 0));
        idRow.add(new JLabel("ID (Auto):"));
        idRow.add(txtId);
        idRow.add(new JLabel("Correct Answer:"));
        idRow.add(cmbAnswer);

        JPanel questionRow = new JPanel(new BorderLayout(10, 0));
        JLabel lblQuestion = new JLabel("Question:");
        lblQuestion.setPreferredSize(new Dimension(90, 26));
        questionRow.add(lblQuestion, BorderLayout.WEST);
        questionRow.add(txtQuestion, BorderLayout.CENTER);

        JPanel optionRows = new JPanel(new GridLayout(2, 4, 10, 8));
        optionRows.add(new JLabel("Option A:"));
        optionRows.add(txtOptA);
        optionRows.add(new JLabel("Option B:"));
        optionRows.add(txtOptB);
        optionRows.add(new JLabel("Option C:"));
        optionRows.add(txtOptC);
        optionRows.add(new JLabel("Option D:"));
        optionRows.add(txtOptD);

        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        form.add(idRow, BorderLayout.NORTH);
        form.add(questionRow, BorderLayout.CENTER);
        form.add(optionRows, BorderLayout.SOUTH);

        JPanel north = new JPanel(new BorderLayout());
        north.add(searchBar, BorderLayout.NORTH);
        north.add(form, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        // --- Table ---
        String[] columns = {"ID", "Question", "A", "B", "C", "D", "Answer"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(380);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (e.getValueIsAdjusting() || row < 0) return;
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtQuestion.setText(tableModel.getValueAt(row, 1).toString());
            txtOptA.setText(tableModel.getValueAt(row, 2).toString());
            txtOptB.setText(tableModel.getValueAt(row, 3).toString());
            txtOptC.setText(tableModel.getValueAt(row, 4).toString());
            txtOptD.setText(tableModel.getValueAt(row, 5).toString());
            cmbAnswer.setSelectedItem(tableModel.getValueAt(row, 6).toString());
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom: buttons ---
        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Question");
        JButton btnUpdate = new JButton("Update Question");
        JButton btnDelete = new JButton("Delete Question");
        JButton btnClear = new JButton("Clear Fields");
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        add(buttons, BorderLayout.SOUTH);

        // --- Actions ---
        cmbQuiz.addActionListener(e -> {
            if (loading) return;
            clearFields();
            txtSearch.setText("");
            loadRows("");
        });
        btnSearch.addActionListener(e -> loadRows(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadRows(txtSearch.getText().trim()));
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadRows("");
        });
        btnAdd.addActionListener(e -> addQuestion());
        btnUpdate.addActionListener(e -> updateQuestion());
        btnDelete.addActionListener(e -> deleteQuestion());
        btnClear.addActionListener(e -> clearFields());

        loadQuizzes();
    }

    // Called at start and whenever the Questions tab is opened, so new quizzes show up
    public void loadQuizzes() {
        clearFields();
        loading = true;
        Quiz previous = (Quiz) cmbQuiz.getSelectedItem();
        cmbQuiz.removeAllItems();
        try {
            for (Quiz q : quizDAO.getAllQuizzes()) {
                cmbQuiz.addItem(q);
            }
        } catch (SQLException e) {
            Msg.error(this, e);
        }
        if (previous != null) {
            for (int i = 0; i < cmbQuiz.getItemCount(); i++) {
                if (cmbQuiz.getItemAt(i).getQuizId() == previous.getQuizId()) {
                    cmbQuiz.setSelectedIndex(i);
                }
            }
        }
        loading = false;
        loadRows(txtSearch.getText().trim());
    }

    private void loadRows(String keyword) {
        tableModel.setRowCount(0);
        Quiz quiz = (Quiz) cmbQuiz.getSelectedItem();
        if (quiz == null) {
            lblCount.setText("No quizzes yet. Add one in the Quizzes tab.");
            return;
        }
        try {
            List<Question> list = keyword.isEmpty()
                ? questionDAO.getByQuiz(quiz.getQuizId())
                : questionDAO.search(quiz.getQuizId(), keyword);
            for (Question q : list) {
                tableModel.addRow(new Object[]{
                    q.getQuestionId(), q.getQuestionText(), q.getOptionA(),
                    q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getCorrectAnswer()
                });
            }
            lblCount.setText(list.size() + " question(s) shown");
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    // Returns null (after showing a message) when the form is not valid
    private Question readForm() {
        Quiz quiz = (Quiz) cmbQuiz.getSelectedItem();
        if (quiz == null) {
            Msg.error(this, "Create and select a quiz first.");
            return null;
        }
        String text = txtQuestion.getText().trim();
        String a = txtOptA.getText().trim();
        String b = txtOptB.getText().trim();
        String c = txtOptC.getText().trim();
        String d = txtOptD.getText().trim();
        if (text.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            Msg.error(this, "The question and all four options are required.");
            return null;
        }
        if (text.length() > 500) {
            Msg.error(this, "Question must be at most 500 characters.");
            return null;
        }
        if (a.length() > 200 || b.length() > 200 || c.length() > 200 || d.length() > 200) {
            Msg.error(this, "Each option must be at most 200 characters.");
            return null;
        }
        String answer = (String) cmbAnswer.getSelectedItem();
        int id = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        return new Question(id, quiz.getQuizId(), text, a, b, c, d, answer);
    }

    private void addQuestion() {
        Question q = readForm();
        if (q == null) return;
        try {
            questionDAO.addQuestion(q);
            loadRows(txtSearch.getText().trim());
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void updateQuestion() {
        if (txtId.getText().isEmpty()) {
            Msg.error(this, "Select a question from the table first.");
            return;
        }
        Question q = readForm();
        if (q == null) return;
        try {
            if (!questionDAO.updateQuestion(q)) {
                Msg.error(this, "This question no longer exists. The list has been refreshed.");
            }
            loadRows(txtSearch.getText().trim());
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void deleteQuestion() {
        if (txtId.getText().isEmpty()) {
            Msg.error(this, "Select a question from the table first.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete this question?",
            "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (!questionDAO.deleteQuestion(Integer.parseInt(txtId.getText()))) {
                Msg.error(this, "This question was already deleted. The list has been refreshed.");
            }
            loadRows(txtSearch.getText().trim());
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtQuestion.setText("");
        txtOptA.setText("");
        txtOptB.setText("");
        txtOptC.setText("");
        txtOptD.setText("");
        cmbAnswer.setSelectedIndex(0);
        table.clearSelection();
    }
}
