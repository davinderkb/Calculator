package com.example.calculator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<View> buttons = findViewById(R.id.mainLayout).getTouchables();
        for (View button : buttons) {
            button.setOnClickListener(this);
        }
        Button buttonDel = findViewById(R.id.buttonDel);
        buttonDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText inputTextView = findViewById(R.id.inputText);
                TextView outputView = findViewById(R.id.outputView);
                setClearEffect(inputTextView);
                inputTextView.setText("");
                outputView.setText("");
                return true;
            }

            private void setClearEffect(EditText inputTextView) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(300000);
                inputTextView.setBackgroundColor(0xff78c5f9);
                final ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(inputTextView,
                        "backgroundColor",
                        new ArgbEvaluator(),
                        0xff78c5f9,
                        0xFFFFFFFF);
                backgroundColorAnimator.setDuration(400);
                backgroundColorAnimator.start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        EditText inputTextView = findViewById(R.id.inputText);
        CharSequence text = inputTextView.getText();
        switch (view.getId()) {
            case R.id.buttonDel:
                if (text.length() > 3) {
                    Toast.makeText(this, getString(R.string.toast_warn_del_long_press), Toast.LENGTH_SHORT).show();
                }
                performDelOperation(inputTextView, text);
                break;
            case R.id.buttonResult:
                showResult(inputTextView, text);
                break;
            default:
                setTextInput((Button) view, inputTextView, text);
                break;
        }
    }

    private void setTextInput(Button view, EditText inputTextView, CharSequence text) {
        String currentInput = null;
        String lastInput = null;
        currentInput = ((Button) view).getText().toString();
        if (text.length() > 0) {
            lastInput = Character.toString(text.charAt(text.length() - 1));
        }

        if (text.length() == 0
                && (currentInput.equals(getString(R.string.multiply_operator))
                || currentInput.equals(getString(R.string.divide_operator))
                || currentInput.equals(getString(R.string.plus_operator)))) {
            if (!currentInput.equals(getString(R.string.plus_operator)))
                Toast.makeText(this, getString(R.string.toast_warn_invalid_first_input), Toast.LENGTH_SHORT).show();
        } else if (lastInput != null
                && ((isOperator(currentInput) && isOperator(lastInput))
                || (currentInput.equals(".") && lastInput.equals(".")))) {
            if (currentInput.equals(lastInput)) {
                Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
            } else if (currentInput.equals(getString(R.string.minus_operator))
                    && !lastInput.equals(getString(R.string.plus_operator))) {
                inputTextView.setText(text + ((Button) view).getText().toString());
            } else if (isOperator(currentInput)
                    && lastInput.equals(getString(R.string.minus_operator))
                    && isOperator(Character.toString(text.charAt(text.length() - 2)))) {
                inputTextView.setText(text.subSequence(0, text.length() - 2) + ((Button) view).getText().toString());
            } else {
                inputTextView.setText(text.subSequence(0, text.length() - 1) + ((Button) view).getText().toString());
            }
        } else if (lastInput != null
                && isOperator(currentInput)
                && lastInput.equals(".")
                && isOperator(Character.toString(text.charAt(text.length() - 2)))) {
            inputTextView.setText(text.subSequence(0, text.length() - 2) + ((Button) view).getText().toString());
        } else {
            inputTextView.setText(text + ((Button) view).getText().toString());
        }
        inputTextView.setSelection(inputTextView.length());
    }

    private boolean isOperator(String input) {
        if (input.equals(getString(R.string.divide_operator))
                || input.equals(getString(R.string.plus_operator))
                || input.equals(getString(R.string.multiply_operator))
                || input.equals(getString(R.string.minus_operator))) {
            return true;
        }
        return false;
    }

    private void showResult(EditText inputTextView, CharSequence text) {
        if (text.length() > 2 && isOperator(Character.toString(text.charAt(text.length() - 1)))) {
            Toast.makeText(this, getString(R.string.last_number_missing), Toast.LENGTH_SHORT).show();
        }
        if (text.length() > 2 && isOperatorPresent(text) && isNumber(text.charAt(text.length() - 1))) {
            TextView outputView = findViewById(R.id.outputView);
            try {
                final BigDecimal result = calculateResult(text.toString());
                if(result.toString().length()>=16){
                    inputTextView.setText(String.valueOf(result.doubleValue()));
                    outputView.setText(text + " = " + String.valueOf(result.doubleValue()));
                }else{
                    inputTextView.setText(String.valueOf(result));
                    outputView.setText(text + " = " + String.valueOf(result));
                }
                outputView.setMovementMethod(new ScrollingMovementMethod());
            } catch (Exception e) {
                outputView.setText("");
                inputTextView.setText("");
                Toast.makeText(this, getString(R.string.toast_warn_invalid_equation), Toast.LENGTH_LONG).show();
            }

        }
    }

    private boolean isNumber(char val) {
        try {
            Integer.parseInt(String.valueOf(val));
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * It shall check if input equation has any operator present
     *
     * @param text
     * @return true if contains any operator
     */
    private boolean isOperatorPresent(CharSequence text) {
        //We shall check for operator from 2nd character as first can be '-'.
        String temp = String.valueOf(text.subSequence(1, text.length()));
        return temp.contains(getString(R.string.minus_operator)) || temp.contains(getString(R.string.plus_operator)) || temp.contains(getString(R.string.multiply_operator)) || temp.contains(getString(R.string.divide_operator));
    }

    private void performDelOperation(EditText inputTextView, CharSequence text) {
        if (text.length() > 0) {
            inputTextView.setText(text.subSequence(0, text.length() - 1));
        }
    }

    private BigDecimal calculateResult(String text) {
        double result = 0;
        int loopInit = 1;
        String[] tokens = text.split("(?<=[-+x/])|(?=[-+x/])");
        if (tokens[0].equals("")) {
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        ArrayList<String> bodmasOrderedList = new ArrayList<String>();
        if (tokens[0].equals(getString(R.string.minus_operator))) {
            bodmasOrderedList.add(tokens[0] + tokens[1]);
            loopInit = loopInit + 1;
        } else {
            bodmasOrderedList.add(tokens[0]);
        }
        for (int i = loopInit; i < tokens.length; i = i + 2) {
            switch (tokens[i]) {
                case "x":
                    if (tokens[i + 1].equals(getString(R.string.minus_operator))) {
                        bodmasOrderedList.set(bodmasOrderedList.size() - 1, Double.toString(Double.parseDouble(bodmasOrderedList.get(bodmasOrderedList.size() - 1)) * Double.parseDouble(getString(R.string.minus_operator) + tokens[i + 2])));
                        i = i + 1;
                    } else
                        bodmasOrderedList.set(bodmasOrderedList.size() - 1, Double.toString(Double.parseDouble(bodmasOrderedList.get(bodmasOrderedList.size() - 1)) * Double.parseDouble(tokens[i + 1])));
                    break;
                case "/":
                    if (tokens[i + 1].equals(getString(R.string.minus_operator))) {
                        bodmasOrderedList.set(bodmasOrderedList.size() - 1, Double.toString(Double.parseDouble(bodmasOrderedList.get(bodmasOrderedList.size() - 1)) / Double.parseDouble(getString(R.string.minus_operator) + tokens[i + 2])));
                        i = i + 1;
                    } else
                        bodmasOrderedList.set(bodmasOrderedList.size() - 1, Double.toString(Double.parseDouble(bodmasOrderedList.get(bodmasOrderedList.size() - 1)) / Double.parseDouble(tokens[i + 1])));
                    break;
                default:
                    bodmasOrderedList.add(tokens[i]);
                    bodmasOrderedList.add(tokens[i + 1]);

            }
        }
        result = Double.parseDouble(bodmasOrderedList.get(0));
        for (int i = 1; i < bodmasOrderedList.size(); i++) {
            switch (bodmasOrderedList.get(i)) {
                case "+":
                    result = result + Double.parseDouble(bodmasOrderedList.get(i + 1));
                    break;
                case "-":
                    result = result - Double.parseDouble(bodmasOrderedList.get(i + 1));
                    break;
            }
        }
        BigDecimal bigDecResult = new BigDecimal(result);
        //DecimalFormat fmt = new DecimalFormat("##.##");
        return bigDecResult;
    }

}

