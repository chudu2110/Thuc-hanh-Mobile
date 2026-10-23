package com.example.fruitshop.data.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.fruitshop.data.db.DatabaseContract.FruitTable;
import com.example.fruitshop.util.DateUtils;

/**
 * Dữ liệu trái cây mẫu để demo. Ngày hết hạn tính tương đối theo ngày cài app,
 * nên luôn có đủ 3 trạng thái: còn hạn, sắp hết hạn, đã hết hạn.
 */
final class FruitSeeder {

    private FruitSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        insert(db, "Táo Envy", "Táo", 85000, 50, 20, "Táo Envy New Zealand giòn ngọt");
        insert(db, "Táo xanh", "Táo", 60000, 40, 2, "Táo xanh chua nhẹ, sắp hết hạn");
        insert(db, "Cam sành", "Cam/Quýt", 35000, 100, 10, "Cam sành Vĩnh Long nhiều nước");
        insert(db, "Quýt đường", "Cam/Quýt", 45000, 0, 7, "Tạm hết hàng");
        insert(db, "Xoài cát Hoà Lộc", "Xoài", 70000, 30, 5, "Xoài chín cây thơm ngọt");
        insert(db, "Nho mẫu đơn", "Nho", 250000, 15, 12, "Nho Shine Muscat Hàn Quốc");
        insert(db, "Chuối già", "Chuối", 25000, 60, -1, "Lô hàng đã hết hạn (để demo)");
        insert(db, "Dâu tây Đà Lạt", "Dâu", 120000, 20, -3, "Lô hàng đã hết hạn (để demo)");
        insert(db, "Thanh long ruột đỏ", "Thanh long", 40000, 80, 9, "Thanh long Bình Thuận");
    }

    private static void insert(SQLiteDatabase db, String name, String category, double price,
                               int quantity, int expiryInDays, String description) {
        ContentValues v = new ContentValues();
        v.put(FruitTable.NAME, name);
        v.put(FruitTable.CATEGORY, category);
        v.put(FruitTable.PRICE, price);
        v.put(FruitTable.QUANTITY, quantity);
        v.put(FruitTable.EXPIRY_DATE, DateUtils.daysFromToday(expiryInDays));
        v.put(FruitTable.DESCRIPTION, description);
        db.insert(FruitTable.TABLE, null, v);
    }
}
