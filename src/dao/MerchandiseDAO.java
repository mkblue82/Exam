package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Merchandise;

public class MerchandiseDAO extends DAO {

    // 全商品を取得（店舗名も取得）
    public List<Merchandise> selectAll() throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "order by T002_PK1_merchandise");
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
            list.add(m);
        }

        st.close();
        con.close();
        return list;
    }

    // 商品IDで検索
    public Merchandise selectById(int merchandiseId) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise " +
            "from T002_merchandise " +
            "where T002_PK1_merchandise = ?");
        st.setInt(1, merchandiseId);
        ResultSet rs = st.executeQuery();

        Merchandise m = null;
        if (rs.next()) {
            m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
        }

        st.close();
        con.close();
        return m;
    }

    // 店舗IDで商品一覧を取得
    public List<Merchandise> selectByStoreId(int storeId) throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise " +
            "from T002_merchandise " +
            "where T002_FD8_merchandise = ? " +
            "order by T002_PK1_merchandise");
        st.setInt(1, storeId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
            list.add(m);
        }

        st.close();
        con.close();
        return list;
    }

    // 商品を登録
    public int insert(Merchandise merchandise) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "insert into T002_merchandise (T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");

        st.setInt(1, merchandise.getStock());
        st.setInt(2, merchandise.getPrice());
        st.setDate(3, merchandise.getUseByDate());
        st.setString(4, merchandise.getMerchandiseTag());
        st.setString(5, merchandise.getMerchandiseName());
        st.setInt(6, merchandise.getEmployeeId());
        st.setTimestamp(7, merchandise.getRegistrationTime());
        st.setInt(8, merchandise.getStoreId());
        st.setBoolean(9, merchandise.getBookingStatus());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 最新の商品IDを取得（店舗IDと商品名から）
    public int getLatestMerchandiseId(int storeId, String merchandiseName) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "SELECT T002_PK1_merchandise FROM T002_merchandise " +
            "WHERE T002_FD8_merchandise = ? AND T002_FD5_merchandise = ? " +
            "ORDER BY T002_FD7_merchandise DESC LIMIT 1"
        );
        st.setInt(1, storeId);
        st.setString(2, merchandiseName);
        ResultSet rs = st.executeQuery();

        int id = 0;
        if (rs.next()) {
            id = rs.getInt("T002_PK1_merchandise");
        }

        rs.close();
        st.close();
        con.close();
        return id;
    }


    // 商品を更新
    public int update(Merchandise merchandise) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "update T002_merchandise set T002_FD1_merchandise = ?, T002_FD2_merchandise = ?, " +
            "T002_FD3_merchandise = ?, T002_FD4_merchandise = ?, T002_FD5_merchandise = ?, " +
            "T002_FD6_merchandise = ?, T002_FD7_merchandise = ?, T002_FD8_merchandise = ?, " +
            "T002_FD9_merchandise = ? where T002_PK1_merchandise = ?");

        st.setInt(1, merchandise.getStock());
        st.setInt(2, merchandise.getPrice());
        st.setDate(3, merchandise.getUseByDate());
        st.setString(4, merchandise.getMerchandiseTag());
        st.setString(5, merchandise.getMerchandiseName());
        st.setInt(6, merchandise.getEmployeeId());
        st.setTimestamp(7, merchandise.getRegistrationTime());
        st.setInt(8, merchandise.getStoreId());
        st.setBoolean(9, merchandise.getBookingStatus());
        st.setInt(10, merchandise.getProductId());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 商品を削除
    public int delete(int merchandiseId) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "delete from T002_merchandise where T002_PK1_merchandise = ?");
        st.setInt(1, merchandiseId);

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 予約ステータスがtrueの商品のみを取得
    public List<Merchandise> selectByBookingStatus(boolean bookingStatus) throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise " +
            "from T002_merchandise " +
            "where T002_FD9_merchandise = ? " +
            "order by T002_PK1_merchandise");
        st.setBoolean(1, bookingStatus);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
            list.add(m);
        }

        st.close();
        con.close();
        return list;
    }

    // 同一店舗内で同じ商品名が存在するか確認
    public boolean isDuplicateProduct(int storeId, String merchandiseName) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "SELECT COUNT(*) FROM T002_merchandise WHERE T002_FD8_merchandise = ? AND T002_FD5_merchandise = ?");
        st.setInt(1, storeId);
        st.setString(2, merchandiseName);
        ResultSet rs = st.executeQuery();

        boolean exists = false;
        if (rs.next() && rs.getInt(1) > 0) {
            exists = true;
        }

        rs.close();
        st.close();
        con.close();
        return exists;
    }
}