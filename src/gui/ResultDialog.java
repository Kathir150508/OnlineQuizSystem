package gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import model.QuizResult;
import util.CertificateGenerator;

public class ResultDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public ResultDialog(String studentName, String quizTitle, int score, int total, int timeTaken, boolean saved) {
        setTitle("Quiz Result");
        setModal(true);
        setSize(420, 360);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        double percentage = QuizResult.percentage(score, total);

        JPanel info = new JPanel(new GridLayout(6, 1, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblScore = new JLabel("Score: " + score + " / " + total, SwingConstants.CENTER);
        lblScore.setFont(new Font("SansSerif", Font.BOLD, 28));
        info.add(new JLabel(studentName + " - " + quizTitle, SwingConstants.CENTER));
        info.add(lblScore);
        info.add(new JLabel("Percentage: " + percentage + "%", SwingConstants.CENTER));
        info.add(new JLabel("Grade: " + QuizResult.grade(percentage), SwingConstants.CENTER));
        info.add(new JLabel("Time taken: " + QuizResult.formatTime(timeTaken), SwingConstants.CENTER));
        info.add(new JLabel(saved ? "Result saved." : "Result could NOT be saved.", SwingConstants.CENTER));
        add(info, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnCertificate = new JButton("Download Certificate");
        JButton btnClose = new JButton("Close");
        btnCertificate.setEnabled(percentage >= 80);
        btnCertificate.setToolTipText("Available for a score of 80% or more");
        buttons.add(btnCertificate);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnCertificate.addActionListener(e -> saveCertificate(studentName, quizTitle, score, total, percentage));
        btnClose.addActionListener(e -> dispose());
    }

    private void saveCertificate(String studentName, String quizTitle, int score, int total, double percentage) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("certificate.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }
        try {
            CertificateGenerator.create(file, studentName, quizTitle, score, total, percentage);
            Msg.info(this, "Certificate saved to:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            Msg.error(this, "Could not save the certificate: " + ex.getMessage());
        }
    }
}
