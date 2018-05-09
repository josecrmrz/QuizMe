package com.example.android.quizme;

class TextInputQuestion extends QuestionBase {

    private String mAnswer = null;

    /* Creates an Text Input question for EditText views */
    TextInputQuestion(String question, String answer, QuestionType questionType) {
        mQuestion = question;
        mAnswer = answer;
        mQuestionType = questionType;
    }

    String getAnswer() {
        return mAnswer;
    }
}
