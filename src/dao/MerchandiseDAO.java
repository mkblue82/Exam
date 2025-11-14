package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Merchandise;
import bean.MerchandiseImage;

public class MerchandiseDAO {

    private Connection connection;

    public MerchandiseDAO(Connection connection) {
        this.connection = connection;
    }

    // 全商品を取得（店舗名も取得）
    public List<Merchandise> selectAll() throws Exception {
        List<Merchandise> list = new ArrayList<>();
        PreparedStatement st = connection.prepareStatement(
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
        return list;
    }

    // 商品IDで検索
    public Merchandise selectById(int merchandiseId) throws Exception {
        PreparedStatement st = connection.prepareStatement(
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
        return m;
    }

    // 店舗IDで商品一覧を取得
    public List<Merchandise> selectByStoreId(int storeId) throws Exception {
        List<Merchandise> list = new ArrayList<>();
        PreparedStatement st = connection.prepareStatement(
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
        return list;
    }

    // 商品を登録
    public int insert(Merchandise merchandise) throws Exception {
        PreparedStatement st = connection.prepareStatement(
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
        return line;
    }

    // 商品と画像を同時に登録（トランザクション）
    public int insertWithImages(Merchandise merchandise, List<MerchandiseImage> images) throws Exception {
        PreparedStatement st = null;
        int merchandiseId = 0;
        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false); // トランザクション開始

            // 商品を登録
            st = connection.prepareStatement(
                "INSERT INTO T002_merchandise (T002_FD1_merchandise, " +
                "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
                "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
                "T002_FD8_merchandise, T002_FD9_merchandise) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            st.setInt(1, merchandise.getStock());
            st.setInt(2, merchandise.getPrice());
            st.setDate(3, merchandise.getUseByDate());
            st.setString(4, merchandise.getMerchandiseTag());
            st.setString(5, merchandise.getMerchandiseName());
            st.setInt(6, merchandise.getEmployeeId());
            st.setTimestamp(7, merchandise.getRegistrationTime());
            st.setInt(8, merchandise.getStoreId());
            st.setBoolean(9, merchandise.getBookingStatus());

            st.executeUpdate();
            st.close();

            // 最新の商品IDを取得
            merchandiseId = getLatestMerchandiseId(merchandise.getStoreId(),
                                                   merchandise.getMerchandiseName());

            // 画像を登録
            if (images != null && !images.isEmpty()) {
                st = connection.prepareStatement(
                    "INSERT INTO t002_1_merchandise_image " +
                    "(t002_1_fd1_merchandise_id, t002_1_fd2_image_data, " +
                    "t002_1_fd3_file_name, t002_1_fd4_display_order) " +
                    "VALUES (?, ?, ?, ?)");

                for (MerchandiseImage image : images) {
                    st.setInt(1, merchandiseId);
                    st.setBytes(2, image.getImageData());
                    st.setString(3, image.getFileName());
                    st.setInt(4, image.getDisplayOrder());
                    st.addBatch();
                }
                st.executeBatch();
            }

            connection.commit(); // コミット
            return merchandiseId;

        } catch (Exception e) {
            connection.rollback(); // ロールバック
            throw e;
        } finally {
            if (st != null) st.close();
            connection.setAutoCommit(originalAutoCommit); // 元の状態に戻す
        }
    }

    // 商品と画像を同時に更新（トランザクション）
    public int updateWithImages(Merchandise merchandise, List<MerchandiseImage> images,
                                boolean deleteExistingImages) throws Exception {
        PreparedStatement st = null;
        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false); // トランザクション開始

            // 商品を更新
            st = connection.prepareStatement(
                "UPDATE T002_merchandise SET T002_FD1_merchandise = ?, T002_FD2_merchandise = ?, " +
                "T002_FD3_merchandise = ?, T002_FD4_merchandise = ?, T002_FD5_merchandise = ?, " +
                "T002_FD6_merchandise = ?, T002_FD7_merchandise = ?, T002_FD8_merchandise = ?, " +
                "T002_FD9_merchandise = ? WHERE T002_PK1_merchandise = ?");

            st.setInt(1, merchandise.getStock());
            st.setInt(2, merchandise.getPrice());
            st.setDate(3, merchandise.getUseByDate());
            st.setString(4, merchandise.getMerchandiseTag());
            st.setString(5, merchandise.getMerchandiseName());
            st.setInt(6, merchandise.getEmployeeId());
            st.setTimestamp(7, merchandise.getRegistrationTime());
            st.setInt(8, merchandise.getStoreId());
            st.setBoolean(9, merchandise.getBookingStatus());
            st.setInt(10, merchandise.getMerchandiseId());

            int line = st.executeUpdate();
            st.close();

            // 既存の画像を削除（オプション）
            if (deleteExistingImages) {
                st = connection.prepareStatement(
                    "DELETE FROM t002_1_merchandise_image WHERE t002_1_fd1_merchandise_id = ?");
                st.setInt(1, merchandise.getMerchandiseId());
                st.executeUpdate();
                st.close();
            }

            // 新しい画像を登録
            if (images != null && !images.isEmpty()) {
                st = connection.prepareStatement(
                    "INSERT INTO t002_1_merchandise_image " +
                    "(t002_1_fd1_merchandise_id, t002_1_fd2_image_data, " +
                    "t002_1_fd3_file_name, t002_1_fd4_display_order) " +
                    "VALUES (?, ?, ?, ?)");

                for (MerchandiseImage image : images) {
                    st.setInt(1, merchandise.getMerchandiseId());
                    st.setBytes(2, image.getImageData());
                    st.setString(3, image.getFileName());
                    st.setInt(4, image.getDisplayOrder());
                    st.addBatch();
                }
                st.executeBatch();
            }

            connection.commit(); // コミット
            return line;

        } catch (Exception e) {
            connection.rollback(); // ロールバック
            throw e;
        } finally {
            if (st != null) st.close();
            connection.setAutoCommit(originalAutoCommit); // 元の状態に戻す
        }
    }

    // 最新の商品IDを取得（店舗IDと商品名から）
    public int getLatestMerchandiseId(int storeId, String merchandiseName) throws Exception {
        PreparedStatement st = connection.prepareStatement(
            "SELECT T002_PK1_merchandise FROM T002_merchandise " +
            "WHERE T002_FD8_merchandise = ? AND T002_FD5_merchandise = ? " +
            "ORDER BY T002_FD7_merchandise DESC LIMIT 1");
        st.setInt(1, storeId);
        st.setString(2, merchandiseName);
        ResultSet rs = st.executeQuery();

        int id = 0;
        if (rs.next()) {
            id = rs.getInt("T002_PK1_merchandise");
        }

        rs.close();
        st.close();
        return id;
    }

    // 商品を更新
    public int update(Merchandise merchandise) throws Exception {
        PreparedStatement st = connection.prepareStatement(
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
        st.setInt(10, merchandise.getMerchandiseId());

        int line = st.executeUpdate();
        st.close();
        return line;
    }

    // 商品を削除
    public int delete(int merchandiseId) throws Exception {
        PreparedStatement st = connection.prepareStatement(
            "delete from T002_merchandise where T002_PK1_merchandise = ?");
        st.setInt(1, merchandiseId);

        int line = st.executeUpdate();
        st.close();
        return line;
    }

    // 予約ステータスがtrueの商品のみを取得
    public List<Merchandise> selectByBookingStatus(boolean bookingStatus) throws Exception {
        List<Merchandise> list = new ArrayList<>();
        PreparedStatement st = connection.prepareStatement(
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
        return list;
    }

    // 同一店舗内で同じ商品名が存在するか確認
    public boolean isDuplicateMerchandise(int storeId, String merchandiseName) throws Exception {
        PreparedStatement st = connection.prepareStatement(
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
        return exists;
    }
}
