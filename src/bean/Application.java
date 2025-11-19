package bean;

import java.sql.Timestamp;

/**
 * 店舗申請データを扱うBeanクラス
 * テーブル: T001_1_applications
 */
public class Application implements java.io.Serializable {

    private int applicationId;          // T001_1_PK1_applications（申請ID）
    private String storeName;           // T001_1_FD1_applications（店舗名）
    private String storeAddress;        // T001_1_FD2_applications（店舗住所）
    private String storePhone;          // T001_1_FD3_applications（店舗電話番号）
    private String storeEmail;          // T001_1_FD4_applications（店舗メールアドレス）
    private String passwordHash;        // T001_1_FD5_applications（パスワードハッシュ）
    private byte[] businessLicense;     // T001_1_FD6_applications（営業許可証）
    private String approvalToken;       // T001_1_FD7_applications（承認トークン）
    private String status;              // T001_1_FD8_applications（ステータス）
    private Timestamp createdAt;        // T001_1_FD9_applications（申請日時）
    private Timestamp approvedAt;       // T001_1_FD10_applications（承認日時）

    // --- Getter ---
    public int getApplicationId() {
        return applicationId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public byte[] getBusinessLicense() {
        return businessLicense;
    }

    public String getApprovalToken() {
        return approvalToken;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    // --- Setter ---
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setBusinessLicense(byte[] businessLicense) {
        this.businessLicense = businessLicense;
    }

    public void setApprovalToken(String approvalToken) {
        this.approvalToken = approvalToken;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }
}