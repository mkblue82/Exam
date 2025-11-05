package bean;

import java.sql.Date;
import java.sql.Timestamp;

public class Merchandise implements java.io.Serializable {
    private int productId;
    private int stock;
    private int price;
    private Date useByDate;
    private String productTag;
    private String productName;
    private int employeeId;
    private Timestamp registrationTime;
    private int storeId;
    private boolean bookingStatus;

    // コンストラクタ
    public Merchandise(){
    	;
    }

    public Merchandise(int productId, int stock, int price, Date useByDate,
    		String productTag, String productName, int employeeId, Timestamp registrationTime,
    		int storeId, boolean bookingStatus){

    	this.productId = productId;
    	this.stock = stock;
    	this.price = price;
    	this.useByDate = useByDate;
    	this.productTag = productTag;
    	this.productName = productName;
    	this.employeeId = employeeId;
    	this.registrationTime = registrationTime;
    	this.storeId = storeId;
    	this.bookingStatus = bookingStatus;
    }

    // 商品IDのゲッター・セッター
    public int getProductId(){
    	return productId;
    }

    public void setProductId(int productId){
    	this.productId = productId;
    }

    // 在庫数のゲッター・セッター
    public int getStock(){
    	return stock;
    }

    public void setStock(int stock){
    	this.stock = stock;
    }

    // 価格のゲッター・セッター
    public int getPrice(){
    	return price;
    }

    public void setPrice(int price){
    	this.price = price;
    }

    // 消費期限のゲッター・セッター
    public Date getUseByDate(){
    	return useByDate;
    }

    public void setUseByDate(Date useByDate){
    	this.useByDate = useByDate;
    }

    // タグのゲッター・セッター
    public String getProductTag(){
    	return productTag;
    }

    public void setProductTag(String productTag){
    	this.productTag = productTag;
    }

    // 商品名のゲッター・セッター
    public String getProductName(){
    	return productName;
    }

    public void setProductName(String productName){
    	this.productName = productName;
    }

    // 社員番号のゲッター・セッター
    public int getEmployeeId(){
    	return employeeId;
    }

    public void setEmployeeId(int employeeId){
    	this.employeeId = employeeId;
    }

    // 登録時刻のゲッター・セッター
    public Timestamp getRegistrationTime(){
    	return registrationTime;
    }

    public void setRegistrationTime(Timestamp registrationTime){
    	this.registrationTime = registrationTime;
    }

    // 店舗IDのゲッター・セッター
    public int getStoreId(){
    	return storeId;
    }

    public void setStoreId(int storeId){
    	this.storeId = storeId;
    }

    // 予約ステータスのゲッター・セッター
    public boolean getBookingStatus(){
    	return bookingStatus;
    }

    public void setBookingStatus(boolean bookingStatus){
    	this.bookingStatus = bookingStatus;
    }
}


