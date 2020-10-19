package com.example.sprintone.numberfilter;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        try {
            String newVal = dest.toString().substring(0, dStart) + dest.toString().substring(dEnd);
            newVal = newVal.substring(0, dStart) + source.toString() + newVal.substring(dStart);
            if(newVal.equalsIgnoreCase("-") && min < 0)
                return null;
            int input = Integer.parseInt(newVal);
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
