package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import model.QuizResult;

public class CertificateGenerator {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final int MAX_TEXT_WIDTH = WIDTH - 140;

    public static void create(File file, String studentName, String quizTitle,
                              int score, int total, double percentage) throws IOException {
        if (file == null) {
            throw new IOException("No file was chosen for the certificate.");
        }
        String name = clean(studentName, "Student");
        String quiz = clean(quizTitle, "Quiz");

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(new Color(20, 50, 110));
            g.setStroke(new BasicStroke(8));
            g.drawRect(20, 20, WIDTH - 40, HEIGHT - 40);
            g.setStroke(new BasicStroke(2));
            g.drawRect(36, 36, WIDTH - 72, HEIGHT - 72);

            drawCentered(g, "Certificate of Achievement", "Serif", Font.BOLD, 46, 170);

            g.setColor(Color.DARK_GRAY);
            drawCentered(g, "This is to certify that", "SansSerif", Font.PLAIN, 22, 260);

            g.setColor(new Color(20, 50, 110));
            drawCentered(g, name, "Serif", Font.BOLD | Font.ITALIC, 42, 335);

            g.setColor(Color.DARK_GRAY);
            drawCentered(g, "has successfully completed the quiz", "SansSerif", Font.PLAIN, 22, 405);

            g.setColor(Color.BLACK);
            drawCentered(g, quiz, "SansSerif", Font.BOLD, 30, 465);

            g.setColor(Color.DARK_GRAY);
            drawCentered(g, "with a score of " + score + " / " + total + " (" + percentage + "%)",
                         "SansSerif", Font.PLAIN, 24, 535);
            drawCentered(g, "Grade: " + QuizResult.grade(percentage), "SansSerif", Font.PLAIN, 22, 580);

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            drawCentered(g, "Date: " + date, "SansSerif", Font.PLAIN, 18, 640);
        } finally {
            g.dispose();
        }

        if (!ImageIO.write(img, "png", file)) {
            throw new IOException("Could not write the certificate image.");
        }
    }

    private static String clean(String text, String fallback) {
        return text == null || text.trim().isEmpty() ? fallback : text.trim();
    }

    // Shrinks the font, and as a last resort shortens the text, so it always fits inside the border
    private static void drawCentered(Graphics2D g, String text, String family, int style, int size, int y) {
        Font font = new Font(family, style, size);
        g.setFont(font);
        while (size > 14 && g.getFontMetrics().stringWidth(text) > MAX_TEXT_WIDTH) {
            size -= 2;
            font = new Font(family, style, size);
            g.setFont(font);
        }
        while (text.length() > 1 && g.getFontMetrics().stringWidth(text) > MAX_TEXT_WIDTH) {
            text = text.substring(0, text.length() - 2) + "…";
        }
        int x = (WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
