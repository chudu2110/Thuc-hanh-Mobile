package com.example.fruitshop.service;

import android.content.Context;

import com.example.fruitshop.data.dao.FruitDao;
import com.example.fruitshop.data.db.DatabaseHelper;
import com.example.fruitshop.data.model.Fruit;
import com.example.fruitshop.util.DateUtils;
import com.example.fruitshop.util.Validator;

import java.util.List;

/**
 * Nghiệp vụ trái cây:
 * - Admin: thêm / sửa / xoá / tìm kiếm / xem, lọc hàng hết hạn.
 * - Customer: xem & tìm trái cây còn bán (còn hạn, còn hàng).
 */
public class FruitService {

    private final FruitDao fruitDao;

    public FruitService(Context context) {
        this.fruitDao = new FruitDao(DatabaseHelper.getInstance(context));
    }

    // ===== Truy vấn =====

    public Fruit getById(long id) {
        return fruitDao.findById(id);
    }

    /** Admin: tìm toàn bộ; expiredOnly = true để chỉ xem hàng đã hết hạn. */
    public List<Fruit> searchForAdmin(String keyword, boolean expiredOnly) {
        FruitDao.Filter filter = expiredOnly ? FruitDao.Filter.EXPIRED_ONLY : FruitDao.Filter.ALL;
        return fruitDao.search(keyword, filter, DateUtils.today());
    }

    /** Customer: chỉ hiển thị trái cây còn hạn và còn hàng. */
    public List<Fruit> searchAvailable(String keyword) {
        return fruitDao.search(keyword, FruitDao.Filter.AVAILABLE_ONLY, DateUtils.today());
    }

    public int countExpired() {
        return fruitDao.countExpired(DateUtils.today());
    }

    // ===== Thêm / sửa / xoá (Admin) =====

    public ServiceResult<Fruit> add(Fruit fruit) {
        String error = validate(fruit);
        if (error != null) {
            return ServiceResult.failure(error);
        }
        normalize(fruit);
        if (fruitDao.insert(fruit) == -1) {
            return ServiceResult.failure("Không thể thêm trái cây");
        }
        return ServiceResult.success(fruit, "Đã thêm " + fruit.getName());
    }

    public ServiceResult<Fruit> update(Fruit fruit) {
        if (fruit.getId() <= 0 || fruitDao.findById(fruit.getId()) == null) {
            return ServiceResult.failure("Trái cây không tồn tại");
        }
        String error = validate(fruit);
        if (error != null) {
            return ServiceResult.failure(error);
        }
        normalize(fruit);
        if (!fruitDao.update(fruit)) {
            return ServiceResult.failure("Cập nhật thất bại");
        }
        return ServiceResult.success(fruit, "Đã cập nhật " + fruit.getName());
    }

    public ServiceResult<Void> delete(long id) {
        Fruit fruit = fruitDao.findById(id);
        if (fruit == null) {
            return ServiceResult.failure("Trái cây không tồn tại");
        }
        if (!fruitDao.delete(id)) {
            return ServiceResult.failure("Xoá thất bại");
        }
        return ServiceResult.success(null, "Đã xoá " + fruit.getName());
    }

    // ===== Kiểm tra dữ liệu =====

    private String validate(Fruit f) {
        if (Validator.isBlank(f.getName())) {
            return "Tên trái cây không được để trống";
        }
        if (Validator.isBlank(f.getCategory())) {
            return "Loại trái cây không được để trống";
        }
        if (f.getPrice() <= 0) {
            return "Giá phải lớn hơn 0";
        }
        if (f.getQuantity() < 0) {
            return "Số lượng không được âm";
        }
        if (!DateUtils.isValidDbDate(f.getExpiryDate())) {
            return "Vui lòng chọn ngày hết hạn hợp lệ";
        }
        return null;
    }

    private void normalize(Fruit f) {
        f.setName(f.getName().trim());
        f.setCategory(f.getCategory().trim());
        f.setDescription(f.getDescription() == null ? "" : f.getDescription().trim());
    }
}
