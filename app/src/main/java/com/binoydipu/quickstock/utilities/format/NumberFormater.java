package com.binoydipu.quickstock.utilities.format;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NumberFormater {

    public NumberFormater() {}

    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "৳ " + formatter.format(price);
    }

    public static String convertMillisToDate(long expireDateInMillis) {
        Date date = new Date(expireDateInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
