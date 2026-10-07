package model;

public class QuizResult {
    private String studentName;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private double percentage;
    private int timeTakenSec;
    private String attemptedAt;

    public QuizResult(String studentName, String quizTitle, int score, int totalQuestions,
                      double percentage, int timeTakenSec, String attemptedAt) {
        this.studentName = studentName;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.timeTakenSec = timeTakenSec;
        this.attemptedAt = attemptedAt;
    }

    public String getStudentName() { return studentName; }
    public String getQuizTitle() { return quizTitle; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getPercentage() { return percentage; }
    public int getTimeTakenSec() { return timeTakenSec; }
    public String getAttemptedAt() { return attemptedAt; }

    // Rounded to 2 decimals, 0 when there are no questions (avoids division by zero)
    public static double percentage(int score, int total) {
        if (total <= 0) return 0;
        return Math.round(score * 10000.0 / total) / 100.0;
    }

    public static String grade(double percentage) {
        if (percentage >= 80) return "Excellent";
        if (percentage >= 60) return "Good";
        if (percentage >= 40) return "Average";
        return "Needs Improvement";
    }

    public static String formatTime(int seconds) {
        return (seconds / 60) + "m " + String.format("%02d", seconds % 60) + "s";
    }
}
