package com.example.fruitshop.ui.customer;

import android.os.Bundle;
import android.widget.TextView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.ui.base.DashboardActivity;

/** Trang chủ của Customer. */
public class CustomerHomeActivity extends DashboardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireRole(Role.CUSTOMER)) {
            return;
        }
        setContentView(R.layout.activity_customer_home);
        setTitle("Fruit Shop");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Xin chào, " + session.getDisplayName());

        bindLogout(R.id.btnLogout);
    }
}
