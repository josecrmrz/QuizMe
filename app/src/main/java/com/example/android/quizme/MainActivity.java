package com.example.android.quizme;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

class MainActivity extends AppCompatActivity {

    static Questions questions = null;

    static final int EDIT_TEXT_SCORE = 10;
    static final int RADIO_BUTTON_SCORE = 10;
    static final int CHECK_BOX_SCORE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createQuestionViews();
    }

    public void createQuestionViews() {
        LinearLayout linearLayoutMain = findViewById(R.id.linear_layout_main);

        clearCardViews(linearLayoutMain);           // Clear the layout
        loadQuestions();                            // Load the questions
        addCardViewQuestions(linearLayoutMain);     // Generate the layout for the questions
    }

    /* Reset the questions */
    public void resetQuestionViews(View v) {
        ScrollView sv = findViewById(R.id.scroll_view_main);
        sv.smoothScrollTo(0, sv.getTop());

        createQuestionViews();
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
            // create the class of questions
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
            // Generate a EditText question
            EditText editText = getEditBoxAnswer(((TextInputQuestion) question).getAnswer());
            radioGroup.addView(editText);
        } else if (((ChoiceQuestion) question).isSingleAnswerQuestion()) {
            // Generate a RadioButton question
            for (int i = 0; i < ((ChoiceQuestion) question).getAnswers().size(); i++) {
                RadioButton radioButton = getRadioButtonAnswer(((ChoiceQuestion) question).getAnswers().get(i));
                radioGroup.addView(radioButton);
            }
        } else if (((ChoiceQuestion) question).isMultipleAnswerQuestion()) {
            // Generate a CheckBox Question
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
        radioButton.setTextAppearance(this, R.style.radio_button_style);
        radioButton.setTag(answer.first);       // Tag indicates if the answer is a correct answer
        radioButton.setText(answer.second);     // Set the text to the answer choice

        return radioButton;
    }

    /* This will return the CheckBox to be added inside a Radio Group */
    private CheckBox getCheckBoxAnswer(Pair<Boolean, String> answer) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setTextAppearance(this, R.style.check_box_style);
        checkBox.setTag(answer.first);      // Tag indicates if the answer is a correct answer
        checkBox.setText(answer.second);    // Set the text to the answer choice

        return checkBox;
    }

    /* This will return the Edit Text to be added inside a Radio Group */
    private EditText getEditBoxAnswer(String answer) {
        final EditText editText = new EditText(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        editText.setLayoutParams(layoutParams);     // required since the style does not appear to set width to wrap_content programmatically
        editText.setTextAppearance(this, R.style.edit_text_style);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint(R.string.enter_name_hint);
        editText.setTag(answer);        // The tag holds the answer to compare later

        /* This will override the IME Action to clear focus and hide the keyboard
         * Without this, it would just close the keyboard without losing focus*/
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        // Loop through all Card Views
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            // This will skip anything that is not a CardView
            if (!(linearLayout.getChildAt(i) instanceof CardView)) {
                continue;
            }

            // Break down the views
            CardView cardView = (CardView) linearLayout.getChildAt(i);
            LinearLayout linearLayout1 = (LinearLayout) cardView.getChildAt(0);
            RadioGroup radioGroup = (RadioGroup) linearLayout1.getChildAt(1);

            if (radioGroup.getChildAt(0) instanceof RadioButton) {
                RadioButton radioButtonAnswer = findViewById(radioGroup.getCheckedRadioButtonId());
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(radioGroup.indexOfChild(radioButtonAnswer));

                // Check for unanswered Questions
                if (radioButton == null) {
                    toastForUnansweredQuestions();
                    return;
                }

                // if the checked answers tag value is true
                if ((boolean) radioButton.getTag()) {
                    correctAnswers++;
                    score += MainActivity.RADIO_BUTTON_SCORE;
                }
            } else if (radioGroup.getChildAt(0) instanceof CheckBox) {
                boolean isCorrect = true;   // assume correct answers

                // Check for unanswered Questions
                if (!questionIsAnswered(radioGroup)) {
                    toastForUnansweredQuestions();
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
                    score += MainActivity.CHECK_BOX_SCORE;
                }
            } else if (radioGroup.getChildAt(0) instanceof EditText) {
                EditText editText = (EditText) radioGroup.getChildAt(0);

                // Check that the user entered an answer
                if (editText.getText().toString().trim().isEmpty()) {
                    toastForUnansweredQuestions();
                    return;
                }

                // Check if the answer stored in the tag matches the entered answer
                if ((editText.getTag().toString()).equalsIgnoreCase(editText.getText().toString().trim())) {
                    correctAnswers++;
                    score += MainActivity.EDIT_TEXT_SCORE;
                }
            }
        }

        // Display the results in a custom Toast Message
        showResults(getString(R.string.results, correctAnswers, numberOfQuestion, score));
    }

    private void toastForUnansweredQuestions() {
        Toast.makeText(this, R.string.answer_all_questions, Toast.LENGTH_SHORT).show();
    }

    /* Display a custom Toast of the users results */
    private void showResults(String results) {
        View layout = getLayoutInflater().inflate(R.layout.results_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));

        TextView text = layout.findViewById(R.id.text_view_results);
        text.setText(results);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.FILL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /* Check that the user answered the CheckBox question */
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
