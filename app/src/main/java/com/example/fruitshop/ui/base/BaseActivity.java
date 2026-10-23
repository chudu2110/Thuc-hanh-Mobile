package com.example.fruitshop.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.session.SessionManager;
import com.example.fruitshop.ui.auth.LoginActivity;

/**
 * Activity cơ sở cho toàn app: gom các hàm dùng chung (toast, hộp thoại xác nhận,
 * nút back trên ActionBar, kiểm tra quyền truy cập theo Role).
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
    }

    /**
     * Đảm bảo người dùng đã đăng nhập đúng vai trò; nếu không sẽ quay về màn hình Login.
     *
     * @return true nếu hợp lệ, false nếu đã chuyển hướng (caller nên return ngay)
     */
    protected boolean requireRole(Role role) {
        if (!session.isLoggedIn() || session.getRole() != role) {
            goToLogin();
            return false;
        }
        return true;
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /** Hiện nút back trên ActionBar và đặt tiêu đề màn hình. */
    protected void setupBackButton(String title) {
        setTitle(title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void confirm(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (d, w) -> onConfirm.run())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    protected void navigateTo(Class<?> target) {
        startActivity(new Intent(this, target));
    }
}
