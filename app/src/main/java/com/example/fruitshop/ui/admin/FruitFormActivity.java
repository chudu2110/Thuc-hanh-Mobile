package com.example.fruitshop.ui.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.service.FruitService;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.ui.base.BaseActivity;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.FormatUtils;

import java.util.Locale;

/** Form thêm mới / cập nhật trái cây (Admin). Có fruitId → chế độ sửa. */
public class FruitFormActivity extends BaseActivity {

    private static final String EXTRA_FRUIT_ID = "fruit_id";

    private EditText etName;
    private EditText etCategory;
    private EditText etPrice;
    private EditText etQuantity;
    private TextView tvExpiryDate;
    private EditText etDescription;

    private FruitService fruitService;
    /** Ngày hết hạn đang chọn, định dạng "yyyy-MM-dd". */
    private String selectedExpiryDate;
    private Fruit editingFruit;

    public static Intent editIntent(Context context, long fruitId) {
        return new Intent(context, FruitFormActivity.class).putExtra(EXTRA_FRUIT_ID, fruitId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireRole(Role.ADMIN)) {
            return;
        }
        setContentView(R.layout.activity_fruit_form);
        fruitService = new FruitService(this);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        tvExpiryDate = findViewById(R.id.tvExpiryDate);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        long fruitId = getIntent().getLongExtra(EXTRA_FRUIT_ID, -1);
        if (fruitId > 0) {
            editingFruit = fruitService.getById(fruitId);
        }

        if (editingFruit != null) {
            setupBackButton("Cập nhật trái cây");
            fillForm(editingFruit);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> deleteFruit());
        } else {
            setupBackButton("Thêm trái cây");
            setExpiryDate(DateUtils.daysFromToday(7));
        }

        tvExpiryDate.setOnClickListener(v -> pickDate());
        btnSave.setOnClickListener(v -> save());
    }

    private void fillForm(Fruit f) {
        etName.setText(f.getName());
        etCategory.setText(f.getCategory());
        etPrice.setText(String.format(Locale.US, "%.0f", f.getPrice()));
        etQuantity.setText(String.valueOf(f.getQuantity()));
        etDescription.setText(f.getDescription());
        setExpiryDate(f.getExpiryDate());
    }

    private void setExpiryDate(String dbDate) {
        selectedExpiryDate = dbDate;
        tvExpiryDate.setText("Ngày hết hạn: " + DateUtils.toDisplayDate(dbDate) + "  📅");
    }

    private void pickDate() {
        int[] ymd = DateUtils.toYmd(selectedExpiryDate);
        new DatePickerDialog(this,
                (view, year, month, day) -> setExpiryDate(DateUtils.toDbDate(year, month, day)),
                ymd[0], ymd[1], ymd[2]).show();
    }

    private Fruit readForm() {
        Fruit f = editingFruit != null ? editingFruit : new Fruit();
        f.setName(etName.getText().toString());
        f.setCategory(etCategory.getText().toString());
        f.setPrice(FormatUtils.parseDouble(etPrice.getText().toString(), -1));
        f.setQuantity(FormatUtils.parseInt(etQuantity.getText().toString(), -1));
        f.setExpiryDate(selectedExpiryDate);
        f.setDescription(etDescription.getText().toString());
        return f;
    }

    private void save() {
        Fruit fruit = readForm();
        ServiceResult<Fruit> result = editingFruit != null
                ? fruitService.update(fruit)
                : fruitService.add(fruit);
        toast(result.getMessage());
        if (result.isSuccess()) {
            finish();
        }
    }

    private void deleteFruit() {
        confirm("Xoá trái cây", "Bạn chắc chắn muốn xoá \"" + editingFruit.getName() + "\"?", () -> {
            ServiceResult<Void> result = fruitService.delete(editingFruit.getId());
            toast(result.getMessage());
            if (result.isSuccess()) {
                finish();
            }
        });
    }
}
