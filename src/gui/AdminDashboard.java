package gui;

import dao.QuestionDAO;
import model.Question;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private QuestionDAO questionDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtQuestion, txtOptA, txtOptB, txtOptC, txtOptD, txtAnswer;

    public AdminDashboard() {
        questionDAO = new QuestionDAO();

        setTitle("Admin Dashboard - Question Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Panel: Input Fields ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("ID (Auto):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Correct Answer (A/B/C/D):"));
        txtAnswer = new JTextField();
        inputPanel.add(txtAnswer);

        inputPanel.add(new JLabel("Question:"));
        txtQuestion = new JTextField();
        inputPanel.add(txtQuestion);

        inputPanel.add(new JLabel("Option A:"));
        txtOptA = new JTextField();
        inputPanel.add(txtOptA);

        inputPanel.add(new JLabel("Option B:"));
        txtOptB = new JTextField();
        inputPanel.add(txtOptB);

        inputPanel.add(new JLabel("Option C:"));
        txtOptC = new JTextField();
        inputPanel.add(txtOptC);

        inputPanel.add(new JLabel("Option D:"));
        txtOptD = new JTextField();
        inputPanel.add(txtOptD);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel: Table ---
        String[] columns = {"ID", "Question", "A", "B", "C", "D", "Answer"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // Listen for table row selection to populate text fields
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtQuestion.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtOptA.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtOptB.setText(tableModel.getValueAt(selectedRow, 3).toString());
                txtOptC.setText(tableModel.getValueAt(selectedRow, 4).toString());
                txtOptD.setText(tableModel.getValueAt(selectedRow, 5).toString());
                txtAnswer.setText(tableModel.getValueAt(selectedRow, 6).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnAdd = new JButton("Add Question");
        JButton btnUpdate = new JButton("Update Question");
        JButton btnDelete = new JButton("Delete Question");
        JButton btnClear = new JButton("Clear Fields");

        btnAdd.addActionListener(e -> addQuestion());
        btnUpdate.addActionListener(e -> updateQuestion());
        btnDelete.addActionListener(e -> deleteQuestion());
        btnClear.addActionListener(e -> clearFields());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshTable();
    }

    private void addQuestion() {
        Question q = new Question(
            txtQuestion.getText(), txtOptA.getText(), txtOptB.getText(),
            txtOptC.getText(), txtOptD.getText(), txtAnswer.getText().toUpperCase()
        );
        if (questionDAO.addQuestion(q)) {
            JOptionPane.showMessageDialog(this, "Question added successfully!");
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding question.");
        }
    }

    private void updateQuestion() {
        if (txtId.getText().isEmpty()) return;
        Question q = new Question(
            Integer.parseInt(txtId.getText()), txtQuestion.getText(), txtOptA.getText(),
            txtOptB.getText(), txtOptC.getText(), txtOptD.getText(), txtAnswer.getText().toUpperCase()
        );
        if (questionDAO.updateQuestion(q)) {
            JOptionPane.showMessageDialog(this, "Question updated successfully!");
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating question.");
        }
    }

    private void deleteQuestion() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        if (questionDAO.deleteQuestion(id)) {
            JOptionPane.showMessageDialog(this, "Question deleted successfully!");
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting question.");
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtQuestion.setText("");
        txtOptA.setText("");
        txtOptB.setText("");
        txtOptC.setText("");
        txtOptD.setText("");
        txtAnswer.setText("");
        table.clearSelection();
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Question> questions = questionDAO.getAllQuestions();
        for (Question q : questions) {
            tableModel.addRow(new Object[]{
                q.getQuestionId(), q.getQuestionText(), q.getOptionA(), 
                q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getCorrectAnswer()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}