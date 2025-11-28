package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Booking;

public class BookingDAO extends DAO {

    // ★ 共有コネクション（DeleteActionが使う）
    private Connection sharedConnection = null;

    // ★ デフォルトコンストラクタ（元の挙動）
    public BookingDAO() {}

    // ★ 共有コネクションを使う場合（MerchandiseDeleteAction 用）
    public BookingDAO(Connection con) {
        this.sharedConnection = con;
    }

    // =========================================================
    // ★ コネクション取得（共有 > 新規）
    // =========================================================
    private Connection getEffectiveConnection() throws Exception {
        if (sharedConnection != null) {
            return sharedConnection; // 共有を優先
        }
        return getConnection();      // 従来の動作（自動で接続）
    }

    // =========================================================
    // ★ 自動クローズ（共有のときは閉じない）
    // =========================================================
    private void closeIfLocal(Connection con) throws Exception {
        if (sharedConnection == null && con != null) {
            con.close();
        }
    }

    // =========================================================
    // 全予約を取得（元のまま改善）
    // =========================================================
    public List<Booking> selectAll() throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getEffectiveConnection();

        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking " +
            "from T005_booking order by T005_PK1_booking"
        );

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

        rs.close();
        st.close();
        closeIfLocal(con);
        return list;
    }

    // =========================================================
    // 商品IDに紐づく予約件数（削除判定に使う）
    // =========================================================
    public int countByMerchandiseId(int merchandiseId) throws Exception {
        Connection con = getEffectiveConnection();

        PreparedStatement st = con.prepareStatement(
            "SELECT COUNT(*) FROM T005_booking WHERE T005_FD4_booking = ?"
        );
        st.setInt(1, merchandiseId);

        ResultSet rs = st.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }

        rs.close();
        st.close();
        closeIfLocal(con);

        return count;
    }

    // =========================================================
    // 予約IDで検索
    // =========================================================
    public Booking selectById(int bookingId) throws Exception {
        Connection con = getEffectiveConnection();

        PreparedStatement st = con.prepareStatement(
            "select T005_PK1_booking, T005_FD1_booking, " +
            "T005_FD2_booking, T005_FD3_booking, T005_FD4_booking, " +
            "T005_FD5_booking, T005_FD6_booking, T005_FD7_booking " +
            "from T005_booking where T005_PK1_booking = ?"
        );
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
            b.setAmount(rs.getInt("T005_FD7_booking"));
        }

        rs.close();
        st.close();
        closeIfLocal(con);
        return b;
    }

    // =========================================================
    // ユーザーIDで予約一覧
    // =========================================================
    public List<Booking> selectByUserId(int userId) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getEffectiveConnection();

        PreparedStatement st = con.prepareStatement(
            "select * from T005_booking where T005_FD2_booking = ? order by T005_PK1_booking"
        );
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

        rs.close();
        st.close();
        closeIfLocal(con);
        return list;
    }

    // =========================================================
    // 商品IDで予約一覧
    // =========================================================
    public List<Booking> selectByProductId(int productId) throws Exception {
        List<Booking> list = new ArrayList<>();
        Connection con = getEffectiveConnection();

        PreparedStatement st = con.prepareStatement(
            "select * from T005_booking where T005_FD4_booking = ? order by T005_PK1_booking"
        );
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

        rs.close();
        st.close();
        closeIfLocal(con);
        return list;
    }

    // =========================================================
    // 予約INSERT（元のまま）
    // =========================================================
    public boolean insert(Booking booking) throws Exception {
        Connection con = getEffectiveConnection();
        PreparedStatement st = null;

        try {
            String sql = "INSERT INTO T005_booking "
                       + "(T005_FD1_booking, T005_FD2_booking, T005_FD3_booking, "
                       + "T005_FD4_booking, T005_FD5_booking, T005_FD6_booking, T005_FD7_booking) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            st = con.prepareStatement(sql);
            st.setInt(1, booking.getCount());
            st.setInt(2, booking.getUserId());
            st.setTimestamp(3, booking.getPickupTime());
            st.setInt(4, booking.getProductId());
            st.setTimestamp(5, booking.getBookingTime());
            st.setBoolean(6, booking.getPickupStatus());
            st.setInt(7, booking.getAmount());  // 金額

            int result = st.executeUpdate();

            st.close();
            closeIfLocal(con);

            return result > 0;

        } catch (Exception e) {
            if (st != null) st.close();
            closeIfLocal(con);
            throw e;
        }
    }

	 // =========================================================
	 // 予約を更新（共有コネクション対応）
	 // =========================================================
	 public int update(Booking booking) throws Exception {
	     Connection con = getEffectiveConnection();

	     PreparedStatement st = con.prepareStatement(
	         "UPDATE T005_booking SET " +
	         "T005_FD1_booking = ?, " +
	         "T005_FD2_booking = ?, " +
	         "T005_FD3_booking = ?, " +
	         "T005_FD4_booking = ?, " +
	         "T005_FD5_booking = ?, " +
	         "T005_FD6_booking = ? " +
	         "WHERE T005_PK1_booking = ?"
	     );

	     st.setInt(1, booking.getCount());
	     st.setInt(2, booking.getUserId());
	     st.setTimestamp(3, booking.getPickupTime());
	     st.setInt(4, booking.getProductId());
	     st.setTimestamp(5, booking.getBookingTime());
	     st.setBoolean(6, booking.getPickupStatus());
	     st.setInt(7, booking.getBookingId());

	     int line = st.executeUpdate();
	     st.close();
	     closeIfLocal(con);

	     return line;
	 }

	//=========================================================
	//予約を削除（共有コネクション対応）
	//=========================================================
	public int delete(int bookingId) throws Exception {
	  Connection con = getEffectiveConnection();

	  PreparedStatement st = con.prepareStatement(
	      "DELETE FROM T005_booking WHERE T005_PK1_booking = ?"
	  );
	  st.setInt(1, bookingId);

	  int line = st.executeUpdate();
	  st.close();
	  closeIfLocal(con);

	  return line;
	}

	// =========================================================
	// 店舗IDに紐づく予約一覧を取得（商品名JOIN付き）
	// =========================================================
	public List<Booking> selectByStoreId(int storeId) throws Exception {
	    List<Booking> list = new ArrayList<>();
	    Connection con = getEffectiveConnection();

	    PreparedStatement st = con.prepareStatement(
	        "SELECT b.T005_PK1_booking, b.T005_FD1_booking, b.T005_FD2_booking, " +
	        "b.T005_FD3_booking, b.T005_FD4_booking, b.T005_FD5_booking, b.T005_FD6_booking, " +
	        "b.T005_FD7_booking, " +  // ← 金額を追加
	        "m.T002_FD5_merchandise AS merchandiseName " +
	        "FROM T005_booking b " +
	        "JOIN T002_merchandise m " +
	        "ON b.T005_FD4_booking = m.T002_PK1_merchandise " +
	        "WHERE m.T002_FD8_merchandise = ? " +
	        "ORDER BY b.T005_PK1_booking"
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
	        b.setAmount(rs.getInt("T005_FD7_booking"));  // ← 金額をセット

	        // ★ 商品名セット
	        b.setMerchandiseName(rs.getString("merchandiseName"));

	        list.add(b);
	    }

	    rs.close();
	    st.close();
	    closeIfLocal(con);

	    return list;
	}
}
