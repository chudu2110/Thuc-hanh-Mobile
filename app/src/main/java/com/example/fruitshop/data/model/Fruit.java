package com.example.fruitshop.data.model;

import com.example.fruitshop.util.DateUtils;

import java.io.Serializable;

/** Entity ánh xạ bảng Fruit(id, name, category, price, quantity, expiryDate, description). */
public class Fruit implements Serializable {

    private long id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    /** Định dạng "yyyy-MM-dd". */
    private String expiryDate;
    private String description;

    public Fruit() {
    }

    public Fruit(long id, String name, String category, double price, int quantity,
                 String expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    // ===== Nghiệp vụ hạn sử dụng =====

    public long getDaysUntilExpiry() {
        return DateUtils.daysUntil(expiryDate);
    }

    public ExpiryStatus getExpiryStatus() {
        return ExpiryStatus.fromDaysLeft(getDaysUntilExpiry());
    }

    public boolean isExpired() {
        return getExpiryStatus() == ExpiryStatus.EXPIRED;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    /** Khách chỉ được đặt khi còn hàng và chưa hết hạn. */
    public boolean isOrderable() {
        return isInStock() && !isExpired();
    }

    // ===== Getter / Setter =====

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
