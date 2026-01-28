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

    // ===== 全店舗取得 =====
    public List<Store> selectAll() throws SQLException {
        List<Store> list = new ArrayList<>();

        String sql = "SELECT * FROM T001_store ORDER BY T001_PK1_store";

        try (PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStore(rs));
            }
        }
        return list;
    }

    // ===== 店舗ID検索 =====
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

    // ===== 店舗登録 =====
    public int insert(Store store) throws SQLException {
        String sql =
            "INSERT INTO T001_store " +
            "(T001_FD1_store, T001_FD2_store, T001_FD3_store, " +
            " T001_FD4_store, T001_FD7_store, T001_FD8_store) " +  // カンマ削除
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING T001_PK1_store";  // ?を6個に

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getAddress());
            pstmt.setString(3, store.getPhone());
            pstmt.setString(4, store.getPassword());
            pstmt.setString(5, store.getEmail());
            pstmt.setBytes(6, store.getLicense());
            // pstmt.setString(7, store.getLicenseType()); // この行を削除

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    store.setStoreId(id);
                    return id;
                }
            }
        }
        return 0;
    }
    


    // ===== 店舗更新 =====
    public void update(Store store) throws SQLException {
        String sql =
            "UPDATE T001_store SET " +
            "T001_FD1_store = ?, T001_FD2_store = ?, T001_FD3_store = ?, " +
            "T001_FD4_store = ?, T001_FD5_store = ?, T001_FD6_store = ?, " +
            "T001_FD7_store = ?, T001_FD8_store = ? " +  // カンマを削除
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
            st.setInt(9, store.getStoreId());

            st.executeUpdate();
        }
    }

    // ===== 店舗削除 =====
    public void delete(int storeId) throws SQLException {
        String sql = "DELETE FROM T001_store WHERE T001_PK1_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, storeId);
            st.executeUpdate();
        }
    }

    // ===== ログイン =====
    public Store login(int storeId, String hashedPassword) throws SQLException {
        String sql =
            "SELECT * FROM T001_store " +
            "WHERE T001_PK1_store = ? AND T001_FD4_store = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStore(rs);
                }
            }
        }
        return null;
    }

    // ===== 店舗名検索（部分一致） =====
    public List<Store> searchByKeyword(String keyword) throws SQLException {
        List<Store> list = new ArrayList<>();

        String sql =
            "SELECT * FROM T001_store " +
            "WHERE T001_FD1_store LIKE ? " +
            "ORDER BY T001_PK1_store";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStore(rs));
                }
            }
        }
        return list;
    }

    // ===== メール検索 =====
    public Store selectByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM T001_store WHERE T001_FD7_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStore(rs);
                }
            }
        }
        return null;
    }

    // ===== 電話番号検索 =====
    public Store selectByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM T001_store WHERE T001_FD3_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, phone);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStore(rs);
                }
            }
        }
        return null;
    }

    // ===== ★追加：重複チェック =====
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM T001_store WHERE T001_FD7_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM T001_store WHERE T001_FD3_store = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, phone);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== ResultSet → Store =====
    private Store mapResultSetToStore(ResultSet rs) throws SQLException {
        Store s = new Store();
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

    public byte[] getLicense(int storeId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
            "SELECT t001_fd8_store FROM T001_store WHERE T001_PK1_store = ?"
        );
        ps.setInt(1, storeId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBytes("t001_fd8_store");
        }
        return null;
    }





}
