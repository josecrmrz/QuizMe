package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

/**
 * Created by joser on 2/14/18.
 */

public class MultipleChoiceQuestion {
    private String question = null;
    private ArrayList<Pair<Boolean, String>> answers = null;

    public MultipleChoiceQuestion() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Pair<Boolean, String>> getAnswer() {
        return answers;
    }

    public void setAnswers(ArrayList<Pair<Boolean, String>> answers) {
        this.answers = answers;
    }
}
