package bean;

import java.sql.Timestamp;

/**
 * 店舗申請データを扱うBeanクラス
 * テーブル: T001_1_applications
 */
public class Application implements java.io.Serializable {

    private int applicationId;          // 申請ID
    private String storeName;           // 店舗名
    private String storeAddress;        // 店舗住所
    private String storePhone;          // 店舗電話番号
    private String storeEmail;          // 店舗メールアドレス
    private String passwordHash;        // パスワードハッシュ
    private byte[] businessLicense;     // 営業許可証バイト配列
    private String approvalToken;       // 承認トークン
    private String status;              // ステータス
    private Timestamp createdAt;        // 申請日時
    private Timestamp approvedAt;       // 承認日時

    // Action用：DBには保存しないファイルパス
    private String businessLicensePath;

    // --- ゲッター・セッター ---

    // 申請ID
    public int getApplicationId() {
        return applicationId;
    }
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    // 店舗名
    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    // 店舗住所
    public String getStoreAddress() {
        return storeAddress;
    }
    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    // 店舗電話番号
    public String getStorePhone() {
        return storePhone;
    }
    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    // 店舗メールアドレス
    public String getStoreEmail() {
        return storeEmail;
    }
    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    // パスワードハッシュ
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // 営業許可証（DB保存用）
    public byte[] getBusinessLicense() {
        return businessLicense;
    }
    public void setBusinessLicense(byte[] businessLicense) {
        this.businessLicense = businessLicense;
    }

    // 承認トークン
    public String getApprovalToken() {
        return approvalToken;
    }
    public void setApprovalToken(String approvalToken) {
        this.approvalToken = approvalToken;
    }

    // ステータス
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // 申請日時
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // 承認日時
    public Timestamp getApprovedAt() {
        return approvedAt;
    }
    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    // Action用：ファイルパス
    public String getBusinessLicensePath() {
        return businessLicensePath;
    }
    public void setBusinessLicensePath(String businessLicensePath) {
        this.businessLicensePath = businessLicensePath;
    }
}
