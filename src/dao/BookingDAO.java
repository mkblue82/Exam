package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Booking;

public class BookingDAO extends DAO {

    // 全予約を取得
    public List<Booking> selectAll() throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking " +
            "order by T005_PK1_booking");
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));
            list.add(b);
        }

        st.close();
        con.close();
        return list;
    }

    // 予約IDで検索
    public Booking selectById(int bookingId) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking " +
            "where T005_PK1_booking = ?");
        st.setInt(1, bookingId);
        ResultSet rs = st.executeQuery();

        Booking b = null;
        if (rs.next()) {
            b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));
        }

        st.close();
        con.close();
        return b;
    }

    // ユーザーIDで予約一覧を取得
    public List<Booking> selectByUserId(int userId) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking " +
            "where T005_FD2_booking = ? " +
            "order by T005_PK1_booking");
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));
            list.add(b);
        }

        st.close();
        con.close();
        return list;
    }

    // 商品IDで予約一覧を取得
    public List<Booking> selectByProductId(int productId) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking " +
            "where T005_FD4_booking = ? " +
            "order by T005_PK1_booking");
        st.setInt(1, productId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));
            list.add(b);
        }

        st.close();
        con.close();
        return list;
    }

    // 受け取りステータスで予約一覧を取得
    public List<Booking> selectByPickupStatus(boolean pickupStatus) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking " +
            "where T005_FD6_booking = ? " +
            "order by T005_PK1_booking");
        st.setBoolean(1, pickupStatus);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));
            list.add(b);
        }

        st.close();
        con.close();
        return list;
    }

 // 店舗IDで予約一覧を取得（商品名JOIN付き）
    public List<Booking> selectByStoreId(int storeId) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getConnection();

        PreparedStatement st = con.prepareStatement(
            "select b.T005_PK1_booking, b.T005_FD1_booking, b.T005_FD2_booking, " +
            "b.T005_FD3_booking, b.T005_FD4_booking, b.T005_FD5_booking, b.T005_FD6_booking, " +
            "m.T002_FD5_merchandise as merchandiseName " +        // ★ 商品名取得
            "from T005_booking b " +
            "join T002_merchandise m " +
            "on b.T005_FD4_booking = m.T002_PK1_merchandise " +   // ★ 商品IDで結合
            "where m.T002_FD8_merchandise = ? " +                 // ★ 店舗IDで絞る
            "order by b.T005_PK1_booking"
        );

        st.setInt(1, storeId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("T005_PK1_booking"));
            b.setCount(rs.getInt("T005_FD1_booking"));
            b.setUserId(rs.getInt("T005_FD2_booking"));
            b.setPickupTime(rs.getTimestamp("T005_FD3_booking"));
            b.setProductId(rs.getInt("T005_FD4_booking"));
            b.setBookingTime(rs.getTimestamp("T005_FD5_booking"));
            b.setPickupStatus(rs.getBoolean("T005_FD6_booking"));

            // ★ 商品名セット
            b.setMerchandiseName(rs.getString("merchandiseName"));

            list.add(b);
        }

        rs.close();
        st.close();
        con.close();
        return list;
    }



    // 予約を作成
//    public int insert(Booking booking) throws Exception {
//        Connection con = getConnection();
//        PreparedStatement st = con.prepareStatement(
//            "insert into T005_booking (T005_FD1_booking, T005_FD2_booking, " +
//            "T005_FD3_booking, T005_FD4_booking, T005_FD5_booking, T005_FD6_booking) " +
//            "values (?, ?, ?, ?, ?, ?)");
//
//        st.setInt(1, booking.getCount());
//        st.setInt(2, booking.getUserId());
//        st.setTimestamp(3, booking.getPickupTime());
//        st.setInt(4, booking.getProductId());
//        st.setTimestamp(5, booking.getBookingTime());
//        st.setBoolean(6, booking.getPickupStatus());
//
//        int line = st.executeUpdate();
//        st.close();
//        con.close();
//        return line;
//    }

    // 予約を更新
    public int update(Booking booking) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "update T005_booking set T005_FD1_booking = ?, T005_FD2_booking = ?, " +
            "T005_FD3_booking = ?, T005_FD4_booking = ?, T005_FD5_booking = ?, " +
            "T005_FD6_booking = ? where T005_PK1_booking = ?");

        st.setInt(1, booking.getCount());
        st.setInt(2, booking.getUserId());
        st.setTimestamp(3, booking.getPickupTime());
        st.setInt(4, booking.getProductId());
        st.setTimestamp(5, booking.getBookingTime());
        st.setBoolean(6, booking.getPickupStatus());
        st.setInt(7, booking.getBookingId());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 予約を削除
    public int delete(int bookingId) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "delete from T005_booking where T005_PK1_booking = ?");
        st.setInt(1, bookingId);

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
    public boolean insert(Booking booking) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "insert into T005_booking (T005_FD1_booking, T005_FD2_booking, " +
            "T005_FD3_booking, T005_FD4_booking, T005_FD5_booking, T005_FD6_booking) " +
            "values (?, ?, ?, ?, ?, ?)");

        st.setInt(1, booking.getCount());
        st.setInt(2, booking.getUserId());
        st.setTimestamp(3, booking.getPickupTime());
        st.setInt(4, booking.getProductId());
        st.setTimestamp(5, booking.getBookingTime());
        st.setBoolean(6, booking.getPickupStatus());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line > 0;
    }

}