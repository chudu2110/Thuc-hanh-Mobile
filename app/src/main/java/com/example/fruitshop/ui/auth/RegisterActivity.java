package com.example.fruitshop.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.fruitshop.R;
import com.example.fruitshop.data.model.Customer;
import com.example.fruitshop.service.AuthService;
import com.example.fruitshop.service.ServiceResult;
import com.example.fruitshop.ui.base.BaseActivity;

/** Đăng ký tài khoản khách hàng mới (Account + Customer). */
public class RegisterActivity extends BaseActivity {

    private EditText etFullname;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupBackButton("Đăng ký");
        authService = new AuthService(this);

        etFullname = findViewById(R.id.etFullname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        ServiceResult<Customer> result = authService.registerCustomer(
                text(etUsername), etPassword.getText().toString(), etConfirmPassword.getText().toString(),
                text(etFullname), text(etPhone), text(etEmail));
        toast(result.getMessage());
        if (result.isSuccess()) {
            finish();
        }
    }

    private static String text(EditText et) {
        return et.getText().toString().trim();
    }
}
