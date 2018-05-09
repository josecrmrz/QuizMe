package com.example.android.quizme;

import android.support.v4.util.Pair;

import java.util.ArrayList;

class ChoiceQuestion extends QuestionBase {

    private ArrayList<Pair<Boolean, String>> mAnswers = null;

    /* Creates a Choice question object for CheckBox and RadioButton Views*/
    ChoiceQuestion(String question, ArrayList<Pair<Boolean, String>> answers, QuestionType questionType) {
        mQuestion = question;
        mAnswers = answers;
        mQuestionType = questionType;
    }

    ArrayList<Pair<Boolean, String>> getAnswers() {
        return mAnswers;
    }
}
