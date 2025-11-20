package bean;

import java.sql.Timestamp;

/**
 * 割引設定ビーン
 */
public class DiscountSetting implements java.io.Serializable {

    private int id;
    private int storeId;
    private int discountTime;        // 割引開始時刻（0-23）
    private int discountPercent;     // 割引率（1-100）
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getDiscountTime() {
        return discountTime;
    }

    public void setDiscountTime(int discountTime) {
        this.discountTime = discountTime;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
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
}