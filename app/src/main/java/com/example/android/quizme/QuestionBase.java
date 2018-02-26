package com.example.android.quizme;

abstract class QuestionBase {

    private QuestionType _questionType = QuestionType.SINGLE_ANSWER_QUESTION;
    private String _question = null;

    public enum QuestionType {
        SINGLE_ANSWER_QUESTION,
        MULTIPLE_ANSWER,
        TEXT_INPUT
    }

    QuestionBase() {
    }

    QuestionType getQuestionType() {
        return _questionType;
    }

    void setQuestionType(QuestionType questionType) {
        _questionType = questionType;
    }

    String getQuestion() {
        return _question;
    }

    void setQuestion(String question) {
        _question = question;
    }
}
