package com.example.fruitshop.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fruitshop.data.db.DatabaseContract.AccountTable;
import com.example.fruitshop.data.db.DatabaseContract.CustomerTable;
import com.example.fruitshop.data.db.DatabaseContract.FruitTable;
import com.example.fruitshop.data.db.DatabaseContract.OrderItemTable;
import com.example.fruitshop.data.db.DatabaseContract.OrderTable;
import com.example.fruitshop.data.model.Role;
import com.example.fruitshop.util.PasswordHasher;

/**
 * Quản lý vòng đời CSDL SQLite (tạo bảng, nâng cấp, dữ liệu mẫu).
 * Dùng Singleton để cả app chỉ có một kết nối CSDL.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static volatile DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccountTable.CREATE);
        db.execSQL(CustomerTable.CREATE);
        db.execSQL(FruitTable.CREATE);
        db.execSQL(OrderTable.CREATE);
        db.execSQL(OrderItemTable.CREATE);
        seedAccounts(db);
        FruitSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OrderItemTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OrderTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FruitTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CustomerTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AccountTable.TABLE);
        onCreate(db);
    }

    /** Tạo sẵn 1 tài khoản Admin và 1 tài khoản Customer để demo. */
    private void seedAccounts(SQLiteDatabase db) {
        insertAccount(db, "admin", "admin123", Role.ADMIN);

        long customerAccountId = insertAccount(db, "customer", "123456", Role.CUSTOMER);
        ContentValues customer = new ContentValues();
        customer.put(CustomerTable.FULLNAME, "Nguyễn Văn A");
        customer.put(CustomerTable.PHONE, "0901234567");
        customer.put(CustomerTable.EMAIL, "customer@fruitshop.com");
        customer.put(CustomerTable.ACCOUNT_ID, customerAccountId);
        db.insert(CustomerTable.TABLE, null, customer);
    }

    private long insertAccount(SQLiteDatabase db, String username, String rawPassword, Role role) {
        ContentValues values = new ContentValues();
        values.put(AccountTable.USERNAME, username);
        values.put(AccountTable.PASSWORD, PasswordHasher.hash(rawPassword));
        values.put(AccountTable.ROLE, role.name());
        return db.insert(AccountTable.TABLE, null, values);
    }
}
