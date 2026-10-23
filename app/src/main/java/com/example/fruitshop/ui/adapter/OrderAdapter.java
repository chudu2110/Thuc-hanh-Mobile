package com.example.fruitshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.data.model.OrderItem;
import com.example.fruitshop.data.model.OrderStatus;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/** Adapter hiển thị danh sách đơn hàng (dùng cho cả Admin và Customer). */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final List<Order> items = new ArrayList<>();
    private final OnOrderClickListener listener;
    private final boolean showCustomer;

    /**
     * @param showCustomer true ở màn Admin để hiện tên khách hàng
     */
    public OrderAdapter(boolean showCustomer, OnOrderClickListener listener) {
        this.showCustomer = showCustomer;
        this.listener = listener;
    }

    public void submit(List<Order> orders) {
        items.clear();
        items.addAll(orders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(items.get(position), showCustomer, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Tóm tắt sản phẩm của đơn: "Táo Envy x2, Cam sành x3". */
    public static String summarizeItems(Order order) {
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(item.getFruitName()).append(" x").append(item.getQuantity()).append("kg");
        }
        return sb.toString();
    }

    public static int colorOf(OrderStatus status) {
        switch (status) {
            case PENDING:
                return R.color.status_warning;
            case CANCELLED:
                return R.color.status_expired;
            case COMPLETED:
                return R.color.primary_dark;
            default:
                return R.color.status_ok;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderId;
        private final TextView tvStatus;
        private final TextView tvCustomer;
        private final TextView tvDate;
        private final TextView tvItems;
        private final TextView tvTotal;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvItems = itemView.findViewById(R.id.tvItems);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }

        void bind(Order order, boolean showCustomer, OnOrderClickListener listener) {
            tvOrderId.setText("Đơn #" + order.getId());
            tvStatus.setText(order.getStatus().getLabel());
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), colorOf(order.getStatus())));
            tvCustomer.setVisibility(showCustomer ? View.VISIBLE : View.GONE);
            tvCustomer.setText("Khách hàng: " + order.getCustomerName());
            tvDate.setText(DateUtils.toDisplayDateTime(order.getOrderDate()));
            tvItems.setText(summarizeItems(order));
            tvTotal.setText("Tổng: " + FormatUtils.money(order.getTotalAmount()));
            itemView.setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}
