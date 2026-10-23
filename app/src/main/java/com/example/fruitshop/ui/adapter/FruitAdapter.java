package com.example.fruitshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.ExpiryStatus;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/** Adapter hiển thị danh sách trái cây, tô màu theo trạng thái hạn sử dụng. */
public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.FruitViewHolder> {

    /** Callback khi người dùng chạm / giữ lâu một trái cây. */
    public interface OnFruitClickListener {
        void onFruitClick(Fruit fruit);

        default void onFruitLongClick(Fruit fruit) {
        }
    }

    private final List<Fruit> items = new ArrayList<>();
    private final OnFruitClickListener listener;

    public FruitAdapter(OnFruitClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Fruit> fruits) {
        items.clear();
        items.addAll(fruits);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FruitViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvCategory;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvExpiry;
        private final TextView tvStatus;

        FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(Fruit fruit, OnFruitClickListener listener) {
            tvName.setText(fruit.getName());
            tvCategory.setText(fruit.getCategory());
            tvPrice.setText(FormatUtils.money(fruit.getPrice()) + "/kg");
            tvQuantity.setText(fruit.isInStock() ? "Còn " + fruit.getQuantity() + " kg" : "Hết hàng");
            tvExpiry.setText("HSD: " + DateUtils.toDisplayDate(fruit.getExpiryDate())
                    + " (" + describeDaysLeft(fruit.getDaysUntilExpiry()) + ")");

            ExpiryStatus status = fruit.getExpiryStatus();
            tvStatus.setText(status.getLabel());
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), colorOf(status)));

            itemView.setOnClickListener(v -> listener.onFruitClick(fruit));
            itemView.setOnLongClickListener(v -> {
                listener.onFruitLongClick(fruit);
                return true;
            });
        }

        private static String describeDaysLeft(long days) {
            if (days < 0) {
                return "quá hạn " + (-days) + " ngày";
            }
            return days == 0 ? "hết hạn hôm nay" : "còn " + days + " ngày";
        }

        private static int colorOf(ExpiryStatus status) {
            switch (status) {
                case EXPIRED:
                    return R.color.status_expired;
                case NEAR_EXPIRY:
                    return R.color.status_warning;
                default:
                    return R.color.status_ok;
            }
        }
    }
}
