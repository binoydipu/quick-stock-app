package com.binoydipu.quickstock.utilities.format;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormater {

    public NumberFormater() {}

    public static String formatPrice(double val) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "৳ " + formatter.format(val);
    }
}
