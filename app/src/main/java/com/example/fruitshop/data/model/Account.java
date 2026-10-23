package com.example.fruitshop.data.model;

/** Entity ánh xạ bảng Account(id, username, password, role). */
public class Account {

    private long id;
    private String username;
    /** Mật khẩu đã được băm SHA-256, không bao giờ lưu mật khẩu gốc. */
    private String password;
    private Role role;

    public Account() {
    }

    public Account(long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
