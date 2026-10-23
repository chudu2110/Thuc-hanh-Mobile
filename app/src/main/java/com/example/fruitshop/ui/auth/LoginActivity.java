package com.example.fruitshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Account;
import com.example.fruitshop.service.AuthService;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.ui.MainActivity;
import com.example.fruitshop.ui.base.BaseActivity;

/** Màn hình đăng nhập cho cả Admin và Customer. */
public class LoginActivity extends BaseActivity {

    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbRemember;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Đăng nhập");
        authService = new AuthService(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Điền sẵn username đã ghi nhớ từ SharedPreferences
        String remembered = session.getRememberedUsername();
        if (!remembered.isEmpty()) {
            etUsername.setText(remembered);
            cbRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> doLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        ServiceResult<Account> result = authService.login(
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                cbRemember.isChecked());
        toast(result.getMessage());
        if (result.isSuccess()) {
            startActivity(MainActivity.homeIntent(this, result.getData().getRole()));
            finish();
        }
    }
}
