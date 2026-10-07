package model;

public class Question {
    private int questionId;
    private int quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    // Used when reading from the database
    public Question(int questionId, int quizId, String questionText, String optionA,
                    String optionB, String optionC, String optionD, String correctAnswer) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    // Used when adding a new question (the database creates the id)
    public Question(int quizId, String questionText, String optionA,
                    String optionB, String optionC, String optionD, String correctAnswer) {
        this(0, quizId, questionText, optionA, optionB, optionC, optionD, correctAnswer);
    }

    public int getQuestionId() { return questionId; }
    public int getQuizId() { return quizId; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
}
