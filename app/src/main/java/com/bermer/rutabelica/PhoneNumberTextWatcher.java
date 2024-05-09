package com.bermer.rutabelica;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneNumberTextWatcher implements TextWatcher {

    private EditText editText;

    public PhoneNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No necesitamos implementar nada aquí.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No necesitamos implementar nada aquí.
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        if (text.length() == 10) {
            String formattedNumber = text.substring(0, 3) + "-" + text.substring(3, 6) + "-" + text.substring(6);
            editText.removeTextChangedListener(this);
            editText.setText(formattedNumber);
            editText.setSelection(formattedNumber.length());
            editText.addTextChangedListener(this);
        }
    }
}
