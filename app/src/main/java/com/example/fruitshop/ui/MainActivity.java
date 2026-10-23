package com.example.fruitshop.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.session.SessionManager;
import com.example.fruitshop.ui.admin.AdminDashboardActivity;
import com.example.fruitshop.ui.auth.LoginActivity;
import com.example.fruitshop.ui.customer.CustomerHomeActivity;

/**
 * Màn hình khởi động (không có giao diện): đọc phiên từ SharedPreferences
 * và điều hướng tới Login / trang Admin / trang Customer.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);

        Class<?> target;
        if (!session.isLoggedIn()) {
            target = LoginActivity.class;
        } else if (session.getRole() == Role.ADMIN) {
            target = AdminDashboardActivity.class;
        } else {
            target = CustomerHomeActivity.class;
        }
        startActivity(new Intent(this, target));
        finish();
    }

    /** Dùng sau khi đăng nhập để mở đúng trang chủ theo vai trò. */
    public static Intent homeIntent(android.content.Context context, Role role) {
        Class<?> target = role == Role.ADMIN ? AdminDashboardActivity.class : CustomerHomeActivity.class;
        Intent intent = new Intent(context, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
