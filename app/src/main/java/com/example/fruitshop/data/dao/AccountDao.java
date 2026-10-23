package com.example.fruitshop.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseContract.AccountTable;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Account;
import com.example.fruitshop.data.model.Role;

/** Truy xuất dữ liệu bảng Account. */
public class AccountDao extends BaseDao<Account> {

    public AccountDao(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Account mapRow(Cursor c) {
        return new Account(
                getLong(c, AccountTable.ID),
                getString(c, AccountTable.USERNAME),
                getString(c, AccountTable.PASSWORD),
                Role.fromString(getString(c, AccountTable.ROLE)));
    }

    public Account findById(long id) {
        Cursor c = readable().query(AccountTable.TABLE, null,
                AccountTable.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        return mapFirst(c);
    }

    public Account findByUsername(String username) {
        Cursor c = readable().query(AccountTable.TABLE, null,
                AccountTable.USERNAME + " = ? COLLATE NOCASE", new String[]{username},
                null, null, null);
        return mapFirst(c);
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    /** Thêm tài khoản; nhận SQLiteDatabase để có thể chạy trong một transaction lớn hơn. */
    public long insert(SQLiteDatabase db, Account account) {
        ContentValues values = new ContentValues();
        values.put(AccountTable.USERNAME, account.getUsername());
        values.put(AccountTable.PASSWORD, account.getPassword());
        values.put(AccountTable.ROLE, account.getRole().name());
        long id = db.insert(AccountTable.TABLE, null, values);
        account.setId(id);
        return id;
    }

    public long insert(Account account) {
        return insert(writable(), account);
    }
}
