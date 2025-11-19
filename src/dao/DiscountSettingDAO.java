package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.DiscountSetting;

/**
 * 割引設定用DAO
 * 既存のMerchandiseDAOを変更せずに割引機能を実装
 */
public class DiscountSettingDAO {

    private Connection connection;

    public DiscountSettingDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 店舗の割引設定を保存または更新
     * @param storeId 店舗ID
     * @param discountTime 割引開始時刻（0-23）
     * @param discountPercent 割引率（1-100）
     * @return 更新行数
     */
    public int saveDiscountSetting(int storeId, int discountTime, int discountPercent) throws Exception {
        // 既存の設定を確認
        DiscountSetting existing = getDiscountSetting(storeId);

        if (existing != null) {
            // 更新
            PreparedStatement st = connection.prepareStatement(
                "UPDATE T_discount_setting " +
                "SET discount_time = ?, discount_percent = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE store_id = ?");
            st.setInt(1, discountTime);
            st.setInt(2, discountPercent);
            st.setInt(3, storeId);

            int line = st.executeUpdate();
            st.close();
            return line;
        } else {
            // 新規登録
            PreparedStatement st = connection.prepareStatement(
                "INSERT INTO T_discount_setting (store_id, discount_time, discount_percent, created_at, updated_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            st.setInt(1, storeId);
            st.setInt(2, discountTime);
            st.setInt(3, discountPercent);

            int line = st.executeUpdate();
            st.close();
            return line;
        }
    }

    /**
     * 店舗の割引設定を取得
     * @param storeId 店舗ID
     * @return DiscountSetting（存在しない場合null）
     */
    public DiscountSetting getDiscountSetting(int storeId) throws Exception {
        PreparedStatement st = connection.prepareStatement(
            "SELECT * FROM T_discount_setting WHERE store_id = ?");
        st.setInt(1, storeId);
        ResultSet rs = st.executeQuery();

        DiscountSetting setting = null;
        if (rs.next()) {
            setting = new DiscountSetting();
            setting.setId(rs.getInt("id"));
            setting.setStoreId(rs.getInt("store_id"));
            setting.setDiscountTime(rs.getInt("discount_time"));
            setting.setDiscountPercent(rs.getInt("discount_percent"));
            setting.setCreatedAt(rs.getTimestamp("created_at"));
            setting.setUpdatedAt(rs.getTimestamp("updated_at"));
        }

        rs.close();
        st.close();
        return setting;
    }

    /**
     * 店舗の割引設定を削除
     * @param storeId 店舗ID
     * @return 削除行数
     */
    public int deleteDiscountSetting(int storeId) throws Exception {
        PreparedStatement st = connection.prepareStatement(
            "DELETE FROM T_discount_setting WHERE store_id = ?");
        st.setInt(1, storeId);

        int line = st.executeUpdate();
        st.close();
        return line;
    }
}