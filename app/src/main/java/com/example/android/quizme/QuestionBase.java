package com.example.android.quizme;

abstract class QuestionBase {

    QuestionType _questionType = QuestionType.SINGLE_ANSWER_QUESTION;
    String _question = null;

    public enum QuestionType {
        SINGLE_ANSWER_QUESTION,
        MULTIPLE_ANSWER_QUESTION,
        TEXT_INPUT_QUESTION
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

    boolean isSingleAnswerQuestion() {
        return _questionType == QuestionType.SINGLE_ANSWER_QUESTION;
    }

    boolean isMultipleAnswerQuestion() {
        return _questionType == QuestionType.MULTIPLE_ANSWER_QUESTION;
    }

    boolean isTextInputQuestion() {
        return _questionType == QuestionType.TEXT_INPUT_QUESTION;
    }
}
