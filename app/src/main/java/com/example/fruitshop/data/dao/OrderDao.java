package com.example.fruitshop.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseContract.CustomerTable;
import com.example.fruitshop.data.db.DatabaseContract.OrderItemTable;
import com.example.fruitshop.data.db.DatabaseContract.OrderTable;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderItem;
import com.example.fruitshop.data.model.OrderStatus;

import java.util.ArrayList;
import java.util.List;

/** Truy xuất dữ liệu bảng Order và bảng chi tiết OrderItem. */
public class OrderDao extends BaseDao<Order> {

    private static final String COL_CUSTOMER_NAME = "customerName";

    /** SELECT o.*, c.fullname AS customerName FROM orders o JOIN customers c ... */
    private static final String SELECT_WITH_CUSTOMER = "SELECT o.*, c." + CustomerTable.FULLNAME
            + " AS " + COL_CUSTOMER_NAME
            + " FROM " + OrderTable.TABLE + " o"
            + " LEFT JOIN " + CustomerTable.TABLE + " c ON c." + CustomerTable.ID
            + " = o." + OrderTable.CUSTOMER_ID;

    private static final String ORDER_BY_NEWEST = " ORDER BY o." + OrderTable.ORDER_DATE
            + " DESC, o." + OrderTable.ID + " DESC";

    public OrderDao(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Order mapRow(Cursor c) {
        Order order = new Order(
                getLong(c, OrderTable.ID),
                getLong(c, OrderTable.CUSTOMER_ID),
                getString(c, OrderTable.ORDER_DATE),
                getDouble(c, OrderTable.TOTAL_AMOUNT),
                OrderStatus.fromString(getString(c, OrderTable.STATUS)));
        int nameIdx = c.getColumnIndex(COL_CUSTOMER_NAME);
        if (nameIdx >= 0) {
            order.setCustomerName(c.getString(nameIdx));
        }
        return order;
    }

    // ===== Truy vấn =====

    public Order findById(long orderId) {
        return findById(readable(), orderId);
    }

    public Order findById(SQLiteDatabase db, long orderId) {
        Cursor c = db.rawQuery(SELECT_WITH_CUSTOMER + " WHERE o." + OrderTable.ID + " = ?",
                new String[]{String.valueOf(orderId)});
        Order order = mapFirst(c);
        if (order != null) {
            order.setItems(findItems(db, orderId));
        }
        return order;
    }

    /** Lịch sử đơn của một khách hàng (mới nhất trước). */
    public List<Order> findByCustomer(long customerId) {
        Cursor c = readable().rawQuery(SELECT_WITH_CUSTOMER
                        + " WHERE o." + OrderTable.CUSTOMER_ID + " = ?" + ORDER_BY_NEWEST,
                new String[]{String.valueOf(customerId)});
        return withItems(mapList(c));
    }

    /** Toàn bộ đơn (Admin); status == null nghĩa là không lọc. */
    public List<Order> findAll(OrderStatus status) {
        Cursor c;
        if (status == null) {
            c = readable().rawQuery(SELECT_WITH_CUSTOMER + ORDER_BY_NEWEST, null);
        } else {
            c = readable().rawQuery(SELECT_WITH_CUSTOMER + " WHERE o." + OrderTable.STATUS + " = ?"
                    + ORDER_BY_NEWEST, new String[]{status.name()});
        }
        return withItems(mapList(c));
    }

    public List<OrderItem> findItems(SQLiteDatabase db, long orderId) {
        List<OrderItem> items = new ArrayList<>();
        Cursor c = db.query(OrderItemTable.TABLE, null, OrderItemTable.ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)}, null, null, OrderItemTable.ID);
        try {
            int fruitIdIdx = c.getColumnIndexOrThrow(OrderItemTable.FRUIT_ID);
            while (c.moveToNext()) {
                items.add(new OrderItem(
                        getLong(c, OrderItemTable.ID),
                        getLong(c, OrderItemTable.ORDER_ID),
                        c.isNull(fruitIdIdx) ? 0 : c.getLong(fruitIdIdx),
                        getString(c, OrderItemTable.FRUIT_NAME),
                        getInt(c, OrderItemTable.QUANTITY),
                        getDouble(c, OrderItemTable.UNIT_PRICE)));
            }
        } finally {
            c.close();
        }
        return items;
    }

    private List<Order> withItems(List<Order> orders) {
        SQLiteDatabase db = readable();
        for (Order o : orders) {
            o.setItems(findItems(db, o.getId()));
        }
        return orders;
    }

    // ===== Ghi (luôn chạy trong transaction do OrderService quản lý) =====

    public long insertOrder(SQLiteDatabase db, Order order) {
        ContentValues v = new ContentValues();
        v.put(OrderTable.CUSTOMER_ID, order.getCustomerId());
        v.put(OrderTable.ORDER_DATE, order.getOrderDate());
        v.put(OrderTable.TOTAL_AMOUNT, order.getTotalAmount());
        v.put(OrderTable.STATUS, order.getStatus().name());
        long id = db.insert(OrderTable.TABLE, null, v);
        order.setId(id);
        return id;
    }

    public long insertItem(SQLiteDatabase db, OrderItem item) {
        ContentValues v = new ContentValues();
        v.put(OrderItemTable.ORDER_ID, item.getOrderId());
        v.put(OrderItemTable.FRUIT_ID, item.getFruitId());
        v.put(OrderItemTable.FRUIT_NAME, item.getFruitName());
        v.put(OrderItemTable.QUANTITY, item.getQuantity());
        v.put(OrderItemTable.UNIT_PRICE, item.getUnitPrice());
        long id = db.insert(OrderItemTable.TABLE, null, v);
        item.setId(id);
        return id;
    }

    public boolean updateStatus(SQLiteDatabase db, long orderId, OrderStatus status) {
        ContentValues v = new ContentValues();
        v.put(OrderTable.STATUS, status.name());
        return db.update(OrderTable.TABLE, v, OrderTable.ID + " = ?",
                new String[]{String.valueOf(orderId)}) > 0;
    }
}
