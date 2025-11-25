package bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 検索結果を店舗ごとにグループ化するためのBean
 */
public class Search {

    private Store store;                      // 店舗情報
    private List<Merchandise> merchandises;   // その店舗の商品リスト

    /**
     * コンストラクタ
     */
    public Search() {
        this.merchandises = new ArrayList<>();
    }

    /**
     * コンストラクタ（店舗情報を指定）
     */
    public Search(Store store) {
        this.store = store;
        this.merchandises = new ArrayList<>();
    }

    // ========== Getter / Setter ==========

    /**
     * 店舗情報を取得
     */
    public Store getStore() {
        return store;
    }

    /**
     * 店舗情報を設定
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * 店舗IDを取得
     */
    public int getStoreId() {
        return store != null ? store.getStoreId() : 0;
    }

    /**
     * 店舗名を取得
     */
    public String getStoreName() {
        return store != null ? store.getStoreName() : "";
    }

    /**
     * 店舗住所を取得
     */
    public String getStoreAddress() {
        return store != null ? store.getAddress() : "";
    }

    /**
     * 店舗電話番号を取得
     */
    public String getStorePhone() {
        return store != null ? store.getPhone() : "";
    }

    /**
     * 商品リストを取得
     */
    public List<Merchandise> getMerchandises() {
        return merchandises;
    }

    /**
     * 商品リストを設定
     */
    public void setMerchandises(List<Merchandise> merchandises) {
        this.merchandises = merchandises;
    }

    /**
     * 商品を追加
     */
    public void addMerchandise(Merchandise merchandise) {
        if (merchandise != null) {
            this.merchandises.add(merchandise);
        }
    }

    /**
     * 商品数を取得
     */
    public int getMerchandiseCount() {
        return merchandises != null ? merchandises.size() : 0;
    }

    /**
     * 指定した商品IDが既に含まれているかチェック
     */
    public boolean hasMerchandise(int merchandiseId) {
        if (merchandises == null) {
            return false;
        }
        for (Merchandise m : merchandises) {
            if (m.getMerchandiseId() == merchandiseId) {
                return true;
            }
        }
        return false;
    }

    /**
     * 商品リストが空かどうか
     */
    public boolean isEmpty() {
        return merchandises == null || merchandises.isEmpty();
    }

    @Override
    public String toString() {
        return "Search{" +
                "storeId=" + getStoreId() +
                ", storeName='" + getStoreName() + '\'' +
                ", merchandiseCount=" + getMerchandiseCount() +
                '}';
    }
}