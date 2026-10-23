package com.example.fruitshop.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Định dạng hiển thị tiền tệ. */
public final class FormatUtils {

    private FormatUtils() {
    }

    /** 85000 → "85.000 đ" */
    public static String money(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(amount) + " đ";
    }

    /** Đọc số an toàn từ chuỗi nhập; trả về defaultValue nếu không hợp lệ. */
    public static double parseDouble(String s, double defaultValue) {
        try {
            return Double.parseDouble(s.trim());
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }
}
