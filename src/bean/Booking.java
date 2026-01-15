package bean;

import java.sql.Timestamp;

public class Booking {
	private int bookingId; // 予約ID
	private int count; // 個数
	private int userId; // ユーザーID
	private Timestamp pickupTime; // 受け取り時間
	private int productId; // 商品ID
	private Timestamp bookingTime; // 予約時間
	private boolean pickupStatus; // 受け取りステータス
	private String merchandiseName;
	private int amount;
	private Timestamp receivedTime;

	public String getMerchandiseName() {
	    return merchandiseName;
	}

	public void setMerchandiseName(String merchandiseName) {
	    this.merchandiseName = merchandiseName;
	}


	// コンストラクタ
	public Booking(){
		;
	}

	public Booking(int bookingId, int count, int userId,
			Timestamp pickupTime, int productId, Timestamp bookingTime, boolean pickupStatus){
		this.bookingId = bookingId;
		this.count = count;
		this.userId = userId;
		this.pickupTime = pickupTime;
		this.productId = productId;
		this.bookingTime = bookingTime;
		this.pickupStatus = pickupStatus;
	}

	// 予約IDのゲッター・セッター
	public int getBookingId(){
		return bookingId;
	}

	public void setBookingId(int bookingId){
		this.bookingId = bookingId;
	}

	// 個数のゲッター・セッター
	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	// ユーザーIDのゲッター・セッター
	public int getUserId(){
		return userId;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	// 商品IDのゲッター・セッター
	public int getProductId(){
		return productId;
	}

	public void setProductId(int productId){
		this.productId = productId;
	}

	// 受け取り時間のゲッター・セッター
	public Timestamp getPickupTime(){
		return pickupTime;
	}

	public void setPickupTime(Timestamp pickupTime){
		this.pickupTime = pickupTime;
	}

	// 予約時間のゲッター・セッター
	public Timestamp getBookingTime(){
		return bookingTime;
	}

	public void setBookingTime(Timestamp bookingTime){
		this.bookingTime = bookingTime;
	}

	// 受け取りステータスのゲッター・セッター
	public boolean getPickupStatus(){
		return pickupStatus;
	}

	public void setPickupStatus(boolean pickupStatus){
		this.pickupStatus = pickupStatus;
	}
	public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    // 実際の受取時刻のゲッター・セッター
    public Timestamp getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Timestamp receivedTime) {
        this.receivedTime = receivedTime;
    }

}
