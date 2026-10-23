package com.example.fruitshop.ui.base;

import android.view.View;

/**
 * Lớp cơ sở cho các trang chủ (Admin/Customer): gắn nút menu với màn hình đích
 * và xử lý đăng xuất. Lớp con chỉ khai báo layout và các mục menu.
 */
public abstract class DashboardActivity extends BaseActivity {

    /** Gắn một nút menu với Activity đích; target == null nghĩa là chức năng chưa phát triển. */
    protected void bindMenu(int viewId, Class<?> target) {
        View view = findViewById(viewId);
        view.setOnClickListener(v -> {
            if (target == null) {
                toast("Chức năng đang được phát triển");
            } else {
                navigateTo(target);
            }
        });
    }

    protected void bindLogout(int viewId) {
        findViewById(viewId).setOnClickListener(v ->
                confirm("Đăng xuất", "Bạn có chắc muốn đăng xuất?", () -> {
                    session.logout();
                    goToLogin();
                }));
    }
}
