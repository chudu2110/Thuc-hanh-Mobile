package com.example.fruitshop.data.model;

import java.util.ArrayList;
import java.util.List;

/** Entity ánh xạ bảng Order(id, customerId, orderDate, totalAmount, status). */
public class Order {

    private long id;
    private long customerId;
    /** Định dạng "yyyy-MM-dd HH:mm:ss". */
    private String orderDate;
    private double totalAmount;
    private OrderStatus status;

    /** Thông tin bổ sung khi JOIN (không phải cột của bảng Order). */
    private String customerName;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    public Order(long id, long customerId, String orderDate, double totalAmount, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
