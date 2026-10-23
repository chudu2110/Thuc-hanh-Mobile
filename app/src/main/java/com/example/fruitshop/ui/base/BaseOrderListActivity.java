package com.example.fruitshop.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderItem;
import com.example.fruitshop.data.model.OrderStatus;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.service.OrderService;
import com.example.fruitshop.ui.adapter.OrderAdapter;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Khung chung cho màn hình danh sách đơn hàng (Template Method):
 * bộ lọc trạng thái + RecyclerView. Lớp con quyết định nguồn dữ liệu và hành động khi chạm.
 */
public abstract class BaseOrderListActivity extends BaseActivity
        implements OrderAdapter.OnOrderClickListener {

    protected OrderService orderService;
    private OrderAdapter adapter;
    private Spinner spStatusFilter;
    private TextView tvSummary;
    private TextView tvEmpty;

    protected abstract Role requiredRole();

    protected abstract String screenTitle();

    /** status == null nghĩa là "Tất cả". */
    protected abstract List<Order> loadOrders(OrderStatus status);

    protected abstract boolean showCustomerName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireRole(requiredRole())) {
            return;
        }
        setContentView(R.layout.activity_order_list);
        setupBackButton(screenTitle());
        orderService = new OrderService(this);

        tvSummary = findViewById(R.id.tvSummary);
        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(showCustomerName(), this);
        recyclerView.setAdapter(adapter);
        setupStatusFilter();
    }

    private void setupStatusFilter() {
        spStatusFilter = findViewById(R.id.spStatusFilter);
        List<String> labels = new ArrayList<>();
        labels.add("Tất cả trạng thái");
        for (OrderStatus s : OrderStatus.values()) {
            labels.add(s.getLabel());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusFilter.setAdapter(spinnerAdapter);
        spStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            refresh();
        }
    }

    protected OrderStatus selectedStatus() {
        int pos = spStatusFilter.getSelectedItemPosition();
        return pos <= 0 ? null : OrderStatus.values()[pos - 1];
    }

    protected void refresh() {
        List<Order> orders = loadOrders(selectedStatus());
        adapter.submit(orders);
        tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
        double sum = 0;
        for (Order o : orders) {
            sum += o.getTotalAmount();
        }
        tvSummary.setText(orders.size() + " đơn · Tổng giá trị " + FormatUtils.money(sum));
    }

    /** Nội dung chi tiết đơn hàng dùng cho hộp thoại. */
    protected String buildOrderDetail(Order order) {
        StringBuilder sb = new StringBuilder();
        if (showCustomerName()) {
            sb.append("Khách hàng: ").append(order.getCustomerName()).append('\n');
        }
        sb.append("Ngày đặt: ").append(DateUtils.toDisplayDateTime(order.getOrderDate())).append('\n')
                .append("Trạng thái: ").append(order.getStatus().getLabel()).append("\n\n");
        for (OrderItem item : order.getItems()) {
            sb.append("• ").append(item.getFruitName())
                    .append(": ").append(item.getQuantity()).append(" kg × ")
                    .append(FormatUtils.money(item.getUnitPrice()))
                    .append(" = ").append(FormatUtils.money(item.getSubtotal())).append('\n');
        }
        sb.append("\nTổng cộng: ").append(FormatUtils.money(order.getTotalAmount()));
        return sb.toString();
    }
}
