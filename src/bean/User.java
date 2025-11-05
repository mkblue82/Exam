package bean;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String favoriteStore;
    private int storeId;
    private boolean notification; // true=ON, false=OFF

    // --- getter/setter ----
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFavoriteStore() {
        return favoriteStore;
    }
    public void setFavoriteStore(String favoriteStore) {
        this.favoriteStore = favoriteStore;
    }

    public int getStoreId() {
        return storeId;
    }
    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public boolean isNotification() {
        return notification;
    }
    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
