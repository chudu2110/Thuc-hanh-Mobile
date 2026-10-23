package com.example.fruitshop.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.dao.AccountDao;
import com.example.fruitshop.data.dao.CustomerDao;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Account;
import com.example.fruitshop.data.model.Customer;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.session.SessionManager;
import com.example.fruitshop.util.PasswordHasher;
import com.example.fruitshop.util.Validator;

/**
 * Nghiệp vụ xác thực: đăng nhập, đăng ký khách hàng, đăng xuất.
 * UI chỉ gọi Service, không truy cập DAO trực tiếp.
 */
public class AuthService {

    private final DatabaseHelper dbHelper;
    private final AccountDao accountDao;
    private final CustomerDao customerDao;
    private final SessionManager sessionManager;

    public AuthService(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.accountDao = new AccountDao(dbHelper);
        this.customerDao = new CustomerDao(dbHelper);
        this.sessionManager = new SessionManager(context);
    }

    public ServiceResult<Account> login(String username, String password, boolean rememberMe) {
        if (Validator.isBlank(username) || Validator.isBlank(password)) {
            return ServiceResult.failure("Vui lòng nhập tên đăng nhập và mật khẩu");
        }
        Account account = accountDao.findByUsername(username.trim());
        if (account == null || !PasswordHasher.matches(password, account.getPassword())) {
            return ServiceResult.failure("Sai tên đăng nhập hoặc mật khẩu");
        }

        Customer customer = null;
        if (account.getRole() == Role.CUSTOMER) {
            customer = customerDao.findByAccountId(account.getId());
            if (customer == null) {
                return ServiceResult.failure("Tài khoản khách hàng chưa có hồ sơ");
            }
        }
        sessionManager.saveLogin(account, customer, rememberMe);
        return ServiceResult.success(account, "Đăng nhập thành công");
    }

    /** Đăng ký tài khoản Customer: tạo Account + Customer trong cùng một transaction. */
    public ServiceResult<Customer> registerCustomer(String username, String password, String confirmPassword,
                                                    String fullname, String phone, String email) {
        String error = validateRegistration(username, password, confirmPassword, fullname, phone, email);
        if (error != null) {
            return ServiceResult.failure(error);
        }
        if (accountDao.existsByUsername(username.trim())) {
            return ServiceResult.failure("Tên đăng nhập đã tồn tại");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Account account = new Account(0, username.trim(), PasswordHasher.hash(password), Role.CUSTOMER);
            if (accountDao.insert(db, account) == -1) {
                return ServiceResult.failure("Không thể tạo tài khoản");
            }
            Customer customer = new Customer(0, fullname.trim(), phone.trim(), email.trim(), account.getId());
            if (customerDao.insert(db, customer) == -1) {
                return ServiceResult.failure("Không thể tạo hồ sơ khách hàng");
            }
            db.setTransactionSuccessful();
            return ServiceResult.success(customer, "Đăng ký thành công, hãy đăng nhập");
        } finally {
            db.endTransaction();
        }
    }

    public void logout() {
        sessionManager.logout();
    }

    private String validateRegistration(String username, String password, String confirmPassword,
                                        String fullname, String phone, String email) {
        if (Validator.isBlank(fullname)) {
            return "Vui lòng nhập họ tên";
        }
        if (!Validator.isValidPhone(phone)) {
            return "Số điện thoại không hợp lệ (10-11 số, bắt đầu bằng 0)";
        }
        if (!Validator.isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        if (!Validator.isValidUsername(username)) {
            return "Tên đăng nhập 3-20 ký tự, chỉ gồm chữ, số và _";
        }
        if (!Validator.isValidPassword(password)) {
            return "Mật khẩu tối thiểu " + Validator.MIN_PASSWORD_LENGTH + " ký tự";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }
        return null;
    }
}
