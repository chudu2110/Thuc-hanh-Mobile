package com.example.fruitshop.data.db;

/**
 * Tập trung toàn bộ tên bảng / tên cột của CSDL SQLite.
 * Mỗi bảng là một inner class để tránh "magic string" rải rác trong code.
 */
public final class DatabaseContract {

    public static final String DATABASE_NAME = "fruit_shop.db";
    public static final int DATABASE_VERSION = 1;

    private DatabaseContract() {
    }

    /** Bảng Account(id, username, password, role) */
    public static final class AccountTable {
        public static final String TABLE = "accounts";
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";

        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT NOT NULL UNIQUE, "
                + PASSWORD + " TEXT NOT NULL, "
                + ROLE + " TEXT NOT NULL CHECK(" + ROLE + " IN ('ADMIN','CUSTOMER')))";

        private AccountTable() {
        }
    }

    /** Bảng Customer(id, fullname, phone, email, accountId) */
    public static final class CustomerTable {
        public static final String TABLE = "customers";
        public static final String ID = "id";
        public static final String FULLNAME = "fullname";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String ACCOUNT_ID = "accountId";

        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FULLNAME + " TEXT NOT NULL, "
                + PHONE + " TEXT, "
                + EMAIL + " TEXT, "
                + ACCOUNT_ID + " INTEGER NOT NULL UNIQUE, "
                + "FOREIGN KEY(" + ACCOUNT_ID + ") REFERENCES "
                + AccountTable.TABLE + "(" + AccountTable.ID + ") ON DELETE CASCADE)";

        private CustomerTable() {
        }
    }

    /** Bảng Fruit(id, name, category, price, quantity, expiryDate, description) */
    public static final class FruitTable {
        public static final String TABLE = "fruits";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        /** Lưu dạng "yyyy-MM-dd" để so sánh chuỗi = so sánh ngày. */
        public static final String EXPIRY_DATE = "expiryDate";
        public static final String DESCRIPTION = "description";

        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + CATEGORY + " TEXT NOT NULL, "
                + PRICE + " REAL NOT NULL CHECK(" + PRICE + " >= 0), "
                + QUANTITY + " INTEGER NOT NULL CHECK(" + QUANTITY + " >= 0), "
                + EXPIRY_DATE + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT)";

        private FruitTable() {
        }
    }

    /** Bảng Order(id, customerId, orderDate, totalAmount, status). "order" là từ khoá SQL nên đặt tên bảng "orders". */
    public static final class OrderTable {
        public static final String TABLE = "orders";
        public static final String ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String ORDER_DATE = "orderDate";
        public static final String TOTAL_AMOUNT = "totalAmount";
        public static final String STATUS = "status";

        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CUSTOMER_ID + " INTEGER NOT NULL, "
                + ORDER_DATE + " TEXT NOT NULL, "
                + TOTAL_AMOUNT + " REAL NOT NULL, "
                + STATUS + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + CUSTOMER_ID + ") REFERENCES "
                + CustomerTable.TABLE + "(" + CustomerTable.ID + ") ON DELETE CASCADE)";

        private OrderTable() {
        }
    }

    /**
     * Bảng phụ OrderItem(id, orderId, fruitId, fruitName, quantity, unitPrice).
     * Bảng Order theo đề không chứa thông tin sản phẩm, nên cần bảng chi tiết
     * để biết đơn hàng gồm những trái cây nào (quan hệ nhiều - nhiều Order - Fruit).
     */
    public static final class OrderItemTable {
        public static final String TABLE = "order_items";
        public static final String ID = "id";
        public static final String ORDER_ID = "orderId";
        public static final String FRUIT_ID = "fruitId";
        /** Lưu tên tại thời điểm mua để lịch sử không bị mất khi trái cây bị xoá/sửa. */
        public static final String FRUIT_NAME = "fruitName";
        public static final String QUANTITY = "quantity";
        public static final String UNIT_PRICE = "unitPrice";

        public static final String CREATE = "CREATE TABLE " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORDER_ID + " INTEGER NOT NULL, "
                + FRUIT_ID + " INTEGER, "
                + FRUIT_NAME + " TEXT NOT NULL, "
                + QUANTITY + " INTEGER NOT NULL CHECK(" + QUANTITY + " > 0), "
                + UNIT_PRICE + " REAL NOT NULL, "
                + "FOREIGN KEY(" + ORDER_ID + ") REFERENCES "
                + OrderTable.TABLE + "(" + OrderTable.ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + FRUIT_ID + ") REFERENCES "
                + FruitTable.TABLE + "(" + FruitTable.ID + ") ON DELETE SET NULL)";

        private OrderItemTable() {
        }
    }
}
