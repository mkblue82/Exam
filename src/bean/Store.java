package bean;

import java.sql.Time;

public class Store implements java.io.Serializable {

    private int storeId;        // T001_PK1_store（店舗ID）
    private String storeName;   // T001_FD1_store（店舗名）
    private String address;     // T001_FD2_store（住所）
    private String phone;       // T001_FD3_store（電話番号）
    private String password;    // T001_FD4_store（パスワード）
    private Time discountTime;  // T001_FD5_store（割引時間）
    private int discountRate;   // T001_FD6_store（割引率）
    private String email;       // T001_FD7_store（メールアドレス）
    private byte[] license;     // T001_FD8_store（営業許可証）


    // --- Getter ---
    public int getStoreId() {
        return storeId;
    }
    public String getStoreName() {
        return storeName;
    }
    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
    public String getPassword() {
        return password;
    }
    public Time getDiscountTime() {
        return discountTime;
    }
    public int getDiscountRate() {
        return discountRate;
    }
    public String getEmail() {
        return email;
    }
    public byte[] getLicense() {
        return license;
    }
    // public String getLicenseFileName() { // 削除
    //     return licenseFileName;
    // }

    // --- Setter ---
    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setDiscountTime(Time discountTime) {
        this.discountTime = discountTime;
    }
    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setLicense(byte[] license) {
        this.license = license;
    }


}