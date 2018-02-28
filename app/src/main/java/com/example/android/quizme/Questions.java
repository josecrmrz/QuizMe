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

    private ArrayList<Object> questions = new ArrayList<>();

    Questions(JSONObject jsonQuestionsFile) {
            createQuestionObjects(jsonQuestionsFile);
    }

    ArrayList<Object> getQuestions() {
        return questions;
    }

    private void createQuestionObjects(JSONObject jsonQuestions) {
        try {
            JSONArray jsonMultipleChoiceQuestions = jsonQuestions.getJSONArray("multipleChoice");
            JSONArray jsonMultipleAnswerQuestions = jsonQuestions.getJSONArray("multipleAnswer");
            JSONArray jsonTrueFalseQuestions = jsonQuestions.getJSONArray("boolean");
            JSONArray jsonTextInputQuestions = jsonQuestions.getJSONArray("textInput");

            createQuestions(jsonMultipleChoiceQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonMultipleAnswerQuestions, QuestionBase.QuestionType.MULTIPLE_ANSWER_QUESTION);
            createQuestions(jsonTrueFalseQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonTextInputQuestions, QuestionBase.QuestionType.TEXT_INPUT_QUESTION);

            // shuffle the mQuestions
            Collections.shuffle(questions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONQuestions(InputStream jsonQuestionsFile) {
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
    private void createQuestions(JSONArray jsonArrayQuestion, QuestionBase.QuestionType questionType) {
        try {
            // loop through each question in the JSON array of mQuestions
            for (int i = 0; i < jsonArrayQuestion.length(); i++) {
                JSONObject jsonQuestionObject = jsonArrayQuestion.getJSONObject(i);

                if (questionType == QuestionBase.QuestionType.TEXT_INPUT_QUESTION) {
                    String question = jsonQuestionObject.getString("question");
                    String answer = jsonQuestionObject.getString("answer");

                    TextInputQuestion textInputQuestion = new TextInputQuestion(question, answer, questionType);

                    questions.add(textInputQuestion);
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

                    ChoiceQuestion choiceQuestion = new ChoiceQuestion(question, answers, questionType);

                    // add the question to the array list of mQuestions
                    questions.add(choiceQuestion);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}