package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

public class TrueFalseQuestion {

    private String _question = null;
    private ArrayList<Pair<Boolean, String>> _answer = null;

    public TrueFalseQuestion() {
    }

    public String getQuestion() {
        return _question;
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public ArrayList<Pair<Boolean, String>> getAnswer() {
        return _answer;
    }

    public void setAnswer(ArrayList<Pair<Boolean, String>> answers) {
        _answer = answers;
    }
}
