package dao;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 割引設定専用のDAO（メモリ管理版）
 * テーブル作成・変更なしで割引機能を実装
 * アプリケーション起動中はメモリに保存
 */
public class DiscountDAO {

    private Connection connection;

    // 店舗ごとの割引設定を保存（メモリ上）
    // key: storeId, value: {discount_time, discount_rate}
    private static Map<Integer, DiscountSetting> discountSettings = new HashMap<>();

    public DiscountDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 割引設定を保存（メモリに保存）
     * @param storeId 店舗ID
     * @param discountTime 割引開始時刻（0-23）
     * @param discountRate 割引率（1-100）
     */
    public void saveDiscountSetting(int storeId, int discountTime, int discountRate) {
        DiscountSetting setting = new DiscountSetting();
        setting.discountTime = discountTime;
        setting.discountRate = discountRate;
        setting.isActive = true;
        discountSettings.put(storeId, setting);
    }

    /**
     * 店舗の割引設定を取得
     * @param storeId 店舗ID
     * @return 割引設定、設定がない場合null
     */
    public DiscountSetting getDiscountSetting(int storeId) {
        return discountSettings.get(storeId);
    }

    /**
     * 現在時刻と店舗IDから割引率を計算
     * @param storeId 店舗ID
     * @param currentHour 現在の時刻（0-23）
     * @return 割引率（0なら割引なし）
     */
    public int calculateDiscountRate(int storeId, int currentHour) {
        DiscountSetting setting = discountSettings.get(storeId);
        if (setting != null && setting.isActive && currentHour >= setting.discountTime) {
            return setting.discountRate;
        }
        return 0;
    }

    /**
     * 割引適用後の価格を計算
     * @param originalPrice 元の価格
     * @param discountRate 割引率（0-100）
     * @return 割引後の価格
     */
    public int calculateDiscountedPrice(int originalPrice, int discountRate) {
        return originalPrice - (originalPrice * discountRate / 100);
    }

    /**
     * 割引設定を無効化
     * @param storeId 店舗ID
     */
    public void deactivateDiscountSetting(int storeId) {
        DiscountSetting setting = discountSettings.get(storeId);
        if (setting != null) {
            setting.isActive = false;
        }
    }

    /**
     * 割引設定を削除
     * @param storeId 店舗ID
     */
    public void deleteDiscountSetting(int storeId) {
        discountSettings.remove(storeId);
    }

    /**
     * 全ての割引設定を取得
     * @return 全店舗の割引設定
     */
    public Map<Integer, DiscountSetting> getAllDiscountSettings() {
        return new HashMap<>(discountSettings);
    }

    /**
     * 割引設定クラス
     */
    public static class DiscountSetting {
        public int discountTime;    // 割引開始時刻（0-23）
        public int discountRate;    // 割引率（1-100）
        public boolean isActive;    // 有効フラグ

        public int getDiscountTime() {
            return discountTime;
        }

        public int getDiscountRate() {
            return discountRate;
        }

        public boolean isActive() {
            return isActive;
        }
    }
}