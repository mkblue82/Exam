package bean;

import java.sql.Date;
import java.sql.Timestamp;

public class Merchandise implements java.io.Serializable {

    // 既存のフィールド（そのまま）
    private int merchandiseId;
    private int stock;
    private int price;
    private Date useByDate;
    private String merchandiseTag;
    private String merchandiseName;
    private int employeeId;
    private Timestamp registrationTime;
    private int storeId;
    private boolean bookingStatus;

    // ========== 追加: 割引関連フィールド ==========
    private Integer originalPrice;      // 元の価格（割引前）
    private int discountPercent;        // 割引率（0-100）
    private Integer discountStartTime;  // 割引開始時刻（0-23、null可）


    // ========== 既存のgetter/setter（そのまま） ==========

    public int getMerchandiseId() {
        return merchandiseId;
    }

    public void setMerchandiseId(int merchandiseId) {
        this.merchandiseId = merchandiseId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getUseByDate() {
        return useByDate;
    }

    public void setUseByDate(Date useByDate) {
        this.useByDate = useByDate;
    }

    public String getMerchandiseTag() {
        return merchandiseTag;
    }

    public void setMerchandiseTag(String merchandiseTag) {
        this.merchandiseTag = merchandiseTag;
    }

    public String getMerchandiseName() {
        return merchandiseName;
    }

    public void setMerchandiseName(String merchandiseName) {
        this.merchandiseName = merchandiseName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Timestamp getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Timestamp registrationTime) {
        this.registrationTime = registrationTime;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public boolean getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(boolean bookingStatus) {
        this.bookingStatus = bookingStatus;
    }


    // ========== 追加: 割引関連のgetter/setter ==========

    /**
     * 元の価格を取得
     * @return 元の価格（割引前の価格）
     */
    public Integer getOriginalPrice() {
        return originalPrice;
    }

    /**
     * 元の価格を設定
     * @param originalPrice 元の価格
     */
    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    /**
     * 割引率を取得
     * @return 割引率（0-100）
     */
    public int getDiscountPercent() {
        return discountPercent;
    }

    /**
     * 割引率を設定
     * @param discountPercent 割引率（0-100）
     */
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * 割引開始時刻を取得
     * @return 割引開始時刻（0-23、設定なしの場合null）
     */
    public Integer getDiscountStartTime() {
        return discountStartTime;
    }

    /**
     * 割引開始時刻を設定
     * @param discountStartTime 割引開始時刻（0-23）
     */
    public void setDiscountStartTime(Integer discountStartTime) {
        this.discountStartTime = discountStartTime;
    }
}