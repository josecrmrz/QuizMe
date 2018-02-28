package com.example.android.quizme;

class TextInputQuestion extends QuestionBase {

    private String _answer = null;

    TextInputQuestion(String question, String answer, QuestionType questionType) {
        _question = question;
        _answer = answer;
        _questionType = questionType;
    }

    String getAnswer() {
        return _answer;
    }

    void setAnswer(String answer) {
        _answer = answer;
    }
}
