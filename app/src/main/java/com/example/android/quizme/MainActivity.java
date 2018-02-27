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

class MainActivity extends AppCompatActivity {

    private ArrayList<Pair<Object, QuestionBase.QuestionType>> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reloadQuestions();
    }

    /* Load the questions to be answered */
    private void reloadQuestions() {
        LinearLayout linearLayoutMain = findViewById(R.id.linear_layout_main);

        clearCardViews(linearLayoutMain);
        createQuestionObjects();
        createCardViews(linearLayoutMain);
    }

    /* Cleat any existing Card Views */
    private void clearCardViews(LinearLayout linearLayout) {
        while (linearLayout.getChildAt(0) instanceof CardView) {
            linearLayout.removeViewAt(0);
        }
    }

    /* Create the question objects ChoiceQuestion or TextInputQuestion  */
    private void createQuestionObjects() {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONQuestions());

            JSONArray jsonMultipleChoiceQuestions = jsonObject.getJSONArray("multipleChoice");
            JSONArray jsonMultipleAnswerQuestions = jsonObject.getJSONArray("multipleAnswer");
            JSONArray jsonTrueFalseQuestions = jsonObject.getJSONArray("boolean");
            JSONArray jsonTextInputQuestions = jsonObject.getJSONArray("textInput");

            createQuestions(jsonMultipleChoiceQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonMultipleAnswerQuestions, QuestionBase.QuestionType.MULTIPLE_ANSWER);
            createQuestions(jsonTrueFalseQuestions, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
            createQuestions(jsonTextInputQuestions, QuestionBase.QuestionType.TEXT_INPUT);

            // shuffle the questions
            Collections.shuffle(questions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Load the questions JSON file*/
    private String loadJSONQuestions() {
        String json = null;

        try {
            InputStream inputStream = getAssets().open("questions.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];

            int numberOfBytes = inputStream.read(buffer);
            inputStream.close();

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
            // loop through each question in the JSON array of questions
            for (int i = 0; i < jsonArrayQuestion.length(); i++) {
                JSONObject jsonQuestionObject = jsonArrayQuestion.getJSONObject(i);

                if (questionType == QuestionBase.QuestionType.TEXT_INPUT) {
                    TextInputQuestion textInputQuestion = new TextInputQuestion();
                    textInputQuestion.setQuestion(jsonQuestionObject.getString("question"));
                    textInputQuestion.setAnswer(jsonQuestionObject.getString("answer"));

                    questions.add(new Pair<Object, QuestionBase.QuestionType>(textInputQuestion, QuestionBase.QuestionType.TEXT_INPUT));
                } else {
                    ArrayList<Pair<Boolean, String>> answers = new ArrayList<>();
                    JSONArray jsonAnswersArray = jsonQuestionObject.getJSONArray("answers");
                    ChoiceQuestion choiceQuestion = new ChoiceQuestion();
                    choiceQuestion.setQuestion(jsonQuestionObject.getString("question"));

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

                    // set the answers to the ChoiceQuestion object
                    choiceQuestion.setAnswers(answers);

                    // Set the appropriate question type
                    if (questionType == QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION) {
                        choiceQuestion.setQuestionType(QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION);
                    } else if (questionType == QuestionBase.QuestionType.MULTIPLE_ANSWER) {
                        choiceQuestion.setQuestionType(QuestionBase.QuestionType.MULTIPLE_ANSWER);
                    }

                    // add the question to the array list of questions
                    questions.add(new Pair<Object, QuestionBase.QuestionType>(choiceQuestion, questionType));
                }

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /* Create the correct Card Views for each type of
     * question to be added to the Main Activity layout */
    private void createCardViews(LinearLayout linearLayoutMain) {
        for (int q = 0; q < questions.size(); q++) {
            if (questions.get(q).first instanceof ChoiceQuestion &&
                    ChoiceQuestion.class.cast(questions.get(q).first).getQuestionType() == QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION) {
                linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.SINGLE_ANSWER_QUESTION), q);
            } else if (questions.get(q).first instanceof ChoiceQuestion &&
                    ChoiceQuestion.class.cast(questions.get(q).first).getQuestionType() == QuestionBase.QuestionType.MULTIPLE_ANSWER) {
                linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.MULTIPLE_ANSWER), q);
            } else if (questions.get(q).first instanceof TextInputQuestion) {
                linearLayoutMain.addView(getCardViewQuestion(questions.get(q).first, QuestionBase.QuestionType.TEXT_INPUT), q);
            }
        }
    }

    /* Returns the Card View with the question and answers */
    private CardView getCardViewQuestion(Object question, QuestionBase.QuestionType questionType) {
        CardView cardView = new CardView(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = getLinearLayout(question, questionType);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        layoutParams.setMargins(margin, margin, margin, 0);

        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(getResources().getDimensionPixelOffset(R.dimen.corner_radius));
        cardView.setElevation(getResources().getDimensionPixelOffset(R.dimen.elevation));
        cardView.setUseCompatPadding(true);
        cardView.addView(linearLayout);

        return cardView;
    }

    /* This will return the Linear Layout with the question and
     * answers to be added inside a Card View */
    private LinearLayout getLinearLayout(Object question, QuestionBase.QuestionType questionType) {
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        TextView tvQuestion = getTextView(question, questionType);
        RadioGroup radioGroup = getRadioGroup(question, questionType);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tvQuestion);
        linearLayout.addView(radioGroup);

        return linearLayout;
    }

    /* This will return the Text View of the Question to be added
     * inside the Linear Layout of the CardView */
    private TextView getTextView(Object question, QuestionBase.QuestionType questionType) {
        TextView tvQuestion = new TextView(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        layoutParams.setMargins(margin, margin, margin, 0);

        tvQuestion.setLayoutParams(layoutParams);

        switch (questionType) {
            case TEXT_INPUT:
                tvQuestion.setText(TextInputQuestion.class.cast(question).getQuestion());
                break;
            case SINGLE_ANSWER_QUESTION:
            case MULTIPLE_ANSWER:
                tvQuestion.setText(ChoiceQuestion.class.cast(question).getQuestion());
                break;
        }

        return tvQuestion;
    }

    /* This will return the Radio Group to be added inside a Linear Layout.
     * NOTE: This will be the default grouping of answers to make checking answers easier */
    private RadioGroup getRadioGroup(Object question, QuestionBase.QuestionType questionType) {
        RadioGroup radioGroup = new RadioGroup(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        layoutParams.setMargins(margin, margin, margin, margin);

        radioGroup.setLayoutParams(layoutParams);

        switch (questionType) {
            case TEXT_INPUT:
                EditText editText = getEditBoxAnswer(TextInputQuestion.class.cast(question).getAnswer());

                radioGroup.setFocusableInTouchMode(true);
                radioGroup.setFocusable(true);
                radioGroup.addView(editText);

                break;
            case SINGLE_ANSWER_QUESTION:
                for (int i = 0; i < ChoiceQuestion.class.cast(question).getAnswer().size(); i++) {
                    RadioButton radioButton = getRadioButtonAnswer(ChoiceQuestion.class.cast(question).getAnswer().get(i));
                    radioGroup.addView(radioButton);
                }

                break;
            case MULTIPLE_ANSWER:
                for (int i = 0; i < ChoiceQuestion.class.cast(question).getAnswer().size(); i++) {
                    CheckBox checkBox = getCheckBoxAnswer(ChoiceQuestion.class.cast(question).getAnswer().get(i));
                    radioGroup.addView(checkBox);
                }

                break;
        }

        return radioGroup;
    }

    /* This will return the Radio Button to be added inside a Radio Group */
    private RadioButton getRadioButtonAnswer(Pair<Boolean, String> answer) {
        RadioButton radioButton = new RadioButton(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        radioButton.setLayoutParams(layoutParams);
        radioButton.setPadding(padding, 0, padding, 0);
        radioButton.setTag(answer.first);
        radioButton.setText(answer.second);

        return radioButton;
    }

    /* This will return the CheckBox to be added inside a Radio Group */
    private CheckBox getCheckBoxAnswer(Pair<Boolean, String> answer) {
        CheckBox checkBox = new CheckBox(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        checkBox.setLayoutParams(layoutParams);
        checkBox.setPadding(padding, 0, padding, 0);
        checkBox.setTag(answer.first);
        checkBox.setText(answer.second);

        return checkBox;
    }

    /* This will return the Edit Text to be added inside a Radio Group */
    private EditText getEditBoxAnswer(String answer) {
        EditText editText = new EditText(getApplicationContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        editText.setLayoutParams(layoutParams);
        editText.setHint("Enter your answer");
        editText.setTag(answer);

        return editText;
    }

    /* Check the answers and display the score in a custom Toast message */
    public void checkAnswer(View v) {
        LinearLayout linearLayout = findViewById(R.id.linear_layout_main);

        int score = 0;

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            // This will skip anything that is not a CardView
            if (!(linearLayout.getChildAt(i) instanceof CardView)) {
                continue;
            }

            CardView cardView = (CardView) linearLayout.getChildAt(i);
            LinearLayout linearLayout1 = (LinearLayout) cardView.getChildAt(0);
            RadioGroup radioGroup = (RadioGroup) linearLayout1.getChildAt(1);

            if (radioGroup.getChildAt(0) instanceof RadioButton) {
                RadioButton radioButtonAnswer = findViewById(radioGroup.getCheckedRadioButtonId());
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(radioGroup.indexOfChild(radioButtonAnswer));

                // This indicates that not
                if (radioButton == null) {
                    Toast.makeText(getApplicationContext(), "Please answer all the questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((boolean) radioButton.getTag()) {
                    score += 10;
                }
            } else if (radioGroup.getChildAt(0) instanceof CheckBox) {
                boolean isCorrect = true;   // assume correct answers

                for (int c = 0; c < radioGroup.getChildCount(); c++) {
                    CheckBox checkBox = (CheckBox) radioGroup.getChildAt(c);

                    /* incorrect answer if did not select a correct answer OR
                    selected an incorrect answer */
                    if ((checkBox.isChecked() && !(boolean) checkBox.getTag()) ||
                            (!checkBox.isChecked() && (boolean) checkBox.getTag())) {
                        isCorrect = false;
                    }
                }

                if (isCorrect) {
                    score += 1;
                }
            } else if (radioGroup.getChildAt(0) instanceof EditText) {
                EditText editText = (EditText) radioGroup.getChildAt(0);

                if ((editText.getTag().toString()).equalsIgnoreCase(editText.getText().toString())) {
                    score += 15;
                }
            }
        }

        Toast.makeText(getApplicationContext(), "Score is " + score, Toast.LENGTH_SHORT).show();
    }
}
