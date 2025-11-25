package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Store;

public class StoreDAO {

    private Connection con;

    public StoreDAO(Connection con) {
        this.con = con;
    }

    // 全店舗を取得
    public List<Store> selectAll() throws SQLException {
        List<Store> list = new ArrayList<>();

        String sql = "SELECT * FROM T001_store ORDER BY T001_PK1_store";

        try (PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Store s = mapResultSetToStore(rs);
                list.add(s);
            }
        }
        return list;
    }

    // 店舗IDで検索
    public Store selectById(int id) throws SQLException {
        String sql = "SELECT * FROM T001_store WHERE T001_PK1_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStore(rs);
                }
            }
        }
        return null;
    }

 // 店舗を登録
    public int insert(Store store) throws SQLException {
        String sql = "INSERT INTO T001_store " +
                     "(T001_FD1_store, T001_FD2_store, T001_FD3_store, T001_FD4_store, T001_FD7_store, T001_FD8_store) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING T001_PK1_store";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, store.getStoreName());   // 店舗名
            pstmt.setString(2, store.getAddress());     // 住所
            pstmt.setString(3, store.getPhone());       // 電話番号
            pstmt.setString(4, store.getPassword());    // パスワード
            pstmt.setString(5, store.getEmail());       // メールアドレス
            pstmt.setBytes(6, store.getLicense());      // 営業許可証

            try (ResultSet rs = pstmt.executeQuery()) {
                int id = 0;
                if (rs.next()) {
                    id = rs.getInt(1);
                    store.setStoreId(id);
                }
                return id;
            }
        }
    }


    // 店舗を更新
    public void update(Store store) throws SQLException {
        // T001_FD9_store = ? を削除
        String sql = "UPDATE T001_store SET " +
                      "T001_FD1_store = ?, T001_FD2_store = ?, T001_FD3_store = ?, T001_FD4_store = ?, " +
                      "T001_FD5_store = ?, T001_FD6_store = ?, T001_FD7_store = ?, T001_FD8_store = ? " +
                      "WHERE T001_PK1_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, store.getStoreName());
            st.setString(2, store.getAddress());
            st.setString(3, store.getPhone());
            st.setString(4, store.getPassword());
            st.setTime(5, store.getDiscountTime());
            st.setInt(6, store.getDiscountRate());
            st.setString(7, store.getEmail());
            st.setBytes(8, store.getLicense());
            st.setInt(9, store.getStoreId()); // WHERE句の引数

            st.executeUpdate();
        }
    }

    // 店舗を削除
    public void delete(int storeId) throws SQLException {
        String sql = "DELETE FROM T001_store WHERE T001_PK1_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, storeId);
            st.executeUpdate();
        }
    }

    public Store login(int storeId, String hashedPassword) throws SQLException {

        String sql = "SELECT * FROM t001_store WHERE t001_pk1_store = ? AND t001_fd4_store = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Store store = new Store();


                    store.setStoreId(rs.getInt("t001_pk1_store"));
                    store.setStoreName(rs.getString("t001_fd1_store"));
                    store.setAddress(rs.getString("t001_fd2_store"));
                    store.setPhone(rs.getString("t001_fd3_store"));
                    store.setPassword(rs.getString("t001_fd4_store"));
                    store.setDiscountTime(rs.getTime("t001_fd5_store"));
                    store.setDiscountRate(rs.getInt("t001_fd6_store"));
                    store.setEmail(rs.getString("t001_fd7_store"));
                    store.setLicense(rs.getBytes("t001_fd8_store"));



                    return store;
                }
            }
        }


        return null; // 該当なし（ログイン失敗）
    }

    // ResultSetからStoreオブジェクトへのマッピング（重複コード削減）
    private Store mapResultSetToStore(ResultSet rs) throws SQLException {
        Store s = new Store();
        // 列名すべてを小文字に修正
        s.setStoreId(rs.getInt("t001_pk1_store"));
        s.setStoreName(rs.getString("t001_fd1_store"));
        s.setAddress(rs.getString("t001_fd2_store"));
        s.setPhone(rs.getString("t001_fd3_store"));
        s.setPassword(rs.getString("t001_fd4_store"));
        s.setDiscountTime(rs.getTime("t001_fd5_store"));
        s.setDiscountRate(rs.getInt("t001_fd6_store"));
        s.setEmail(rs.getString("t001_fd7_store"));
        s.setLicense(rs.getBytes("t001_fd8_store"));

        return s;
    }
// ===== 以下、検索機能を追加 =====

    /**
     * 店舗名で検索（部分一致）
     * @param keyword 検索キーワード
     * @return 検索結果のリスト
     * @throws SQLException
     */
    public List<Store> searchByKeyword(String keyword) throws SQLException {
        List<Store> list = new ArrayList<>();

        String sql = "SELECT * FROM T001_store " +
                     "WHERE T001_FD1_store LIKE ? " +
                     "ORDER BY T001_PK1_store";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Store s = mapResultSetToStore(rs);
                    list.add(s);
                }
            }
        }

        return list;
    }
}
