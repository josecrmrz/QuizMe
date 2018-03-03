package com.example.android.quizme;

class TextInputQuestion extends QuestionBase {

    private String _answer = null;

    TextInputQuestion(String question, String answer, @QuestionType int questionType) {
        mQuestion = question;
        _answer = answer;
        mQuestionType = questionType;
    }

    String getAnswer() {
        return _answer;
    }
}
