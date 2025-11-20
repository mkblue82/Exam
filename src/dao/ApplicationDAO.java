package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bean.Application;

public class ApplicationDAO extends DAO {

    private Connection con;

    public ApplicationDAO(Connection con) {
        this.con = con;
    }

    /**
     * 申請データを新規登録
     */
    public void insert(Application app) throws SQLException {
        String sql = "INSERT INTO T001_1_applications " +
                     "(T001_1_FD1_applications, T001_1_FD2_applications, T001_1_FD3_applications, " +
                     "T001_1_FD4_applications, T001_1_FD5_applications, T001_1_FD6_applications, " +
                     "T001_1_FD7_applications, T001_1_FD8_applications) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING T001_1_PK1_applications";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, app.getStoreName());           // 店舗名
            pstmt.setString(2, app.getStoreAddress());        // 店舗住所
            pstmt.setString(3, app.getStorePhone());          // 店舗電話番号
            pstmt.setString(4, app.getStoreEmail());          // 店舗メールアドレス
            pstmt.setString(5, app.getPasswordHash());        // パスワードハッシュ
            pstmt.setBytes(6, app.getBusinessLicense());      // 営業許可証
            pstmt.setString(7, app.getApprovalToken());       // 承認トークン
            pstmt.setString(8, "pending");                    // ステータス（初期値: pending）

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    app.setApplicationId(rs.getInt(1));       // 生成されたPKをセット
                }
            }
        }
    }

    /**
     * 承認トークンで申請データを検索
     */
    public Application selectByToken(String token) throws SQLException {
        String sql = "SELECT * FROM T001_1_applications WHERE T001_1_FD7_applications = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, token);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToApplication(rs);
                }
            }
        }
        return null;
    }

    /**
     * 申請のステータスを更新
     */
    public void updateStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE T001_1_applications SET " +
                     "T001_1_FD8_applications = ?, " +
                     "T001_1_FD10_applications = ? " +
                     "WHERE T001_1_PK1_applications = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // 承認日時
            pstmt.setInt(3, applicationId);
            pstmt.executeUpdate();
        }
    }

    /**
     * 未承認(pending)の申請一覧を取得
     */
    public List<Application> selectPendingApplications() throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM T001_1_applications WHERE T001_1_FD8_applications = 'pending'";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
        }
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
