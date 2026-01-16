package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import dao.DAO;
import dao.FavoriteDAO;

/**
 * 値引き通知を処理するサービス（シンプル版）
 * スケジューラーを1日1回実行するため、送信履歴テーブル不要
 */
public class DiscountNotificationService {

    public static void checkAndNotify() {
        System.out.println("⏰ 値引き通知チェック開始: " + LocalTime.now());

        Connection con = null;

        try {
            DAO db = new DAO();
            con = db.getConnection();

            // 値引き設定がある全店舗を取得
            String sql = "SELECT " +
                         "s.t001_pk1_store, " +
                         "s.t001_fd1_store, " +
                         "TRIM(s.t001_fd6_store) as t001_fd6_store, " +
                         "s.t001_fd5_store " +
                         "FROM t001_store s " +
                         "WHERE s.t001_fd5_store IS NOT NULL " +
                         "AND TRIM(s.t001_fd6_store) IS NOT NULL " +
                         "AND TRIM(s.t001_fd6_store) != ''";

            System.out.println("🔍 値引き設定のある店舗を検索");
            try (PreparedStatement stmt = con.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                int count = 0;
                FavoriteDAO favoriteDAO = new FavoriteDAO(con);

                while (rs.next()) {
                    int storeId = rs.getInt("t001_pk1_store");
                    String storeName = rs.getString("t001_fd1_store");
                    String discountRateStr = rs.getString("t001_fd6_store");
                    Time discountStartTime = rs.getTime("t001_fd5_store");

                    System.out.println("📢 店舗検出: " + storeName + " (割引率:[" + discountRateStr + "])");

                    int discountRate = 0;
                    try {
                        discountRate = Integer.parseInt(discountRateStr.trim());
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ 割引率が数値ではありません: [" + discountRateStr + "]");
                        continue;
                    }

                    if (discountRate <= 0) {
                        System.out.println("⚠️ 割引率が0以下: " + discountRate);
                        continue;
                    }

                    // 通知ONのユーザーを取得
                    List<String> emails = favoriteDAO.getNotificationEnabledEmails(storeId);
                    System.out.println("📧 取得したメールアドレス数: " + emails.size());

                    if (!emails.isEmpty()) {
                        System.out.println("📤 メール送信開始...");
                        try {
                            EmailUtility.sendDiscountNotification(
                                emails, storeName, discountRate, discountStartTime
                            );
                            count++;
                        } catch (Exception e) {
                            System.err.println("⚠️ メール送信に失敗しました");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("ℹ️ 通知ONのお気に入り登録ユーザーなし: " + storeName);
                    }
                }

                System.out.println("✅ 処理完了: " + count + "店舗に送信しました");
            }

        } catch (Exception e) {
            System.err.println("❌ 値引き通知チェックでエラー");
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}