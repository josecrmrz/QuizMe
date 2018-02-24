package com.example.android.quizme;

/**
 * Created by joser on 2/20/18.
 */

public class TrueFalseQuestion {
    private String question = null;
    private Boolean answer = null;

    public TrueFalseQuestion() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answers) {
        this.answer = answers;
    }
}
