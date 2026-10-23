package com.example.fruitshop.ui.admin;

import androidx.appcompat.app.AlertDialog;

import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderStatus;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.ui.base.BaseOrderListActivity;

import java.util.List;

/** Admin: xem tất cả đơn hàng và cập nhật trạng thái (Pending → Confirmed → Completed / Cancelled). */
public class AdminOrderListActivity extends BaseOrderListActivity {

    @Override
    protected Role requiredRole() {
        return Role.ADMIN;
    }

    @Override
    protected String screenTitle() {
        return "Quản lý đơn hàng";
    }

    @Override
    protected List<Order> loadOrders(OrderStatus status) {
        return orderService.getAllOrders(status);
    }

    @Override
    protected boolean showCustomerName() {
        return true;
    }

    @Override
    public void onOrderClick(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Đơn #" + order.getId())
                .setMessage(buildOrderDetail(order))
                .setNegativeButton("Đóng", null);
        if (!order.getStatus().isFinal()) {
            builder.setPositiveButton("Cập nhật trạng thái", (d, w) -> chooseNextStatus(order));
        }
        builder.show();
    }

    private void chooseNextStatus(Order order) {
        List<OrderStatus> options = order.getStatus().nextStatuses();
        String[] labels = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            labels[i] = options.get(i).getLabel();
        }
        new AlertDialog.Builder(this)
                .setTitle("Chuyển đơn #" + order.getId() + " sang")
                .setItems(labels, (d, which) -> applyStatus(order, options.get(which)))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void applyStatus(Order order, OrderStatus target) {
        Runnable action = () -> {
            ServiceResult<Order> result = orderService.updateStatus(order.getId(), target);
            toast(result.getMessage());
            refresh();
        };
        if (target == OrderStatus.CANCELLED) {
            confirm("Huỷ đơn hàng", "Huỷ đơn #" + order.getId() + "? Tồn kho sẽ được hoàn lại.", action);
        } else {
            action.run();
        }
    }
}
