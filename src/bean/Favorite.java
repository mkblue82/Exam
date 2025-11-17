package bean;

public class Favorite {
    private int favoriteStoreId;
    private int userId;
    private int storeId;
    private boolean notificationSetting;

    // 追加：店舗情報（表示用）
    private String storeName;
    private String storeAddress;

    // お気に入り店舗のゲッター・セッター
    public int getFavoriteStoreId(){
        return favoriteStoreId;
    }
    public void setFavoriteStoreId(int favoriteStoreId){
        this.favoriteStoreId = favoriteStoreId;
    }

    // ユーザーIDのゲッター・セッター
    public int getUserId(){
        return userId;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }

    // 店舗IDのゲッター・セッター
    public int getStoreId(){
        return storeId;
    }
    public void setStoreId(int storeId){
        this.storeId = storeId;
    }

    // 通知設定のゲッター・セッター
    public boolean getNotificationSetting(){
        return notificationSetting;
    }
    public void setNotificationSetting(boolean notificationSetting){
        this.notificationSetting = notificationSetting;
    }

    // 店舗名のゲッター・セッター（追加）
    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    // 店舗住所のゲッター・セッター（追加）
    public String getStoreAddress() {
        return storeAddress;
    }
    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }
}