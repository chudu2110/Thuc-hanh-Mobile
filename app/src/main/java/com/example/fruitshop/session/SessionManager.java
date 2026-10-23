package com.example.fruitshop.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fruitshop.data.model.Account;
import com.example.fruitshop.data.model.Customer;
import com.example.fruitshop.data.model.Role;

/**
 * Lưu thông tin tài khoản đăng nhập bằng SharedPreferences.
 * - Phiên đăng nhập (accountId, username, role, customerId, tên hiển thị) → tự đăng nhập lại khi mở app.
 * - "Ghi nhớ tài khoản": lưu username để điền sẵn ở màn hình Login (không lưu mật khẩu).
 */
public class SessionManager {

    private static final String PREF_NAME = "fruit_shop_session";

    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_REMEMBER = "remember_me";
    private static final String KEY_REMEMBERED_USERNAME = "remembered_username";

    private static final long NO_ID = -1L;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Lưu phiên sau khi đăng nhập thành công. customer có thể null với Admin. */
    public void saveLogin(Account account, Customer customer, boolean rememberMe) {
        SharedPreferences.Editor editor = prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putLong(KEY_ACCOUNT_ID, account.getId())
                .putString(KEY_USERNAME, account.getUsername())
                .putString(KEY_ROLE, account.getRole().name())
                .putLong(KEY_CUSTOMER_ID, customer != null ? customer.getId() : NO_ID)
                .putString(KEY_DISPLAY_NAME, customer != null ? customer.getFullname() : account.getUsername())
                .putBoolean(KEY_REMEMBER, rememberMe);
        if (rememberMe) {
            editor.putString(KEY_REMEMBERED_USERNAME, account.getUsername());
        } else {
            editor.remove(KEY_REMEMBERED_USERNAME);
        }
        editor.apply();
    }

    /** Đăng xuất: xoá phiên nhưng giữ lại username đã ghi nhớ. */
    public void logout() {
        prefs.edit()
                .remove(KEY_LOGGED_IN)
                .remove(KEY_ACCOUNT_ID)
                .remove(KEY_USERNAME)
                .remove(KEY_ROLE)
                .remove(KEY_CUSTOMER_ID)
                .remove(KEY_DISPLAY_NAME)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public long getAccountId() {
        return prefs.getLong(KEY_ACCOUNT_ID, NO_ID);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public Role getRole() {
        return Role.fromString(prefs.getString(KEY_ROLE, null));
    }

    public long getCustomerId() {
        return prefs.getLong(KEY_CUSTOMER_ID, NO_ID);
    }

    public String getDisplayName() {
        return prefs.getString(KEY_DISPLAY_NAME, "");
    }

    public boolean isRememberMe() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public String getRememberedUsername() {
        return prefs.getString(KEY_REMEMBERED_USERNAME, "");
    }
}
