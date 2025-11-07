package bean;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 商品情報を保持するBeanクラス
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    // 商品ID（自動採番）
    private int productId;

    // 店舗ID
    private String storeId;

    // 商品名
    private String productName;

    // 個数
    private int quantity;

    // 消費期限
    private Date expirationDate;

    // タグ（カンマ区切り）
    private String tags;

    // 画像ファイルパス
    private String imagePath;

    // 登録日時
    private Timestamp createdAt;

    // 更新日時
    private Timestamp updatedAt;

    // 削除フラグ（0:有効, 1:削除済み）
    private int deleteFlag;

    /**
     * デフォルトコンストラクタ
     */
    public Product() {
        this.deleteFlag = 0;
    }

    /**
     * コンストラクタ（商品登録用）
     */
    public Product(String storeId, String productName, int quantity,
                   Date expirationDate, String tags, String imagePath) {
        this.storeId = storeId;
        this.productName = productName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.tags = tags;
        this.imagePath = imagePath;
        this.deleteFlag = 0;
    }

    // Getter and Setter メソッド

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /**
     * オブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", storeId='" + storeId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", expirationDate=" + expirationDate +
                ", tags='" + tags + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}