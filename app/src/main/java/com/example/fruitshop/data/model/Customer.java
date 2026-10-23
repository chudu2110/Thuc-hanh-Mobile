package com.example.fruitshop.data.model;

/** Entity ánh xạ bảng Customer(id, fullname, phone, email, accountId). */
public class Customer {

    private long id;
    private String fullname;
    private String phone;
    private String email;
    private long accountId;

    public Customer() {
    }

    public Customer(long id, String fullname, String phone, String email, long accountId) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.accountId = accountId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
