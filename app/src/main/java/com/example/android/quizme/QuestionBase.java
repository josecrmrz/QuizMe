package com.example.android.quizme;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

abstract class QuestionBase {

    String mQuestion = null;
    @QuestionType int mQuestionType;

    static final int SINGLE_ANSWER_QUESTION = 0;
    static final int MULTIPLE_ANSWER_QUESTION = 1;
    static final int TEXT_INPUT_QUESTION = 2;

    QuestionBase() {
    }

    String getQuestion() {
        return mQuestion;
    }

    boolean isSingleAnswerQuestion() {
        return mQuestionType == SINGLE_ANSWER_QUESTION;
    }

    boolean isMultipleAnswerQuestion() {
        return mQuestionType == MULTIPLE_ANSWER_QUESTION;
    }

    @Retention(RetentionPolicy.SOURCE)

    @IntDef({SINGLE_ANSWER_QUESTION, MULTIPLE_ANSWER_QUESTION, TEXT_INPUT_QUESTION})
    @interface QuestionType {
    }
}
