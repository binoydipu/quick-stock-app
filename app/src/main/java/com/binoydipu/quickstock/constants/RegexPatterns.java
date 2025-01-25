package com.binoydipu.quickstock.constants;

import java.util.regex.Pattern;

public class RegexPatterns {
    public static final Pattern namePattern = Pattern.compile("^[a-zA-Z .-]+$");
    public static final Pattern idPattern = Pattern.compile("^[0-9]{5}$"); // 5 digits number
    public static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,63}$");
    public static final Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,32}$"); // aA@12345
    public static final Pattern phonePattern = Pattern.compile("^(\\+88)?01[2-9][0-9]{8}$"); // (+88) 01 7 12345678

    public RegexPatterns() {}
}
