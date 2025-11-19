package tool;

import java.sql.Connection;

import dao.DiscountDAO;

/**
 * 割引計算用のヘルパークラス
 * JSPやActionから簡単に割引適用後の価格を取得できる
 */
public class DiscountHelper {

    /**
     * 割引適用後の価格を計算
     * @param connection データベース接続
     * @param storeId 店舗ID
     * @param originalPrice 元の価格
     * @return 割引適用後の価格
     */
    public static int getDiscountedPrice(Connection connection, int storeId, int originalPrice) {
        try {
            DiscountDAO dao = new DiscountDAO(connection);

            // 現在時刻を取得
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY);

            // 割引率を取得
            int discountRate = dao.calculateDiscountRate(storeId, currentHour);

            // 割引適用後の価格を計算
            if (discountRate > 0) {
                return dao.calculateDiscountedPrice(originalPrice, discountRate);
            }

            return originalPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return originalPrice; // エラー時は元の価格を返す
        }
    }

    /**
     * 現在の割引率を取得
     * @param connection データベース接続
     * @param storeId 店舗ID
     * @return 割引率（0なら割引なし）
     */
    public static int getCurrentDiscountRate(Connection connection, int storeId) {
        try {
            DiscountDAO dao = new DiscountDAO(connection);

            // 現在時刻を取得
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY);

            return dao.calculateDiscountRate(storeId, currentHour);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 割引中かどうかを判定
     * @param connection データベース接続
     * @param storeId 店舗ID
     * @return 割引中ならtrue
     */
    public static boolean isDiscountActive(Connection connection, int storeId) {
        return getCurrentDiscountRate(connection, storeId) > 0;
    }
}