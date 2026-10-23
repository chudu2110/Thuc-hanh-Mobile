package com.example.fruitshop.data.model;

/** Trạng thái hạn sử dụng của trái cây. */
public enum ExpiryStatus {
    /** Còn hạn trên {@link #NEAR_EXPIRY_DAYS} ngày. */
    FRESH("Còn hạn"),
    /** Còn hạn nhưng sắp hết (≤ {@link #NEAR_EXPIRY_DAYS} ngày). */
    NEAR_EXPIRY("Sắp hết hạn"),
    /** Đã qua ngày hết hạn – không được bán. */
    EXPIRED("Đã hết hạn");

    public static final int NEAR_EXPIRY_DAYS = 3;

    private final String label;

    ExpiryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Quy tắc nghiệp vụ: hết hạn khi ngày hết hạn < hôm nay (ngày hết hạn vẫn được bán). */
    public static ExpiryStatus fromDaysLeft(long daysLeft) {
        if (daysLeft < 0) {
            return EXPIRED;
        }
        return daysLeft <= NEAR_EXPIRY_DAYS ? NEAR_EXPIRY : FRESH;
    }
}
