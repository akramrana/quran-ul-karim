package com.akramhossain.quranulkarim.helper;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // no-op
    }

    @Override
    public void afterTextChanged(Editable s) {
        // no-op
    }
}

