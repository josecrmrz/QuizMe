package com.example.android.quizme;

import java.util.ArrayList;

public class TextInputQuestion extends QuestionBase {
    private String _question = null;
    private String _answer = null;

    public TextInputQuestion() {
    }

    public String getQuestion() {
        return _question;
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public String getAnswer() {
        return _answer;
    }

    public void setAnswer(String answer) {
        _answer = answer;
    }
}
