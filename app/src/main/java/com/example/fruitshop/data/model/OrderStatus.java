package com.example.fruitshop.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trạng thái đơn hàng và luồng chuyển trạng thái hợp lệ (State machine):
 * <pre>
 *   PENDING ──► CONFIRMED ──► COMPLETED
 *      │            │
 *      └────────────┴──► CANCELLED (hoàn lại tồn kho)
 * </pre>
 */
public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã huỷ");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Các trạng thái có thể chuyển tới từ trạng thái hiện tại. */
    public List<OrderStatus> nextStatuses() {
        List<OrderStatus> next = new ArrayList<>();
        switch (this) {
            case PENDING:
                next.add(CONFIRMED);
                next.add(CANCELLED);
                break;
            case CONFIRMED:
                next.add(COMPLETED);
                next.add(CANCELLED);
                break;
            default:
                return Collections.emptyList();
        }
        return next;
    }

    public boolean canTransitionTo(OrderStatus target) {
        return nextStatuses().contains(target);
    }

    public boolean isFinal() {
        return nextStatuses().isEmpty();
    }

    public static OrderStatus fromString(String value) {
        try {
            return OrderStatus.valueOf(value);
        } catch (RuntimeException e) {
            return PENDING;
        }
    }
}
