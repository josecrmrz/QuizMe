package com.example.android.quizme;

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

public class MainActivity extends AppCompatActivity {

    private ArrayList<Pair<Object, QuestionType>> questions = new ArrayList<>();

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
            main.removeViewAt(0);

            // loop through each question that is Multiple Choice
            for (int i = 0; i < jsonMultipleChoiceQuestions.length(); i++) {
                JSONObject jsonQuestionObject = jsonMultipleChoiceQuestions.getJSONObject(i);
                JSONArray jsonAnswersArray = jsonQuestionObject.getJSONArray("answers");

                MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                multipleChoiceQuestion.setQuestion(jsonQuestionObject.getString("question"));

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

                questions.add(new Pair<Object, QuestionType>(multipleChoiceQuestion, QuestionType.MULTIPLE_CHOICE));
            }

            // shuffle the questions
            Collections.shuffle(questions);

            // create the questions to their appropriate card view layout
            for (int q = 0; q < questions.size(); q++) {
                main.addView(getCardViewQuestion(questions.get(q).first, QuestionType.MULTIPLE_CHOICE), q);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
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
        }

        Toast.makeText(getApplicationContext(), "Score is " + score, Toast.LENGTH_SHORT).show();
    }

    private CardView getCardViewQuestion(Object question, QuestionType questionType) {
        CardView cardView = new CardView(MainActivity.this);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(2);
        cardView.setUseCompatPadding(true);

        LinearLayout linearLayout = getLinearLayout(question, questionType);

        cardView.addView(linearLayout);

        return cardView;
    }

    private LinearLayout getLinearLayout(Object question, QuestionType questionType) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tvQuestion = getTextView(question);
        RadioGroup radioGroup = getRadioGroup(question, questionType);

        linearLayout.addView(tvQuestion);
        linearLayout.addView(radioGroup);

        return linearLayout;
    }

    private TextView getTextView(Object question) {
        TextView tvQuestion = new TextView(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, margin, margin, 0);

        tvQuestion.setLayoutParams(layoutParams);
        tvQuestion.setText(MultipleChoiceQuestion.class.cast(question).getQuestion());

        return tvQuestion;
    }

    private RadioGroup getRadioGroup(Object question, QuestionType questionType) {
        RadioGroup radioGroup = new RadioGroup(getApplicationContext());

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, margin, margin, margin);

        radioGroup.setLayoutParams(layoutParams);

        switch (questionType) {
            case INPUT:
                EditText editText = getEditBox();
                radioGroup.addView(editText);

                break;
            case TRUE_FALSE:
                RadioButton rbTrue = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(0));
                RadioButton rbFalse = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(1));

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
                RadioButton radioButton1 = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(0));
                RadioButton radioButton2 = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(1));
                RadioButton radioButton3 = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(2));
                RadioButton radioButton4 = getRadioButton(MultipleChoiceQuestion.class.cast(question).getAnswer().get(3));

                radioGroup.addView(radioButton1);
                radioGroup.addView(radioButton2);
                radioGroup.addView(radioButton3);
                radioGroup.addView(radioButton4);

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

    private CheckBox getCheckBox() {
        CheckBox cbAnswer = new CheckBox(getApplicationContext());

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        cbAnswer.setLayoutParams(layoutParams);
        cbAnswer.setPadding(padding, 0, padding, 0);

        return cbAnswer;
    }

    private EditText getEditBox() {
        EditText etAnswer = new EditText(getApplicationContext());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        etAnswer.setLayoutParams(layoutParams);

        return etAnswer;
    }
}
