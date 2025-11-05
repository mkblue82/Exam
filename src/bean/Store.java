package bean;

import java.sql.Time;

public class Store implements java.io.Serializable {
    private int storeId;
    private String storeName;
    private String address;
    private String phone;
    private String representative;
    private Time openingTime;
    private String category;
    private String email;
    private byte[] license;
    private String licenseFileName;

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
    public String getRepresentative() {
        return representative;
    }
    public Time getOpeningTime() {
        return openingTime;
    }
    public String getCategory() {
        return category;
    }
    public String getEmail() {
        return email;
    }
    public byte[] getLicense() {
        return license;
    }
    public String getLicenseFileName() {
        return licenseFileName;
    }

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
    public void setRepresentative(String representative) {
        this.representative = representative;
    }
    public void setOpeningTime(Time openingTime) {
        this.openingTime = openingTime;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setLicense(byte[] license) {
        this.license = license;
    }
    public void setLicenseFileName(String licenseFileName) {
        this.licenseFileName = licenseFileName;
    }
}