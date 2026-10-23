# 🍎 FRUIT SHOP MANAGEMENT (Android · Java · SQLite)

Ứng dụng quản lý cửa hàng trái cây với 2 vai trò **Admin** và **Customer**.

## Chạy demo

1. Mở thư mục project bằng **Android Studio** (Ladybug trở lên) → đợi Gradle Sync.
2. Chạy cấu hình `app` trên máy ảo / điện thoại (Android 7.0+, minSdk 24).
3. Tài khoản có sẵn:

| Vai trò  | Username   | Password   |
|----------|------------|------------|
| Admin    | `admin`    | `admin123` |
| Customer | `customer` | `123456`   |

Dữ liệu mẫu có đủ trái cây **còn hạn / sắp hết hạn / đã hết hạn / hết hàng** để demo.

> Khi chuyển giữa các branch, nên **gỡ app (hoặc Clear data)** trước khi chạy lại để CSDL được tạo mới theo đúng schema & dữ liệu mẫu.

## Kiến trúc (phân lớp theo OOP)

```
com.example.fruitshop
├── data
│   ├── db        DatabaseContract (tên bảng/cột), DatabaseHelper (Singleton SQLiteOpenHelper), FruitSeeder
│   ├── model     Account, Customer, Fruit, Order, OrderItem, Role, ExpiryStatus, OrderStatus
│   └── dao       BaseDao<T> (Template Method) → AccountDao, CustomerDao, FruitDao, OrderDao
├── service       AuthService, FruitService, OrderService, ServiceResult<T>   ← toàn bộ nghiệp vụ
├── session       SessionManager (SharedPreferences)
├── util          DateUtils, FormatUtils, PasswordHasher (SHA-256), Validator
└── ui
    ├── base      BaseActivity, DashboardActivity, BaseFruitListActivity, BaseOrderListActivity
    ├── auth      LoginActivity, RegisterActivity
    ├── admin     AdminDashboardActivity, AdminFruitListActivity, FruitFormActivity, AdminOrderListActivity
    ├── customer  CustomerHomeActivity, CustomerFruitListActivity, PlaceOrderDialog, OrderHistoryActivity
    └── adapter   FruitAdapter, OrderAdapter
```

Luồng gọi: **UI (Activity) → Service (nghiệp vụ, validate, transaction) → DAO (SQL) → SQLite**. UI không truy cập DAO trực tiếp.

### Cơ sở dữ liệu

| Bảng          | Cột                                                                 |
|---------------|---------------------------------------------------------------------|
| `accounts`    | id, username, password (SHA-256), role (`ADMIN`/`CUSTOMER`)          |
| `customers`   | id, fullname, phone, email, accountId → accounts                    |
| `fruits`      | id, name, category, price, quantity, expiryDate (`yyyy-MM-dd`), description |
| `orders`      | id, customerId → customers, orderDate, totalAmount, status          |
| `order_items` | *(bảng phụ)* id, orderId → orders, fruitId → fruits, fruitName, quantity, unitPrice |

`order_items` là bảng chi tiết bắt buộc để biết một đơn gồm trái cây nào (bảng `Order` của đề không có cột sản phẩm).

### Quy tắc nghiệp vụ chính

- **Hạn sử dụng**: hết hạn khi `expiryDate < hôm nay`; còn ≤ 3 ngày → "Sắp hết hạn". Admin lọc được hàng hết hạn; Customer chỉ thấy hàng còn hạn & còn hàng.
- **Chặn đặt hàng hết hạn** ở 3 lớp: danh sách khách không hiển thị → dialog kiểm tra → `OrderService` kiểm tra lại trong transaction.
- **Đặt hàng**: kiểm tra tồn kho, trừ kho và lưu đơn trong **một transaction**.
- **Trạng thái đơn**: `PENDING → CONFIRMED → COMPLETED`, hoặc `→ CANCELLED` (hoàn lại tồn kho).
- **SharedPreferences**: lưu phiên đăng nhập (tự vào thẳng trang chủ khi mở lại app) và "Ghi nhớ tài khoản".

## Phân công (3 người – 3 branch)

Các branch được xếp chồng theo thứ tự phụ thuộc: `main` → `chudu2110` → `andangiudayne` → `NTDSuper`. Merge lần lượt vào `main` theo đúng thứ tự này (không xung đột).

### 👤 chudu2110 — Đăng nhập, phân quyền & nền tảng CSDL
- `DatabaseContract`, `DatabaseHelper` (tạo toàn bộ 5 bảng, khoá ngoại, seed tài khoản)
- `Account`, `Customer`, `Role`, `BaseDao`, `AccountDao`, `CustomerDao`
- `AuthService` (đăng nhập, đăng ký trong transaction), `ServiceResult`
- `SessionManager` (SharedPreferences), `PasswordHasher`, `Validator`
- UI: `MainActivity` (điều hướng theo phiên), `LoginActivity`, `RegisterActivity`, `BaseActivity`, `DashboardActivity`, trang chủ Admin/Customer

### 👤 andangiudayne — Quản lý trái cây & hạn sử dụng
- `Fruit`, `ExpiryStatus`, `FruitDao` (CRUD, tìm kiếm, lọc hết hạn/còn bán), `FruitSeeder`
- `FruitService` (validate, thêm/sửa/xoá/tìm), `DateUtils`, `FormatUtils`
- UI: `BaseFruitListActivity`, `AdminFruitListActivity`, `FruitFormActivity` (DatePicker), `CustomerFruitListActivity`, `FruitAdapter`

### 👤 NTDSuper — Đặt hàng & quản lý đơn hàng
- `Order`, `OrderItem`, `OrderStatus` (state machine), `OrderDao` (JOIN khách hàng, chi tiết đơn)
- `OrderService` (đặt hàng + trừ kho trong transaction, chặn hàng hết hạn, cập nhật trạng thái, huỷ đơn hoàn kho)
- UI: `PlaceOrderDialog`, `OrderHistoryActivity`, `AdminOrderListActivity`, `BaseOrderListActivity`, `OrderAdapter`
