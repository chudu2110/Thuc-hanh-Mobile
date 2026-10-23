package com.example.fruitshop.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO cơ sở (Template Method pattern).
 * - Giữ tham chiếu DatabaseHelper dùng chung.
 * - Cung cấp các hàm tiện ích đọc Cursor.
 * - Lớp con chỉ cần cài đặt {@link #mapRow(Cursor)} để chuyển 1 dòng thành entity.
 *
 * @param <T> kiểu entity mà DAO quản lý
 */
public abstract class BaseDao<T> {

    protected final DatabaseHelper dbHelper;

    protected BaseDao(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /** Chuyển dòng hiện tại của cursor thành entity. */
    protected abstract T mapRow(Cursor cursor);

    protected SQLiteDatabase readable() {
        return dbHelper.getReadableDatabase();
    }

    protected SQLiteDatabase writable() {
        return dbHelper.getWritableDatabase();
    }

    /** Đọc toàn bộ cursor thành danh sách và tự đóng cursor. */
    protected List<T> mapList(Cursor cursor) {
        List<T> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                result.add(mapRow(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    /** Đọc phần tử đầu tiên (hoặc null) và tự đóng cursor. */
    protected T mapFirst(Cursor cursor) {
        try {
            return cursor.moveToFirst() ? mapRow(cursor) : null;
        } finally {
            cursor.close();
        }
    }

    protected static String getString(Cursor c, String column) {
        return c.getString(c.getColumnIndexOrThrow(column));
    }

    protected static long getLong(Cursor c, String column) {
        return c.getLong(c.getColumnIndexOrThrow(column));
    }

    protected static int getInt(Cursor c, String column) {
        return c.getInt(c.getColumnIndexOrThrow(column));
    }

    protected static double getDouble(Cursor c, String column) {
        return c.getDouble(c.getColumnIndexOrThrow(column));
    }
}
