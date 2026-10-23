package com.example.fruitshop.ui.customer;

import androidx.appcompat.app.AlertDialog;

import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.ui.base.BaseFruitListActivity;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

import java.util.List;

/**
 * Customer: xem & tìm trái cây đang bán (đã loại hàng hết hạn / hết hàng),
 * xem hạn sử dụng và thông tin chi tiết.
 */
public class CustomerFruitListActivity extends BaseFruitListActivity {

    @Override
    protected Role requiredRole() {
        return Role.CUSTOMER;
    }

    @Override
    protected String screenTitle() {
        return "Trái cây đang bán";
    }

    @Override
    protected List<Fruit> loadFruits(String keyword) {
        return fruitService.searchAvailable(keyword);
    }

    @Override
    protected String buildSummary(List<Fruit> fruits) {
        return fruits.size() + " sản phẩm còn hạn · Chạm để xem chi tiết";
    }

    @Override
    public void onFruitClick(Fruit fruit) {
        String detail = "Loại: " + fruit.getCategory()
                + "\nGiá: " + FormatUtils.money(fruit.getPrice()) + "/kg"
                + "\nTồn kho: " + fruit.getQuantity() + " kg"
                + "\nHạn sử dụng: " + DateUtils.toDisplayDate(fruit.getExpiryDate())
                + " (" + fruit.getExpiryStatus().getLabel() + ")"
                + "\n\n" + fruit.getDescription();

        new AlertDialog.Builder(this)
                .setTitle(fruit.getName())
                .setMessage(detail)
                .setPositiveButton("Đóng", null)
                .show();
    }
}
