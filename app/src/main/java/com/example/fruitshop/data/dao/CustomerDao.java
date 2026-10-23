package com.example.fruitshop.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseContract.CustomerTable;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Customer;

/** Truy xuất dữ liệu bảng Customer. */
public class CustomerDao extends BaseDao<Customer> {

    public CustomerDao(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Customer mapRow(Cursor c) {
        return new Customer(
                getLong(c, CustomerTable.ID),
                getString(c, CustomerTable.FULLNAME),
                getString(c, CustomerTable.PHONE),
                getString(c, CustomerTable.EMAIL),
                getLong(c, CustomerTable.ACCOUNT_ID));
    }

    public Customer findById(long id) {
        Cursor c = readable().query(CustomerTable.TABLE, null,
                CustomerTable.ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        return mapFirst(c);
    }

    public Customer findByAccountId(long accountId) {
        Cursor c = readable().query(CustomerTable.TABLE, null,
                CustomerTable.ACCOUNT_ID + " = ?", new String[]{String.valueOf(accountId)},
                null, null, null);
        return mapFirst(c);
    }

    public long insert(SQLiteDatabase db, Customer customer) {
        ContentValues values = new ContentValues();
        values.put(CustomerTable.FULLNAME, customer.getFullname());
        values.put(CustomerTable.PHONE, customer.getPhone());
        values.put(CustomerTable.EMAIL, customer.getEmail());
        values.put(CustomerTable.ACCOUNT_ID, customer.getAccountId());
        long id = db.insert(CustomerTable.TABLE, null, values);
        customer.setId(id);
        return id;
    }
}
