package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.MerchandiseImage;

public class MerchandiseImageDAO extends DAO {

    private Connection connection;

    public MerchandiseImageDAO() throws Exception {
        super();
    }

    public MerchandiseImageDAO(Connection con) {
        this.connection = con;
    }

    // 画像を登録（画像データなし - ファイル名のみ）
    public int insert(MerchandiseImage image) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "INSERT INTO t002_1_merchandise_image " +
            "(t002_1_fd1_merchandise_id, t002_1_fd3_file_name, t002_1_fd4_display_order) " +
            "VALUES (?, ?, ?)");

        st.setInt(1, image.getMerchandiseId());
        st.setString(2, image.getFileName());
        st.setInt(3, image.getDisplayOrder());

        int line = st.executeUpdate();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return line;
    }

    // 商品IDに紐づく画像を全て取得
    public List<MerchandiseImage> selectByMerchandiseId(int merchandiseId) throws Exception {
        List<MerchandiseImage> list = new ArrayList<>();
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "SELECT t002_1_pk1_image, t002_1_fd1_merchandise_id, " +
            "t002_1_fd3_file_name, t002_1_fd4_display_order, t002_1_fd5_uploaded_at " +
            "FROM t002_1_merchandise_image " +
            "WHERE t002_1_fd1_merchandise_id = ? " +
            "ORDER BY t002_1_fd4_display_order, t002_1_pk1_image");
        st.setInt(1, merchandiseId);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            MerchandiseImage img = new MerchandiseImage();
            img.setImageId(rs.getInt("t002_1_pk1_image"));
            img.setMerchandiseId(rs.getInt("t002_1_fd1_merchandise_id"));
            img.setFileName(rs.getString("t002_1_fd3_file_name"));
            img.setDisplayOrder(rs.getInt("t002_1_fd4_display_order"));
            img.setUploadedAt(rs.getTimestamp("t002_1_fd5_uploaded_at"));
            list.add(img);
        }

        rs.close();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return list;
    }

    // 画像IDで取得
    public MerchandiseImage selectById(int imageId) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "SELECT t002_1_pk1_image, t002_1_fd1_merchandise_id, " +
            "t002_1_fd3_file_name, t002_1_fd4_display_order, t002_1_fd5_uploaded_at " +
            "FROM t002_1_merchandise_image " +
            "WHERE t002_1_pk1_image = ?");
        st.setInt(1, imageId);
        ResultSet rs = st.executeQuery();

        MerchandiseImage img = null;
        if (rs.next()) {
            img = new MerchandiseImage();
            img.setImageId(rs.getInt("t002_1_pk1_image"));
            img.setMerchandiseId(rs.getInt("t002_1_fd1_merchandise_id"));
            img.setFileName(rs.getString("t002_1_fd3_file_name"));
            img.setDisplayOrder(rs.getInt("t002_1_fd4_display_order"));
            img.setUploadedAt(rs.getTimestamp("t002_1_fd5_uploaded_at"));
        }

        rs.close();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return img;
    }

    // 複数画像を一括登録
    public int[] insertBatch(List<MerchandiseImage> images) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "INSERT INTO t002_1_merchandise_image " +
            "(t002_1_fd1_merchandise_id, t002_1_fd3_file_name, t002_1_fd4_display_order) " +
            "VALUES (?, ?, ?)");

        for (MerchandiseImage image : images) {
            st.setInt(1, image.getMerchandiseId());
            st.setString(2, image.getFileName());
            st.setInt(3, image.getDisplayOrder());
            st.addBatch();
        }

        int[] lines = st.executeBatch();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return lines;
    }

    // 画像を更新
    public int update(MerchandiseImage image) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "UPDATE t002_1_merchandise_image SET " +
            "t002_1_fd3_file_name = ?, t002_1_fd4_display_order = ? " +
            "WHERE t002_1_pk1_image = ?");

        st.setString(1, image.getFileName());
        st.setInt(2, image.getDisplayOrder());
        st.setInt(3, image.getImageId());

        int line = st.executeUpdate();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return line;
    }

    // 表示順のみ更新
    public int updateDisplayOrder(int imageId, int displayOrder) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "UPDATE t002_1_merchandise_image SET t002_1_fd4_display_order = ? " +
            "WHERE t002_1_pk1_image = ?");

        st.setInt(1, displayOrder);
        st.setInt(2, imageId);

        int line = st.executeUpdate();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return line;
    }

    // 画像を削除
    public int delete(int imageId) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "DELETE FROM t002_1_merchandise_image WHERE t002_1_pk1_image = ?");
        st.setInt(1, imageId);

        int line = st.executeUpdate();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return line;
    }

    // 商品IDに紐づく全画像を削除
    public int deleteByMerchandiseId(int merchandiseId) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "DELETE FROM t002_1_merchandise_image WHERE t002_1_fd1_merchandise_id = ?");
        st.setInt(1, merchandiseId);

        int line = st.executeUpdate();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return line;
    }

    // 商品の画像数を取得
    public int countByMerchandiseId(int merchandiseId) throws Exception {
        Connection con = (connection != null) ? connection : getConnection();
        boolean shouldClose = (connection == null);

        PreparedStatement st = con.prepareStatement(
            "SELECT COUNT(*) FROM t002_1_merchandise_image " +
            "WHERE t002_1_fd1_merchandise_id = ?");
        st.setInt(1, merchandiseId);
        ResultSet rs = st.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }

        rs.close();
        st.close();

        if (shouldClose) {
            con.close();
        }

        return count;
    }
}