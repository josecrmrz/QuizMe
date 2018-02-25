package com.example.android.quizme;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Pair<Object, QuestionBase.QuestionType>> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setQuestions();
    }

    public void checkAnswer(View v) {
        LinearLayout linearLayout = findViewById(R.id.linear_layout_main);

        int score = 0;

        for (int i = 0; i < linearLayout.getChildCount(); i++) {

            if (!(linearLayout.getChildAt(i) instanceof CardView)) {
                continue;
            }

            CardView cardView = (CardView) linearLayout.getChildAt(i);
            LinearLayout linearLayout1 = (LinearLayout) cardView.getChildAt(0);
            RadioGroup radioGroup = (RadioGroup) linearLayout1.getChildAt(1);

            if (radioGroup.getChildAt(0) instanceof RadioButton) {
                int answerId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButtonAnswer = findViewById(answerId);
                int idx = radioGroup.indexOfChild(radioButtonAnswer);

                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(idx);

                if (radioButton == null) {
                    Toast.makeText(getApplicationContext(), "Please answer all the questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((boolean) radioButton.getTag()) {
                    score += 10;
                }
            } else if (radioGroup.getChildAt(0) instanceof CheckBox) {
                CheckBox checkBox = null;
                for (int c = 0; c < radioGroup.getChildCount(); c++) {
                    checkBox = (CheckBox) radioGroup.getChildAt(i);
                    // TODO: Figure out how to check each checkbox
                }
            }
            else if (radioGroup.getChildAt(0) instanceof EditText) {
                EditText editText = (EditText) radioGroup.getChildAt(0);

                if ((editText.getTag().toString()).equalsIgnoreCase(editText.getText().toString())) {
                    score += 15;
                }
            }
        }

        Toast.makeText(getApplicationContext(), "Score is " + score, Toast.LENGTH_SHORT).show();
    }

    /**
     * Create questions by reading a json file
     */
    private void setQuestions() {
        try {
            LinearLayout linearLayoutMain = findViewById(R.id.linear_layout_main);

            clearCardViews(linearLayoutMain);

            JSONObject jsonObject = new JSONObject(loadJSONQuestions());
            JSONArray jsonMultipleChoiceQuestions = jsonObject.getJSONArray("multipleChoice");
            JSONArray jsonMultipleAnswerQuestions = jsonObject.getJSONArray("multipleAnswer");
            JSONArray jsonArrayTrueFalseQuestions = jsonObject.getJSONArray("boolean");
            JSONArray jsonTextInputQuestions = jsonObject.getJSONArray("textInput");

            createQuestion(jsonMultipleChoiceQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestion(jsonMultipleAnswerQuestions, QuestionBase.QuestionType.MULTIPLE_ANSWER);
            createQuestion(jsonArrayTrueFalseQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestion(jsonTextInputQuestions, QuestionBase.QuestionType.TEXT_INPUT);

            // shuffle the questions
            Collections.shuffle(questions);

            // create the appropriate card view layout for each question
            for (int q = 0; q < questions.size(); q++) {
                if (questions.get(q).first instanceof SingleAnswerQuestion &&
                        SingleAnswerQuestion.class.cast(questions.get(q).first).getQuestionType() == QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION) {
                    linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION), q);
                } else if (questions.get(q).first instanceof SingleAnswerQuestion &&
                        SingleAnswerQuestion.class.cast(questions.get(q).first).getQuestionType() == QuestionBase.QuestionType.MULTIPLE_ANSWER) {
                    linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.MULTIPLE_ANSWER), q);
                } else if (questions.get(q).first instanceof TextInputQuestion) {
                    linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.TEXT_INPUT), q);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the questions.json file to be read
     *
     * @return String buffer
     */
    private String loadJSONQuestions() {
        String json = null;

        try {
            InputStream inputStream = getAssets().open("questions.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    private void createQuestion(JSONArray jsonArrayQuestion, QuestionBase.QuestionType questionType) {
        try {
            // loop through each question that is Multiple Choice
            for (int i = 0; i < jsonArrayQuestion.length(); i++) {
                JSONObject jsonQuestionObject = jsonArrayQuestion.getJSONObject(i);

                if (questionType == QuestionBase.QuestionType.TEXT_INPUT) {
                    TextInputQuestion textInputQuestion = new TextInputQuestion();
                    textInputQuestion.setQuestion(jsonQuestionObject.getString("question"));
                    textInputQuestion.setAnswer(jsonQuestionObject.getString("correctAnswer"));

                    questions.add(new Pair<Object, QuestionBase.QuestionType>(textInputQuestion, QuestionBase.QuestionType.TEXT_INPUT));
                } else {
                    JSONArray jsonAnswersArray = jsonQuestionObject.getJSONArray("answers");

                    SingleAnswerQuestion singleAnswerQuestion = new SingleAnswerQuestion();
                    singleAnswerQuestion.setQuestion(jsonQuestionObject.getString("question"));

                    ArrayList<Pair<Boolean, String>> answers = new ArrayList<>();

                    // loop through each answer and assign it to a radio button and tag the correct answer
                    for (int j = 0; j < jsonAnswersArray.length(); j++) {
                        Boolean isCorrectAnswer = jsonAnswersArray.getJSONArray(j).getBoolean(0);
                        String answerString = jsonAnswersArray.getJSONArray(j).getString(1);

                        // adding to Array List to shuffle the answers
                        answers.add(new Pair<>(isCorrectAnswer, answerString));
                    }

                    // only shuffle the answers if there is more than 2 options
                    if (answers.size() > 2) {
                        Collections.shuffle(answers);
                    }

                    singleAnswerQuestion.setAnswers(answers);

                    if (questionType == QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION) {
                        singleAnswerQuestion.setQuestionType(QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
                    }
                    else if (questionType == QuestionBase.QuestionType.MULTIPLE_ANSWER) {
                        singleAnswerQuestion.setQuestionType(QuestionBase.QuestionType.MULTIPLE_ANSWER);
                    }

                    questions.add(new Pair<Object, QuestionBase.QuestionType>(singleAnswerQuestion, questionType));
                }

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void clearCardViews(LinearLayout linearLayout) {
        while (linearLayout.getChildAt(0) instanceof CardView) {
            linearLayout.removeViewAt(0);
        }
    }

    private CardView getCardViewQuestion(Object question, QuestionBase.QuestionType questionType) {
        CardView cardView = new CardView(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        float cornerRadius = getResources().getDimensionPixelOffset(R.dimen.corner_radius);
        float elevation = getResources().getDimensionPixelOffset(R.dimen.elevation);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(cornerRadius);
        cardView.setElevation(elevation);
        cardView.setUseCompatPadding(true);

        LinearLayout linearLayout = getLinearLayout(question, questionType);

        cardView.addView(linearLayout);

        return cardView;
    }

    private LinearLayout getLinearLayout(Object question, QuestionBase.QuestionType questionType) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tvQuestion = getTextView(question, questionType);
        RadioGroup radioGroup = getRadioGroup(question, questionType);

        linearLayout.addView(tvQuestion);
        linearLayout.addView(radioGroup);

        return linearLayout;
    }

    private TextView getTextView(Object question, QuestionBase.QuestionType questionType) {
        TextView tvQuestion = new TextView(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        tvQuestion.setLayoutParams(layoutParams);

        switch (questionType) {
            case TEXT_INPUT:
                tvQuestion.setText(TextInputQuestion.class.cast(question).getQuestion());
                break;
            case SINGLE_ANSWER_QUESTION:
            case MULTIPLE_ANSWER:
                tvQuestion.setText(SingleAnswerQuestion.class.cast(question).getQuestion());
                break;
        }

        return tvQuestion;
    }

    private RadioGroup getRadioGroup(Object question, QuestionBase.QuestionType questionType) {
        RadioGroup radioGroup = new RadioGroup(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, margin, margin, margin);

        radioGroup.setLayoutParams(layoutParams);

        switch (questionType) {
            case TEXT_INPUT:
                radioGroup.setFocusableInTouchMode(true);
                radioGroup.setFocusable(true);

                EditText editText = getEditBox(TextInputQuestion.class.cast(question).getAnswer());
                radioGroup.addView(editText);

                break;
            case SINGLE_ANSWER_QUESTION:
                for (int i = 0; i < SingleAnswerQuestion.class.cast(question).getAnswer().size(); i++) {
                    RadioButton radioButton = getRadioButton(SingleAnswerQuestion.class.cast(question).getAnswer().get(i));
                    radioGroup.addView((radioButton));
                }

                break;
            case MULTIPLE_ANSWER:
                for (int i = 0; i < SingleAnswerQuestion.class.cast(question).getAnswer().size(); i++) {
                    CheckBox checkBox = getCheckBox(SingleAnswerQuestion.class.cast(question).getAnswer().get(i));
                    radioGroup.addView((checkBox));
                }

                break;
        }

        return radioGroup;
    }

    private RadioButton getRadioButton(Pair<Boolean, String> answer) {
        RadioButton rbAnswer = new RadioButton(getApplicationContext());

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        rbAnswer.setLayoutParams(layoutParams);
        rbAnswer.setPadding(padding, 0, padding, 0);
        rbAnswer.setTag(answer.first);
        rbAnswer.setText(answer.second);

        return rbAnswer;
    }

    private CheckBox getCheckBox(Pair<Boolean, String> answer) {
        CheckBox cbAnswer = new CheckBox(getApplicationContext());

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        cbAnswer.setLayoutParams(layoutParams);
        cbAnswer.setPadding(padding, 0, padding, 0);
        cbAnswer.setTag(answer.first);
        cbAnswer.setText(answer.second);

        return cbAnswer;
    }

    private EditText getEditBox(String answer) {
        EditText etAnswer = new EditText(getApplicationContext());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        etAnswer.setLayoutParams(layoutParams);
        etAnswer.setTag(answer);

        return etAnswer;
    }
}
