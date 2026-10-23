package com.example.fruitshop.util;

import java.util.regex.Pattern;

/** Các hàm kiểm tra dữ liệu nhập dùng chung. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9,10}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    public static final int MIN_PASSWORD_LENGTH = 6;

    private Validator() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isBlank(email) && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return !isBlank(phone) && PHONE.matcher(phone.trim()).matches();
    }

    public static boolean isValidUsername(String username) {
        return !isBlank(username) && USERNAME.matcher(username.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
}
