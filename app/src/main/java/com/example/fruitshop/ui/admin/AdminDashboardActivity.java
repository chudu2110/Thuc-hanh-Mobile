package com.example.fruitshop.ui.admin;

import android.os.Bundle;
import android.widget.TextView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.ui.base.DashboardActivity;

/** Trang chủ của Admin. */
public class AdminDashboardActivity extends DashboardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireRole(Role.ADMIN)) {
            return;
        }
        setContentView(R.layout.activity_admin_dashboard);
        setTitle("Quản trị cửa hàng");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Xin chào Admin: " + session.getUsername());

        bindLogout(R.id.btnLogout);
    }
}
