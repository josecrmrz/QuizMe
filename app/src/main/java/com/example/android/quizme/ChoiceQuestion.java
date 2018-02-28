package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

class ChoiceQuestion extends QuestionBase {

    private ArrayList<Pair<Boolean, String>> _answers = null;

    ChoiceQuestion(String question, ArrayList<Pair<Boolean, String>> answers, QuestionType questionType) {
        _question = question;
        _answers = answers;
        _questionType = questionType;
    }

    ArrayList<Pair<Boolean, String>> getAnswer() {
        return _answers;
    }

    void setAnswers(ArrayList<Pair<Boolean, String>> answers) {
        _answers = answers;
    }
}
