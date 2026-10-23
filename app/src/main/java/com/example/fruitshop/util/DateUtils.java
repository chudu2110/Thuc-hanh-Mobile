package com.example.fruitshop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Xử lý ngày tháng.
 * - Ngày hết hạn lưu trong DB theo định dạng ISO "yyyy-MM-dd" (so sánh chuỗi == so sánh ngày).
 * - Hiển thị cho người dùng theo "dd/MM/yyyy".
 */
public final class DateUtils {

    public static final String DB_DATE = "yyyy-MM-dd";
    public static final String DB_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE = "dd/MM/yyyy";
    public static final String DISPLAY_DATE_TIME = "dd/MM/yyyy HH:mm";

    private DateUtils() {
    }

    private static SimpleDateFormat format(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        sdf.setLenient(false);
        return sdf;
    }

    /** Ngày hôm nay dạng "yyyy-MM-dd". */
    public static String today() {
        return format(DB_DATE).format(new Date());
    }

    /** Thời điểm hiện tại dạng "yyyy-MM-dd HH:mm:ss". */
    public static String now() {
        return format(DB_DATE_TIME).format(new Date());
    }

    /** Cộng thêm số ngày vào hôm nay (âm = lùi lại), trả về "yyyy-MM-dd". */
    public static String daysFromToday(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return format(DB_DATE).format(cal.getTime());
    }

    public static String toDbDate(int year, int month0, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month0 + 1, day);
    }

    public static boolean isValidDbDate(String date) {
        return parse(date, DB_DATE) != null;
    }

    /** "yyyy-MM-dd" → "dd/MM/yyyy"; trả về nguyên chuỗi nếu sai định dạng. */
    public static String toDisplayDate(String dbDate) {
        Date d = parse(dbDate, DB_DATE);
        return d == null ? dbDate : format(DISPLAY_DATE).format(d);
    }

    /** "yyyy-MM-dd HH:mm:ss" → "dd/MM/yyyy HH:mm". */
    public static String toDisplayDateTime(String dbDateTime) {
        Date d = parse(dbDateTime, DB_DATE_TIME);
        return d == null ? dbDateTime : format(DISPLAY_DATE_TIME).format(d);
    }

    /** Số ngày từ hôm nay tới ngày dbDate (âm nếu đã qua). */
    public static long daysUntil(String dbDate) {
        Date target = parse(dbDate, DB_DATE);
        Date today = parse(today(), DB_DATE);
        if (target == null || today == null) {
            return Long.MIN_VALUE;
        }
        // Làm tròn để tránh lệch do giờ mùa hè (DST)
        return Math.round((target.getTime() - today.getTime()) / (double) TimeUnit.DAYS.toMillis(1));
    }

    /** Trả về [year, month0, day] để khởi tạo DatePickerDialog. */
    public static int[] toYmd(String dbDate) {
        Date d = parse(dbDate, DB_DATE);
        Calendar cal = Calendar.getInstance();
        if (d != null) {
            cal.setTime(d);
        }
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
    }

    private static Date parse(String value, String pattern) {
        if (value == null) {
            return null;
        }
        try {
            return format(pattern).parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
