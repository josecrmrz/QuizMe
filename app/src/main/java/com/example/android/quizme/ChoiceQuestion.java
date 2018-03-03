package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

class ChoiceQuestion extends QuestionBase {

    private ArrayList<Pair<Boolean, String>> mAnswers = null;

    ChoiceQuestion(String question, ArrayList<Pair<Boolean, String>> answers, @QuestionType int questionType) {
        mQuestion = question;
        mAnswers = answers;
        mQuestionType = questionType;
    }

    ArrayList<Pair<Boolean, String>> getAnswers() {
        return mAnswers;
    }
}
