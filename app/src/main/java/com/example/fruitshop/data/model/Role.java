package com.example.fruitshop.data.model;

/** Vai trò người dùng trong hệ thống. */
public enum Role {
    ADMIN,
    CUSTOMER;

    /** Chuyển chuỗi lưu trong DB/SharedPreferences thành enum, mặc định CUSTOMER nếu không hợp lệ. */
    public static Role fromString(String value) {
        if (value == null) {
            return CUSTOMER;
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CUSTOMER;
        }
    }
}
