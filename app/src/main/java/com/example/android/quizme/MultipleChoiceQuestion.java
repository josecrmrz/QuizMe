package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

public class MultipleChoiceQuestion {

    private String _question = null;
    private ArrayList<Pair<Boolean, String>> _answers = null;

    public MultipleChoiceQuestion() {
    }

    public String getQuestion() {
        return _question;
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public ArrayList<Pair<Boolean, String>> getAnswer() {
        return _answers;
    }

    public void setAnswers(ArrayList<Pair<Boolean, String>> answers) {
        _answers = answers;
    }
}
