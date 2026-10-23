package com.example.fruitshop.ui.customer;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Order;
import com.example.fruitshop.service.OrderService;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

/**
 * Hộp thoại đặt hàng một loại trái cây: nhập số lượng, xem tạm tính,
 * xác nhận → gọi {@link OrderService#placeOrder(long, long, int)}.
 * Hàng hết hạn bị chặn ngay tại đây và chặn lần nữa ở tầng Service.
 */
public class PlaceOrderDialog {

    private final Context context;
    private final Fruit fruit;
    private final long customerId;
    private final Runnable onOrdered;
    private final OrderService orderService;

    public PlaceOrderDialog(Context context, Fruit fruit, long customerId, Runnable onOrdered) {
        this.context = context;
        this.fruit = fruit;
        this.customerId = customerId;
        this.onOrdered = onOrdered;
        this.orderService = new OrderService(context);
    }

    public void show() {
        if (fruit.isExpired()) {
            toast("\"" + fruit.getName() + "\" đã hết hạn, không thể đặt hàng");
            return;
        }
        if (!fruit.isInStock()) {
            toast("\"" + fruit.getName() + "\" đã hết hàng");
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_place_order, null);
        TextView tvInfo = view.findViewById(R.id.tvFruitInfo);
        EditText etQuantity = view.findViewById(R.id.etQuantity);
        TextView tvTotal = view.findViewById(R.id.tvTotal);

        tvInfo.setText(FormatUtils.money(fruit.getPrice()) + "/kg · Còn " + fruit.getQuantity()
                + " kg\nHSD: " + DateUtils.toDisplayDate(fruit.getExpiryDate()));
        etQuantity.setText("1");
        updateTotal(tvTotal, 1);
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotal(tvTotal, FormatUtils.parseInt(s.toString(), 0));
            }
        });

        new AlertDialog.Builder(context)
                .setTitle("Đặt " + fruit.getName())
                .setView(view)
                .setPositiveButton("Xác nhận đặt", (d, w) -> submit(FormatUtils.parseInt(
                        etQuantity.getText().toString(), 0)))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void updateTotal(TextView tvTotal, int quantity) {
        tvTotal.setText("Tạm tính: " + FormatUtils.money(Math.max(quantity, 0) * fruit.getPrice()));
    }

    private void submit(int quantity) {
        ServiceResult<Order> result = orderService.placeOrder(customerId, fruit.getId(), quantity);
        toast(result.getMessage());
        if (result.isSuccess() && onOrdered != null) {
            onOrdered.run();
        }
    }

    private void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
