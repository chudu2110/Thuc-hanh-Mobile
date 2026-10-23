package com.example.fruitshop.ui.admin;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.ui.base.BaseFruitListActivity;

import java.util.List;

/**
 * Admin: xem / tìm kiếm toàn bộ trái cây, lọc hàng hết hạn.
 * Chạm → sửa, giữ lâu → xoá, nút dưới cùng → thêm mới.
 */
public class AdminFruitListActivity extends BaseFruitListActivity {

    private CheckBox cbExpiredOnly;

    @Override
    protected Role requiredRole() {
        return Role.ADMIN;
    }

    @Override
    protected String screenTitle() {
        return "Quản lý trái cây";
    }

    @Override
    protected void onViewsReady() {
        cbExpiredOnly = findViewById(R.id.cbExpiredOnly);
        cbExpiredOnly.setVisibility(View.VISIBLE);
        cbExpiredOnly.setOnCheckedChangeListener((b, checked) -> refresh());

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, FruitFormActivity.class)));
    }

    @Override
    protected List<Fruit> loadFruits(String keyword) {
        boolean expiredOnly = cbExpiredOnly != null && cbExpiredOnly.isChecked();
        return fruitService.searchForAdmin(keyword, expiredOnly);
    }

    @Override
    protected String buildSummary(List<Fruit> fruits) {
        return "Hiển thị " + fruits.size() + " sản phẩm · ⚠ " + fruitService.countExpired()
                + " sản phẩm đã hết hạn · Giữ lâu để xoá";
    }

    @Override
    public void onFruitClick(Fruit fruit) {
        startActivity(FruitFormActivity.editIntent(this, fruit.getId()));
    }

    @Override
    public void onFruitLongClick(Fruit fruit) {
        confirm("Xoá trái cây", "Xoá \"" + fruit.getName() + "\"?", () -> {
            ServiceResult<Void> result = fruitService.delete(fruit.getId());
            toast(result.getMessage());
            refresh();
        });
    }
}
