package com.example.android.quizme;

class TextInputQuestion extends QuestionBase {

    private String mAnswer = null;

    TextInputQuestion(String question, String answer, @QuestionType int questionType) {
        mQuestion = question;
        mAnswer = answer;
        mQuestionType = questionType;
    }

    String getAnswer() {
        return mAnswer;
    }
}
