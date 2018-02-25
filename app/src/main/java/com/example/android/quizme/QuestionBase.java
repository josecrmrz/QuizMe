package com.example.android.quizme;

public class QuestionBase {
    QuestionType _questionType = QuestionType.SINGLE_ANSWER_QUESTION;

    public enum QuestionType {
        SINGLE_ANSWER_QUESTION,
        MULTIPLE_ANSWER,
        TEXT_INPUT
    }

    public QuestionBase() {
    }

    protected QuestionType getQuestionType() {
        return _questionType;
    }

    protected void setQuestionType(QuestionType questionType) {
        _questionType = questionType;
    }
}
