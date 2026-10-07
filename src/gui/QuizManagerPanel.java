package gui;

import dao.QuizDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Quiz;

public class QuizManagerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private QuizDAO quizDAO = new QuizDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtId, txtTitle, txtCategory;
    private JSpinner spnDuration;

    public QuizManagerPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtTitle = new JTextField();
        txtCategory = new JTextField();
        spnDuration = new JSpinner(new SpinnerNumberModel(10, 1, 180, 1));

        form.add(new JLabel("ID (Auto):"));
        form.add(txtId);
        form.add(new JLabel("Title:"));
        form.add(txtTitle);
        form.add(new JLabel("Category:"));
        form.add(txtCategory);
        form.add(new JLabel("Duration (minutes):"));
        form.add(spnDuration);
        add(form, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Category", "Duration (min)", "Questions"};
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
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (e.getValueIsAdjusting() || row < 0) return;
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtTitle.setText(tableModel.getValueAt(row, 1).toString());
            txtCategory.setText(tableModel.getValueAt(row, 2).toString());
            spnDuration.setValue(Integer.parseInt(tableModel.getValueAt(row, 3).toString()));
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Quiz");
        JButton btnUpdate = new JButton("Update Quiz");
        JButton btnDelete = new JButton("Delete Quiz");
        JButton btnClear = new JButton("Clear Fields");
        btnAdd.addActionListener(e -> addQuiz());
        btnUpdate.addActionListener(e -> updateQuiz());
        btnDelete.addActionListener(e -> deleteQuiz());
        btnClear.addActionListener(e -> clearFields());
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        add(buttons, BorderLayout.SOUTH);

        refresh();
    }

    // Returns null (after showing a message) when the form is not valid
    private Quiz readForm() {
        String title = txtTitle.getText().trim();
        String category = txtCategory.getText().trim();
        if (title.isEmpty() || category.isEmpty()) {
            Msg.error(this, "Title and category are required.");
            return null;
        }
        if (title.length() > 150) {
            Msg.error(this, "Title must be at most 150 characters.");
            return null;
        }
        if (category.length() > 80) {
            Msg.error(this, "Category must be at most 80 characters.");
            return null;
        }
        Quiz quiz = new Quiz(title, category, (int) spnDuration.getValue());
        if (!txtId.getText().isEmpty()) {
            quiz.setQuizId(Integer.parseInt(txtId.getText()));
        }
        return quiz;
    }

    private void addQuiz() {
        Quiz quiz = readForm();
        if (quiz == null) return;
        try {
            quizDAO.addQuiz(quiz);
            refresh();
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void updateQuiz() {
        if (txtId.getText().isEmpty()) {
            Msg.error(this, "Select a quiz from the table first.");
            return;
        }
        Quiz quiz = readForm();
        if (quiz == null) return;
        try {
            if (!quizDAO.updateQuiz(quiz)) {
                Msg.error(this, "This quiz no longer exists. The list has been refreshed.");
            }
            refresh();
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void deleteQuiz() {
        if (txtId.getText().isEmpty()) {
            Msg.error(this, "Select a quiz from the table first.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Deleting this quiz also deletes all its questions and results.\nContinue?",
            "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (!quizDAO.deleteQuiz(Integer.parseInt(txtId.getText()))) {
                Msg.error(this, "This quiz was already deleted. The list has been refreshed.");
            }
            refresh();
            clearFields();
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtTitle.setText("");
        txtCategory.setText("");
        spnDuration.setValue(10);
        table.clearSelection();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        try {
            for (Quiz q : quizDAO.getAllQuizzes()) {
                tableModel.addRow(new Object[]{
                    q.getQuizId(), q.getTitle(), q.getCategory(), q.getDurationMinutes(), q.getQuestionCount()
                });
            }
        } catch (SQLException e) {
            Msg.error(this, e);
        }
    }
}
