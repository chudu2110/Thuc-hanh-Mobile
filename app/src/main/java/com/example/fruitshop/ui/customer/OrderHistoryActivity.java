package com.example.fruitshop.ui.customer;

import androidx.appcompat.app.AlertDialog;

import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderStatus;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.ui.base.BaseOrderListActivity;

import java.util.ArrayList;
import java.util.List;

/** Customer: xem lịch sử đơn hàng của chính mình. */
public class OrderHistoryActivity extends BaseOrderListActivity {

    @Override
    protected Role requiredRole() {
        return Role.CUSTOMER;
    }

    @Override
    protected String screenTitle() {
        return "Lịch sử đơn hàng";
    }

    @Override
    protected List<Order> loadOrders(OrderStatus status) {
        List<Order> all = orderService.getOrderHistory(session.getCustomerId());
        if (status == null) {
            return all;
        }
        List<Order> filtered = new ArrayList<>();
        for (Order o : all) {
            if (o.getStatus() == status) {
                filtered.add(o);
            }
        }
        return filtered;
    }

    @Override
    protected boolean showCustomerName() {
        return false;
    }

    @Override
    public void onOrderClick(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Đơn #" + order.getId())
                .setMessage(buildOrderDetail(order))
                .setPositiveButton("Đóng", null)
                .show();
    }
}
