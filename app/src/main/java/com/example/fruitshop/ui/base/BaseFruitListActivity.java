package com.example.fruitshop.ui.base;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.service.FruitService;
import com.example.fruitshop.ui.adapter.FruitAdapter;

import java.util.List;

/**
 * Khung chung cho màn hình danh sách trái cây (Template Method):
 * ô tìm kiếm realtime + RecyclerView + trạng thái rỗng.
 * Lớp con (Admin / Customer) quyết định dữ liệu nào được tải và xử lý khi chạm vào item.
 */
public abstract class BaseFruitListActivity extends BaseActivity
        implements FruitAdapter.OnFruitClickListener {

    protected FruitService fruitService;
    protected FruitAdapter adapter;
    protected EditText etSearch;
    protected TextView tvSummary;
    private TextView tvEmpty;

    /** Vai trò được phép mở màn hình. */
    protected abstract Role requiredRole();

    protected abstract String screenTitle();

    /** Tải danh sách theo từ khoá hiện tại. */
    protected abstract List<Fruit> loadFruits(String keyword);

    /** Hook để lớp con cấu hình thêm view (nút thêm, checkbox lọc...). */
    protected void onViewsReady() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireRole(requiredRole())) {
            return;
        }
        setContentView(R.layout.activity_fruit_list);
        setupBackButton(screenTitle());
        fruitService = new FruitService(this);

        etSearch = findViewById(R.id.etSearch);
        tvSummary = findViewById(R.id.tvSummary);
        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FruitAdapter(this);
        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        });
        onViewsReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            refresh();
        }
    }

    protected void refresh() {
        List<Fruit> fruits = loadFruits(etSearch.getText().toString());
        adapter.submit(fruits);
        tvEmpty.setVisibility(fruits.isEmpty() ? View.VISIBLE : View.GONE);
        tvSummary.setText(buildSummary(fruits));
    }

    protected String buildSummary(List<Fruit> fruits) {
        return "Hiển thị " + fruits.size() + " sản phẩm";
    }
}
