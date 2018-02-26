package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

class SingleAnswerQuestion extends QuestionBase {

    private ArrayList<Pair<Boolean, String>> _answers = null;

    SingleAnswerQuestion() {
    }

    ArrayList<Pair<Boolean, String>> getAnswer() {
        return _answers;
    }

    void setAnswers(ArrayList<Pair<Boolean, String>> answers) {
        _answers = answers;
    }
}
