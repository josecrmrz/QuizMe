package com.example.android.quizme;

import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

class Questions {

    private ArrayList<Object> mQuestions = new ArrayList<>();

    Questions(InputStream jsonQuestionsFile) {
        try {
            createQuestionObjects(new JSONObject(getJSONQuestions(jsonQuestionsFile)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Return the Array list of Questions */
    ArrayList<Object> getQuestions() {
        return mQuestions;
    }

    /* Create the Question objects ChoiceQuestion or TextInputQuestion */
    private void createQuestionObjects(JSONObject jsonQuestions) {
        try {
            JSONArray jsonMultipleChoiceQuestions = jsonQuestions.getJSONArray("multipleChoice");
            JSONArray jsonMultipleAnswerQuestions = jsonQuestions.getJSONArray("multipleAnswer");
            JSONArray jsonTrueFalseQuestions = jsonQuestions.getJSONArray("boolean");
            JSONArray jsonTextInputQuestions = jsonQuestions.getJSONArray("textInput");

            createQuestions(jsonMultipleChoiceQuestions, QuestionBase.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonTrueFalseQuestions, QuestionBase.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonMultipleAnswerQuestions, QuestionBase.MULTIPLE_ANSWER_QUESTION);
            createQuestions(jsonTextInputQuestions, QuestionBase.TEXT_INPUT_QUESTION);

            // shuffle the questions
            Collections.shuffle(mQuestions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* Get the JSON String from the questions.json file */
    private String getJSONQuestions(InputStream jsonQuestionsFile) {
        String json = null;

        try {
            int size = jsonQuestionsFile.available();
            byte[] buffer = new byte[size];

            int numberOfBytes = jsonQuestionsFile.read(buffer);
            jsonQuestionsFile.close();

            if (numberOfBytes > -1) {
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    /* Create the question objects and store them in an Array List to be shuffled */
    private void createQuestions(JSONArray jsonArrayQuestion, int questionType) {
        try {
            // loop through each question in the JSON array of questions
            for (int i = 0; i < jsonArrayQuestion.length(); i++) {
                JSONObject jsonQuestionObject = jsonArrayQuestion.getJSONObject(i);

                if (questionType == QuestionBase.TEXT_INPUT_QUESTION) {
                    String question = jsonQuestionObject.getString("question");
                    String answer = jsonQuestionObject.getString("answer");

                    mQuestions.add(new TextInputQuestion(question, answer, questionType));
                } else {
                    ArrayList<Pair<Boolean, String>> answers = new ArrayList<>();

                    String question = jsonQuestionObject.getString("question");
                    JSONArray jsonAnswersArray = jsonQuestionObject.getJSONArray("answers");

                    // create a Pair for the answers for the ChoiceQuestion object
                    for (int answerIndex = 0; answerIndex < jsonAnswersArray.length(); answerIndex++) {
                        Boolean isCorrectAnswer = jsonAnswersArray.getJSONArray(answerIndex).getBoolean(0);
                        String answerString = jsonAnswersArray.getJSONArray(answerIndex).getString(1);

                        // adding to Array List to shuffle the answers
                        answers.add(new Pair<>(isCorrectAnswer, answerString));
                    }

                    // only shuffle the answers if there is more than 2 options
                    if (answers.size() > 2) {
                        Collections.shuffle(answers);
                    }

                    // add the question to the array list of mQuestions
                    mQuestions.add(new ChoiceQuestion(question, answers, questionType));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
