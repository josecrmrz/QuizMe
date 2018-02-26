package com.example.android.quizme;

class TextInputQuestion extends QuestionBase {

    private String _answer = null;

    TextInputQuestion() {
    }

    String getAnswer() {
        return _answer;
    }

    void setAnswer(String answer) {
        _answer = answer;
    }
}
