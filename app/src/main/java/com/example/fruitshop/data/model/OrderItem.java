package com.example.fruitshop.data.model;

/** Một dòng trong đơn hàng: trái cây nào, bao nhiêu kg, giá lúc mua. */
public class OrderItem {

    private long id;
    private long orderId;
    private long fruitId;
    private String fruitName;
    private int quantity;
    private double unitPrice;

    public OrderItem() {
    }

    /** Dùng khi khách tạo yêu cầu đặt hàng: chỉ cần fruitId + số lượng. */
    public OrderItem(long fruitId, int quantity) {
        this.fruitId = fruitId;
        this.quantity = quantity;
    }

    public OrderItem(long id, long orderId, long fruitId, String fruitName, int quantity, double unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.fruitId = fruitId;
        this.fruitName = fruitName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getFruitId() {
        return fruitId;
    }

    public void setFruitId(long fruitId) {
        this.fruitId = fruitId;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
