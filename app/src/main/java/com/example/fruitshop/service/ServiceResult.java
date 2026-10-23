package com.example.fruitshop.service;

/**
 * Kết quả trả về chung của tầng Service: thành công kèm dữ liệu, hoặc thất bại kèm thông báo lỗi.
 * Giúp UI không phải bắt exception hay kiểm tra null rải rác.
 */
public class ServiceResult<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private ServiceResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, null);
    }

    public static <T> ServiceResult<T> success(T data, String message) {
        return new ServiceResult<>(true, data, message);
    }

    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
