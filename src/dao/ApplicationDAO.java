package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bean.Application;

public class ApplicationDAO extends DAO {

    /**
     * 申請データを新規登録
     * @return 生成されたapplicationId
     */
    public int insert(Application app) throws Exception {
        Connection con = getConnection();

        String sql = "INSERT INTO T001_1_applications " +
                     "(T001_1_FD1_applications, T001_1_FD2_applications, T001_1_FD3_applications, " +
                     "T001_1_FD4_applications, T001_1_FD5_applications, T001_1_FD6_applications, " +
                     "T001_1_FD7_applications, T001_1_FD8_applications) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pstmt.setString(1, app.getStoreName());           // 店舗名
        pstmt.setString(2, app.getStoreAddress());        // 店舗住所
        pstmt.setString(3, app.getStorePhone());          // 店舗電話番号
        pstmt.setString(4, app.getStoreEmail());          // 店舗メールアドレス
        pstmt.setString(5, app.getPasswordHash());        // パスワードハッシュ
        pstmt.setBytes(6, app.getBusinessLicense());      // 営業許可証
        pstmt.setString(7, app.getApprovalToken());       // 承認トークン
        pstmt.setString(8, "pending");                    // ステータス（初期値: pending）

        int line = pstmt.executeUpdate();

        int generatedId = 0;
        if (line > 0) {
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                app.setApplicationId(generatedId);
            }
            rs.close();
        }

        pstmt.close();
        con.close();
        return generatedId;
    }

    /**
     * 承認トークンで申請データを検索
     */
    public Application selectByToken(String token) throws Exception {
        Connection con = getConnection();

        String sql = "SELECT * FROM T001_1_applications WHERE T001_1_FD7_applications = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, token);

        ResultSet rs = pstmt.executeQuery();

        Application app = null;
        if (rs.next()) {
            app = mapResultSetToApplication(rs);
        }

        rs.close();
        pstmt.close();
        con.close();
        return app;
    }

    /**
     * メールアドレスで申請が存在するか確認
     * （重複申請防止用）
     */
    public boolean existsByEmail(String email) throws Exception {
        Connection con = getConnection();

        String sql = "SELECT COUNT(*) FROM T001_1_applications " +
                     "WHERE T001_1_FD4_applications = ? AND T001_1_FD8_applications IN ('pending', 'approved')";
        PreparedStatement pstmt = con.prepareStatement(sql);
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

    /**
     * 申請のステータスを更新（Applicationオブジェクトを受け取る版）
     */
    public int updateStatus(Application app) throws Exception {
        Connection con = getConnection();

        String sql = "UPDATE T001_1_applications SET " +
                     "T001_1_FD8_applications = ?, " +
                     "T001_1_FD10_applications = ? " +
                     "WHERE T001_1_PK1_applications = ?";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, app.getStatus());
        pstmt.setTimestamp(2, app.getApprovedAt());
        pstmt.setInt(3, app.getApplicationId());

        int line = pstmt.executeUpdate();

        pstmt.close();
        con.close();
        return line;
    }

    /**
     * 申請のステータスを更新（IDとステータス文字列を受け取る版）
     */
    public int updateStatus(int applicationId, String status) throws Exception {
        Connection con = getConnection();

        String sql = "UPDATE T001_1_applications SET " +
                     "T001_1_FD8_applications = ?, " +
                     "T001_1_FD10_applications = ? " +
                     "WHERE T001_1_PK1_applications = ?";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, status);
        pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        pstmt.setInt(3, applicationId);

        int line = pstmt.executeUpdate();

        pstmt.close();
        con.close();
        return line;
    }

    /**
     * 未承認(pending)の申請一覧を取得
     */
    public List<Application> selectPendingApplications() throws Exception {
        List<Application> list = new ArrayList<>();
        Connection con = getConnection();

        String sql = "SELECT * FROM T001_1_applications " +
                     "WHERE T001_1_FD8_applications = 'pending' " +
                     "ORDER BY T001_1_FD9_applications DESC";

        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            list.add(mapResultSetToApplication(rs));
        }

        rs.close();
        pstmt.close();
        con.close();
        return list;
    }

    /**
     * ResultSetからApplicationオブジェクトへのマッピング
     */
    private Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getInt("t001_1_pk1_applications"));
        app.setStoreName(rs.getString("t001_1_fd1_applications"));
        app.setStoreAddress(rs.getString("t001_1_fd2_applications"));
        app.setStorePhone(rs.getString("t001_1_fd3_applications"));
        app.setStoreEmail(rs.getString("t001_1_fd4_applications"));
        app.setPasswordHash(rs.getString("t001_1_fd5_applications"));
        app.setBusinessLicense(rs.getBytes("t001_1_fd6_applications"));
        app.setApprovalToken(rs.getString("t001_1_fd7_applications"));
        app.setStatus(rs.getString("t001_1_fd8_applications"));
        app.setCreatedAt(rs.getTimestamp("t001_1_fd9_applications"));
        app.setApprovedAt(rs.getTimestamp("t001_1_fd10_applications"));
        return app;
    }
}