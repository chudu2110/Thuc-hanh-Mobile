package com.example.fruitshop.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseContract.FruitTable;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Fruit;

import java.util.ArrayList;
import java.util.List;

/** Truy xuất dữ liệu bảng Fruit: CRUD + tìm kiếm + lọc theo hạn sử dụng. */
public class FruitDao extends BaseDao<Fruit> {

    /** Bộ lọc khi tìm kiếm. */
    public enum Filter {
        /** Tất cả (Admin). */
        ALL,
        /** Chỉ hàng đã hết hạn (Admin). */
        EXPIRED_ONLY,
        /** Còn hạn và còn hàng (Customer). */
        AVAILABLE_ONLY
    }

    public FruitDao(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected Fruit mapRow(Cursor c) {
        return new Fruit(
                getLong(c, FruitTable.ID),
                getString(c, FruitTable.NAME),
                getString(c, FruitTable.CATEGORY),
                getDouble(c, FruitTable.PRICE),
                getInt(c, FruitTable.QUANTITY),
                getString(c, FruitTable.EXPIRY_DATE),
                getString(c, FruitTable.DESCRIPTION));
    }

    public Fruit findById(long id) {
        return findById(readable(), id);
    }

    /** Dùng trong transaction (ví dụ khi đặt hàng). */
    public Fruit findById(SQLiteDatabase db, long id) {
        Cursor c = db.query(FruitTable.TABLE, null, FruitTable.ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        return mapFirst(c);
    }

    /**
     * Tìm theo tên hoặc loại (không phân biệt hoa thường) kèm bộ lọc hạn sử dụng.
     *
     * @param keyword từ khoá, rỗng = không lọc theo từ khoá
     * @param filter  bộ lọc
     * @param today   ngày hôm nay "yyyy-MM-dd" (truyền vào để dễ kiểm thử)
     */
    public List<Fruit> search(String keyword, Filter filter, String today) {
        StringBuilder where = new StringBuilder("1 = 1");
        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim() + "%";
            where.append(" AND (").append(FruitTable.NAME).append(" LIKE ? OR ")
                    .append(FruitTable.CATEGORY).append(" LIKE ?)");
            args.add(like);
            args.add(like);
        }
        if (filter == Filter.EXPIRED_ONLY) {
            where.append(" AND ").append(FruitTable.EXPIRY_DATE).append(" < ?");
            args.add(today);
        } else if (filter == Filter.AVAILABLE_ONLY) {
            where.append(" AND ").append(FruitTable.EXPIRY_DATE).append(" >= ?")
                    .append(" AND ").append(FruitTable.QUANTITY).append(" > 0");
            args.add(today);
        }

        Cursor c = readable().query(FruitTable.TABLE, null, where.toString(),
                args.toArray(new String[0]), null, null,
                FruitTable.EXPIRY_DATE + " ASC, " + FruitTable.NAME + " ASC");
        return mapList(c);
    }

    public int countExpired(String today) {
        Cursor c = readable().rawQuery("SELECT COUNT(*) FROM " + FruitTable.TABLE
                + " WHERE " + FruitTable.EXPIRY_DATE + " < ?", new String[]{today});
        try {
            return c.moveToFirst() ? c.getInt(0) : 0;
        } finally {
            c.close();
        }
    }

    public long insert(Fruit fruit) {
        long id = writable().insert(FruitTable.TABLE, null, toValues(fruit));
        fruit.setId(id);
        return id;
    }

    public boolean update(Fruit fruit) {
        return writable().update(FruitTable.TABLE, toValues(fruit), FruitTable.ID + " = ?",
                new String[]{String.valueOf(fruit.getId())}) > 0;
    }

    public boolean delete(long id) {
        return writable().delete(FruitTable.TABLE, FruitTable.ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    /** Cập nhật tồn kho (dùng trong transaction đặt hàng / huỷ đơn). */
    public boolean updateQuantity(SQLiteDatabase db, long fruitId, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(FruitTable.QUANTITY, newQuantity);
        return db.update(FruitTable.TABLE, values, FruitTable.ID + " = ?",
                new String[]{String.valueOf(fruitId)}) > 0;
    }

    static ContentValues toValues(Fruit fruit) {
        ContentValues values = new ContentValues();
        values.put(FruitTable.NAME, fruit.getName());
        values.put(FruitTable.CATEGORY, fruit.getCategory());
        values.put(FruitTable.PRICE, fruit.getPrice());
        values.put(FruitTable.QUANTITY, fruit.getQuantity());
        values.put(FruitTable.EXPIRY_DATE, fruit.getExpiryDate());
        values.put(FruitTable.DESCRIPTION, fruit.getDescription());
        return values;
    }
}
