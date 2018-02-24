package com.example.android.quizme;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Object> questions = new ArrayList<>();

    private enum QuestionType {
        MULTIPLE_CHOICE,
        MULTIPLE_ANSWER,
        TRUE_FALSE,
        INPUT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setQuestions();
    }

    /**
     * Create questions by reading a json file
     */
    private void setQuestions() {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONQuestions());
            JSONArray jsonMultipleChoiceQuestions = jsonObject.getJSONArray("multipleChoice");

            createQuestion(jsonMultipleChoiceQuestions);

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

    private void createQuestion(JSONArray jsonMultipleChoiceQuestions) {
        try {

            LinearLayout main = findViewById(R.id.linear_layout_main);

            // loop through each question that is Multiple Choice
            for (int i = 0; i < jsonMultipleChoiceQuestions.length(); i++) {
                JSONObject jsonQuestionObject = jsonMultipleChoiceQuestions.getJSONObject(i);
                JSONArray jsonAnswersArray = jsonQuestionObject.getJSONArray("answers");

                MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                multipleChoiceQuestion.setQuestion(jsonQuestionObject.getString("question"));
//                TextView questionTextView = findViewById(R.id.text_view_question);
//                questionTextView.setText(jsonQuestionObject.getString("question"));

                ArrayList<Pair<Boolean, String>> answers = new ArrayList<>();

                // loop through each answer and assign it to a radio button and tag the correct answer
                for (int j = 0; j < jsonAnswersArray.length(); j++) {
                    Boolean isCorrectAnswer = jsonAnswersArray.getJSONArray(j).getBoolean(0);
                    String answerString = jsonAnswersArray.getJSONArray(j).getString(1);

                    // adding to Array List to shuffle the answers
                    answers.add(new Pair<>(isCorrectAnswer, answerString));
                }

                Collections.shuffle(answers);

                multipleChoiceQuestion.setAnswers(answers);

                questions.add(multipleChoiceQuestion);

                main.addView(getCardViewQuestion(QuestionType.MULTIPLE_CHOICE), i+1);

                RadioGroup answersRadioGroup = findViewById(R.id.radio_group_answers);

                for (int answer = 0; answer < answersRadioGroup.getChildCount(); answer++) {
                    ((RadioButton) answersRadioGroup.getChildAt(answer)).setTag(answers.get(answer).first);
                    ((RadioButton) answersRadioGroup.getChildAt(answer)).setText(answers.get(answer).second);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void checkAnswer(View v) {
        RadioGroup radioGroup = findViewById(R.id.radio_group_answers);

        int radioButtonID = radioGroup.getCheckedRadioButtonId();

        RadioButton radioButton = radioGroup.findViewById(radioButtonID);

        if ((boolean) radioButton.getTag()) {
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private CardView getCardViewQuestion(QuestionType questionType) {
        CardView cardView = new CardView(MainActivity.this);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);

        cardView.setLayoutParams(layoutParams);

        cardView.setRadius(2);
        cardView.setUseCompatPadding(true);

        LinearLayout linearLayout = getLinearLayout(questionType);

        cardView.addView(linearLayout);

        return cardView;
    }

    private LinearLayout getLinearLayout(QuestionType questionType) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        TextView tvQuestion = getTextView();
        RadioGroup radioGroup = getRadioGroup(questionType);

        linearLayout.addView(tvQuestion);
        linearLayout.addView(radioGroup);

        return linearLayout;
    }

    private TextView getTextView() {
        TextView tvQuestion = new TextView(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        tvQuestion.setLayoutParams(layoutParams);
        tvQuestion.setText("Is this a question?");

        return tvQuestion;
    }

    private RadioGroup getRadioGroup(QuestionType questionType) {
        RadioGroup radioGroup = new RadioGroup(getApplicationContext());
        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        switch (questionType){
            case INPUT:
                EditText editText = getEditBox();
                radioGroup.addView(editText);

                break;
            case TRUE_FALSE:
                RadioButton rbTrue = getRadioButton();
                RadioButton rbFalse = getRadioButton();

                radioGroup.addView(rbTrue);
                radioGroup.addView(rbFalse);

                break;
            case MULTIPLE_ANSWER:
                CheckBox checkBox1 = getCheckBox();
                CheckBox checkBox2 = getCheckBox();
                CheckBox checkBox3 = getCheckBox();
                CheckBox checkBox4 = getCheckBox();

                radioGroup.addView(checkBox1);
                radioGroup.addView(checkBox2);
                radioGroup.addView(checkBox3);
                radioGroup.addView(checkBox4);

                break;
            case MULTIPLE_CHOICE:
                RadioButton radioButton1 = getRadioButton();
                RadioButton radioButton2 = getRadioButton();
                RadioButton radioButton3 = getRadioButton();
                RadioButton radioButton4 = getRadioButton();

                radioGroup.addView(radioButton1);
                radioGroup.addView(radioButton2);
                radioGroup.addView(radioButton3);
                radioGroup.addView(radioButton4);

                break;
        }

        return  radioGroup;
    }

    private RadioButton getRadioButton() {
        RadioButton rbAnswer = new RadioButton(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.weight = 1;

        rbAnswer.setLayoutParams(layoutParams);
        rbAnswer.setPadding(padding, 0, padding, 0);
        rbAnswer.setText("Answer 1");

        return rbAnswer;
    }

    private CheckBox getCheckBox() {
        CheckBox cbAnswer = new CheckBox(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.weight = 1;

        cbAnswer.setLayoutParams(layoutParams);
        cbAnswer.setPadding(padding, 0, padding, 0);

        return  cbAnswer;
    }

    private EditText getEditBox() {
        EditText etAnswer = new EditText(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, margin);

        etAnswer.setLayoutParams(layoutParams);

        return  etAnswer;
    }
}
