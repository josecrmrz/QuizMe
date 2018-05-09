package com.example.android.quizme;

import static com.example.android.quizme.QuestionBase.QuestionType.MULTIPLE_ANSWER_QUESTION;
import static com.example.android.quizme.QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION;

abstract class QuestionBase {

    String mQuestion = null;
    QuestionType mQuestionType;

    public enum QuestionType {
        SINGLE_ANSWER_QUESTION,
        MULTIPLE_ANSWER_QUESTION,
        TEXT_INPUT_QUESTION
    }

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
}
