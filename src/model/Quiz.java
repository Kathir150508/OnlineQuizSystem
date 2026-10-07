package model;

public class Quiz {
    private int quizId;
    private String title;
    private String category;
    private int durationMinutes;
    private int questionCount;

    // Used when reading from the database
    public Quiz(int quizId, String title, String category, int durationMinutes, int questionCount) {
        this.quizId = quizId;
        this.title = title;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.questionCount = questionCount;
    }

    // Used when adding a new quiz (the database creates the id)
    public Quiz(String title, String category, int durationMinutes) {
        this.title = title;
        this.category = category;
        this.durationMinutes = durationMinutes;
    }

    public int getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getQuestionCount() { return questionCount; }

    public void setQuizId(int quizId) { this.quizId = quizId; }

    @Override
    public String toString() {
        return title;
    }
}
