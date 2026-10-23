package com.example.fruitshop.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.dao.FruitDao;
import com.example.fruitshop.data.dao.OrderDao;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderItem;
import com.example.fruitshop.data.model.OrderStatus;
import com.example.fruitshop.util.DateUtils;

import java.util.Collections;
import java.util.List;

/**
 * Nghiệp vụ đơn hàng.
 * - Customer: đặt hàng (chặn hàng hết hạn / vượt tồn kho), xem lịch sử.
 * - Admin: xem tất cả đơn, cập nhật trạng thái theo luồng hợp lệ.
 * Mọi thao tác ghi đều chạy trong transaction để tồn kho và đơn hàng luôn nhất quán.
 */
public class OrderService {

    public static final int MAX_QUANTITY_PER_ITEM = 100;

    private final DatabaseHelper dbHelper;
    private final OrderDao orderDao;
    private final FruitDao fruitDao;

    public OrderService(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.orderDao = new OrderDao(dbHelper);
        this.fruitDao = new FruitDao(dbHelper);
    }

    // ===== Customer =====

    public ServiceResult<Order> placeOrder(long customerId, long fruitId, int quantity) {
        return placeOrder(customerId, Collections.singletonList(new OrderItem(fruitId, quantity)));
    }

    /**
     * Đặt hàng nhiều sản phẩm. Kiểm tra lại từng sản phẩm ngay trong transaction
     * (dữ liệu trên màn hình có thể đã cũ), sau đó trừ tồn kho và lưu đơn.
     */
    public ServiceResult<Order> placeOrder(long customerId, List<OrderItem> requestItems) {
        if (customerId <= 0) {
            return ServiceResult.failure("Không xác định được khách hàng, vui lòng đăng nhập lại");
        }
        if (requestItems == null || requestItems.isEmpty()) {
            return ServiceResult.failure("Đơn hàng chưa có sản phẩm");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Order order = new Order(0, customerId, DateUtils.now(), 0, OrderStatus.PENDING);
            double total = 0;

            for (OrderItem item : requestItems) {
                Fruit fruit = fruitDao.findById(db, item.getFruitId());
                String error = validateItem(fruit, item.getQuantity());
                if (error != null) {
                    return ServiceResult.failure(error);
                }
                item.setFruitName(fruit.getName());
                item.setUnitPrice(fruit.getPrice());
                total += item.getSubtotal();
                fruitDao.updateQuantity(db, fruit.getId(), fruit.getQuantity() - item.getQuantity());
            }

            order.setTotalAmount(total);
            if (orderDao.insertOrder(db, order) == -1) {
                return ServiceResult.failure("Không thể tạo đơn hàng");
            }
            for (OrderItem item : requestItems) {
                item.setOrderId(order.getId());
                orderDao.insertItem(db, item);
            }
            order.setItems(requestItems);
            db.setTransactionSuccessful();
            return ServiceResult.success(order, "Đặt hàng thành công! Mã đơn #" + order.getId());
        } finally {
            db.endTransaction();
        }
    }

    /** Quy tắc nghiệp vụ cho một dòng đặt hàng. Trả về thông báo lỗi hoặc null nếu hợp lệ. */
    private String validateItem(Fruit fruit, int quantity) {
        if (fruit == null) {
            return "Sản phẩm không còn tồn tại";
        }
        if (fruit.isExpired()) {
            return "\"" + fruit.getName() + "\" đã hết hạn sử dụng, không thể đặt hàng";
        }
        if (quantity <= 0) {
            return "Số lượng phải lớn hơn 0";
        }
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            return "Mỗi sản phẩm đặt tối đa " + MAX_QUANTITY_PER_ITEM + " kg";
        }
        if (quantity > fruit.getQuantity()) {
            return "\"" + fruit.getName() + "\" chỉ còn " + fruit.getQuantity() + " kg";
        }
        return null;
    }

    public List<Order> getOrderHistory(long customerId) {
        return orderDao.findByCustomer(customerId);
    }

    // ===== Admin =====

    /** status == null → tất cả đơn. */
    public List<Order> getAllOrders(OrderStatus status) {
        return orderDao.findAll(status);
    }

    /**
     * Cập nhật trạng thái theo luồng hợp lệ. Huỷ đơn sẽ hoàn lại tồn kho
     * cho các sản phẩm vẫn còn trong hệ thống.
     */
    public ServiceResult<Order> updateStatus(long orderId, OrderStatus newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Order order = orderDao.findById(db, orderId);
            if (order == null) {
                return ServiceResult.failure("Đơn hàng không tồn tại");
            }
            OrderStatus current = order.getStatus();
            if (!current.canTransitionTo(newStatus)) {
                return ServiceResult.failure("Không thể chuyển từ \"" + current.getLabel()
                        + "\" sang \"" + newStatus.getLabel() + "\"");
            }
            if (newStatus == OrderStatus.CANCELLED) {
                restoreStock(db, order);
            }
            orderDao.updateStatus(db, orderId, newStatus);
            order.setStatus(newStatus);
            db.setTransactionSuccessful();
            return ServiceResult.success(order, "Đơn #" + orderId + " → " + newStatus.getLabel());
        } finally {
            db.endTransaction();
        }
    }

    private void restoreStock(SQLiteDatabase db, Order order) {
        for (OrderItem item : order.getItems()) {
            Fruit fruit = fruitDao.findById(db, item.getFruitId());
            if (fruit != null) {
                fruitDao.updateQuantity(db, fruit.getId(), fruit.getQuantity() + item.getQuantity());
            }
        }
    }
}
