package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bean.Store;

public class StoreDAO extends DAO {

    // 全店舗を取得
    public List<Store> selectAll() throws Exception {
        List<Store> list = new ArrayList<>();
        Connection con = getConnection();

        PreparedStatement st = con.prepareStatement(
            "SELECT * FROM T001_store ORDER BY T001_PK1_store");
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Store s = mapResultSetToStore(rs);
            list.add(s);
        }

        rs.close();
        st.close();
        con.close();
        return list;
    }

    // 店舗IDで検索
    public Store selectById(int id) throws Exception {
        Connection con = getConnection();

        PreparedStatement st = con.prepareStatement(
            "SELECT * FROM T001_store WHERE T001_PK1_store = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        Store s = null;
        if (rs.next()) {
            s = mapResultSetToStore(rs);
        }

        rs.close();
        st.close();
        con.close();
        return s;
    }

    /**
     * 店舗を登録（承認後の正式登録用）
     * @return 生成されたstoreId
     */
    public int insert(Store store) throws Exception {
        Connection con = getConnection();

        String sql = "INSERT INTO T001_store " +
                     "(T001_FD1_store, T001_FD2_store, T001_FD3_store, T001_FD4_store, T001_FD7_store, T001_FD8_store) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pstmt.setString(1, store.getStoreName());   // 店舗名
        pstmt.setString(2, store.getAddress());     // 住所
        pstmt.setString(3, store.getPhone());       // 電話番号
        pstmt.setString(4, store.getPassword());    // パスワード（ハッシュ済み）
        pstmt.setString(5, store.getEmail());       // メールアドレス
        pstmt.setBytes(6, store.getLicense());      // 営業許可証 (bytea)

        int line = pstmt.executeUpdate();

        int generatedId = 0;
        if (line > 0) {
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                store.setStoreId(generatedId);
            }
            rs.close();
        }

        pstmt.close();
        con.close();
        return generatedId;
    }

    /**
     * メールアドレスで店舗が存在するか確認
     */
    public boolean existsByEmail(String email) throws Exception {
        Connection con = getConnection();

        PreparedStatement pstmt = con.prepareStatement(
            "SELECT COUNT(*) FROM T001_store WHERE T001_FD7_store = ?");
        pstmt.setString(1, email);

        ResultSet rs = pstmt.executeQuery();

        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }

        rs.close();
        pstmt.close();
        con.close();
        return exists;
    }

    // 店舗を更新
    public int update(Store store) throws Exception {
        Connection con = getConnection();

        PreparedStatement st = con.prepareStatement(
            "UPDATE T001_store SET " +
            "T001_FD1_store = ?, T001_FD2_store = ?, T001_FD3_store = ?, T001_FD4_store = ?, " +
            "T001_FD5_store = ?, T001_FD6_store = ?, T001_FD7_store = ?, T001_FD8_store = ? " +
            "WHERE T001_PK1_store = ?");

        st.setString(1, store.getStoreName());
        st.setString(2, store.getAddress());
        st.setString(3, store.getPhone());
        st.setString(4, store.getPassword());
        st.setTime(5, store.getDiscountTime());
        st.setInt(6, store.getDiscountRate());
        st.setString(7, store.getEmail());
        st.setBytes(8, store.getLicense());
        st.setInt(9, store.getStoreId());

        int line = st.executeUpdate();

        st.close();
        con.close();
        return line;
    }

    // 店舗を削除
    public int delete(int storeId) throws Exception {
        Connection con = getConnection();

        PreparedStatement st = con.prepareStatement(
            "DELETE FROM T001_store WHERE T001_PK1_store = ?");
        st.setInt(1, storeId);

        int line = st.executeUpdate();

        st.close();
        con.close();
        return line;
    }

    /**
     * ログイン処理
     */
    public Store login(int storeId, String hashedPassword) throws Exception {
        Connection con = getConnection();

        PreparedStatement pstmt = con.prepareStatement(
            "SELECT * FROM t001_store WHERE t001_pk1_store = ? AND t001_fd4_store = ?");
        pstmt.setInt(1, storeId);
        pstmt.setString(2, hashedPassword);

        ResultSet rs = pstmt.executeQuery();

        Store store = null;
        if (rs.next()) {
            store = mapResultSetToStore(rs);
        }

        rs.close();
        pstmt.close();
        con.close();
        return store;
    }

    // ResultSetからStoreオブジェクトへのマッピング
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
}