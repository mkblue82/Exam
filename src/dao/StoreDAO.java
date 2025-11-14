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
    public void insert(Store store) throws SQLException {
        String sql = "INSERT INTO T001_store " +
                     "(T001_FD1_store, T001_FD2_store, T001_FD3_store, T001_FD4_store, " +
                     "T001_FD5_store, T001_FD6_store, T001_FD7_store, T001_FD8_store, T001_FD9_store) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING T001_PK1_store";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getAddress());
            pstmt.setString(3, store.getPhone());
            pstmt.setString(4, store.getPassword());
            pstmt.setTime(5, store.getDiscountTime());
            pstmt.setInt(6, store.getDiscountRate());
            pstmt.setString(7, store.getEmail());
            pstmt.setBytes(8, store.getLicense());
            pstmt.setString(9, store.getLicenseFileName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    store.setStoreId(rs.getInt(1));
                }
            }
        }
    }

    // 店舗を更新
    public void update(Store store) throws SQLException {
        String sql = "UPDATE T001_store SET " +
                     "T001_FD1_store = ?, T001_FD2_store = ?, T001_FD3_store = ?, T001_FD4_store = ?, " +
                     "T001_FD5_store = ?, T001_FD6_store = ?, T001_FD7_store = ?, T001_FD8_store = ?, T001_FD9_store = ? " +
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
            st.setString(9, store.getLicenseFileName());
            st.setInt(10, store.getStoreId());

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
    s.setStoreId(rs.getInt("T001_PK1_store"));
    s.setStoreName(rs.getString("T001_FD1_store"));
    s.setAddress(rs.getString("T001_FD2_store"));
    s.setPhone(rs.getString("T001_FD3_store"));
    s.setPassword(rs.getString("T001_FD4_store"));
    s.setDiscountTime(rs.getTime("T001_FD5_store"));
    s.setDiscountRate(rs.getInt("T001_FD6_store"));
    s.setEmail(rs.getString("T001_FD7_store"));
    s.setLicense(rs.getBytes("T001_FD8_store"));
    s.setLicenseFileName(rs.getString("T001_FD9_store"));
    return s;
}


}


