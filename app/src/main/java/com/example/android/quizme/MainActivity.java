package com.example.android.quizme;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

class MainActivity extends AppCompatActivity {

    private Questions questions = null;

    private static final int EDIT_TEXT_SCORE = 10;
    private static final int RADIO_BUTTON_SCORE = 10;
    private static final int CHECK_BOX_SCORE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadQuestion();
    }

    public void loadQuestion() {
        LinearLayout linearLayoutMain = findViewById(R.id.linear_layout_main);

        clearCardViews(linearLayoutMain);
        loadQuestions();
        addCardViewQuestions(linearLayoutMain);
    }

    /* Clear any existing Card Views */
    private void clearCardViews(LinearLayout linearLayout) {
        while (linearLayout.getChildAt(0) instanceof CardView) {
            linearLayout.removeViewAt(0);
        }
    }

    /* Create the Questions object containing the list of Questions
      * by passing in the questions.json file */
    private void loadQuestions() {
        try {
            questions = new Questions(getAssets().open("questions.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Create the correct Card Views for each type of
     * question to be added to the Main Activity layout */
    private void addCardViewQuestions(LinearLayout linearLayoutMain) {
        for (int q = 0; q < questions.getQuestions().size(); q++) {
            linearLayoutMain.addView(getCardViewQuestion(questions.getQuestions().get(q)), q);
        }
    }

    /* Returns the Card View with the question and answers */
    private CardView getCardViewQuestion(Object question) {
        CardView cardView = new CardView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = getLinearLayout(question);

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
    private LinearLayout getLinearLayout(Object question) {
        LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);
        linearLayout.addView(getTextView(question));
        linearLayout.addView(getRadioGroup(question));

        return linearLayout;
    }

    /* This will return the Text View of the Question to be added
     * inside the Linear Layout of the CardView */
    private TextView getTextView(Object question) {
        TextView tvQuestion = new TextView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        layoutParams.setMargins(margin, margin, margin, 0);

        tvQuestion.setLayoutParams(layoutParams);
        tvQuestion.setTextAppearance(this, R.style.question_style);

        if (question instanceof TextInputQuestion) {
            tvQuestion.setText(((TextInputQuestion) question).getQuestion());
        } else {
            tvQuestion.setText(((ChoiceQuestion) question).getQuestion());
        }

        return tvQuestion;
    }

    /* This will return the Radio Group to be added inside a Linear Layout.
     * NOTE: This will be the default grouping of answers to make checking answers easier */
    private RadioGroup getRadioGroup(Object question) {
        RadioGroup radioGroup = new RadioGroup(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);

        layoutParams.setMargins(margin, margin, margin, margin);

        radioGroup.setLayoutParams(layoutParams);

        if (question instanceof TextInputQuestion) {
            EditText editText = getEditBoxAnswer(((TextInputQuestion) question).getAnswer());
            radioGroup.addView(editText);
        } else if (((ChoiceQuestion) question).isSingleAnswerQuestion()) {
            for (int i = 0; i < ((ChoiceQuestion) question).getAnswers().size(); i++) {
                RadioButton radioButton = getRadioButtonAnswer(((ChoiceQuestion) question).getAnswers().get(i));
                radioGroup.addView(radioButton);
            }
        } else if (((ChoiceQuestion) question).isMultipleAnswerQuestion()) {
            for (int i = 0; i < ((ChoiceQuestion) question).getAnswers().size(); i++) {
                CheckBox checkBox = getCheckBoxAnswer(((ChoiceQuestion) question).getAnswers().get(i));
                radioGroup.addView(checkBox);
            }
        }

        return radioGroup;
    }

    /* This will return the Radio Button to be added inside a Radio Group */
    private RadioButton getRadioButtonAnswer(Pair<Boolean, String> answer) {
        RadioButton radioButton = new RadioButton(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        radioButton.setLayoutParams(layoutParams);
        radioButton.setPadding(padding, 0, padding, 0);
        radioButton.setTag(answer.first);
        radioButton.setText(answer.second);
        radioButton.setTextAppearance(this, R.style.answer_style);

        return radioButton;
    }

    /* This will return the CheckBox to be added inside a Radio Group */
    private CheckBox getCheckBoxAnswer(Pair<Boolean, String> answer) {
        CheckBox checkBox = new CheckBox(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F);

        int padding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        checkBox.setLayoutParams(layoutParams);
        checkBox.setPadding(padding, 0, padding, 0);
        checkBox.setTag(answer.first);
        checkBox.setText(answer.second);
        checkBox.setTextAppearance(this, R.style.answer_style);

        return checkBox;
    }

    /* This will return the Edit Text to be added inside a Radio Group */
    private EditText getEditBoxAnswer(String answer) {
        final EditText editText = new EditText(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        editText.setLayoutParams(layoutParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setSelectAllOnFocus(true);
        editText.setHint("Enter your answer");
        editText.setTag(answer);
        editText.setTextAppearance(this, R.style.answer_style);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            /* This will override the IME Action to clear focus and hide the keyboard
             * Without this, it would just close the keyboard without losing focus*/
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.clearFocus();

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

        return editText;
    }

    /* Check the answers and display the score in a custom Toast message */
    public void checkAnswer(View v) {
        LinearLayout linearLayout = findViewById(R.id.linear_layout_main);

        int score = 0;
        int correctAnswers = 0;
        int numberOfQuestion = questions.getQuestions().size();

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

                // This indicates that a question was not answered
                if (!questionIsAnswered(radioButton)) {
                    Toast.makeText(getApplicationContext(), "Please answer all the questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if the checked answers tag value is true
                if ((boolean) radioButton.getTag()) {
                    correctAnswers++;
                    score += RADIO_BUTTON_SCORE;
                }
            } else if (radioGroup.getChildAt(0) instanceof CheckBox) {
                boolean isCorrect = true;   // assume correct answers

                if (!questionIsAnswered(radioGroup)) {
                    Toast.makeText(getApplicationContext(), "Please answer all the questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int c = 0; c < radioGroup.getChildCount(); c++) {
                    CheckBox checkBox = (CheckBox) radioGroup.getChildAt(c);

                    /* incorrect answer if user did not select a correct answer OR
                    selected an incorrect answer */
                    if ((checkBox.isChecked() && !(boolean) checkBox.getTag()) ||
                            (!checkBox.isChecked() && (boolean) checkBox.getTag())) {
                        isCorrect = false;

                        break;
                    }
                }

                if (isCorrect) {
                    correctAnswers++;
                    score += CHECK_BOX_SCORE;
                }
            } else if (radioGroup.getChildAt(0) instanceof EditText) {
                EditText editText = (EditText) radioGroup.getChildAt(0);

                if (!questionIsAnswered(editText)) {
                    Toast.makeText(getApplicationContext(), "Please answer all the questions", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the answer stored in the tag matches the entered answer
                if ((editText.getTag().toString()).equalsIgnoreCase(editText.getText().toString().trim())) {
                    correctAnswers++;
                    score += EDIT_TEXT_SCORE;
                }
            }
        }

        //LayoutInflater inflater = getLayoutInflater();

//        View layout = inflater.inflate(R.layout.results_toast,
//                (ViewGroup) findViewById(R.id.custom_toast_layout_id));

// set a message
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText("Button is clicked!");
//
//// Toast...
//        Toast toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.FILL, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(layout);
//        toast.show();

        Toast.makeText(getApplicationContext(), getString(R.string.results, correctAnswers, numberOfQuestion, score), Toast.LENGTH_LONG).show();
    }


    private boolean questionIsAnswered(RadioButton radioButton) {
        return radioButton != null;
    }

    private boolean questionIsAnswered(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private boolean questionIsAnswered(RadioGroup radioGroup) {
        boolean isAnswered = false;

        // Validate user answered the question
        for (int c = 0; c < radioGroup.getChildCount(); c++) {
            CheckBox checkBox = (CheckBox) radioGroup.getChildAt(c);

            // Exit loop on first checkbox that was check
            if (checkBox.isChecked()) {
                isAnswered = true;

                break;
            }
        }

        return isAnswered;
    }
}
