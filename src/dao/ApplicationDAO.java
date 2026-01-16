package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bean.Application;

public class ApplicationDAO extends DAO {

    private Connection con;

    public ApplicationDAO(Connection con) {
        this.con = con;
    }

    // ===== 申請データ新規登録 =====
    public int insert(Application app) throws Exception {

        String sql =
            "INSERT INTO T001_1_applications (" +
            "T001_1_FD1_applications, T001_1_FD2_applications, T001_1_FD3_applications, " +
            "T001_1_FD4_applications, T001_1_FD5_applications, T001_1_FD6_applications, " +
            "T001_1_FD7_applications, T001_1_FD8_applications) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt =
            con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pstmt.setString(1, app.getStoreName());
        pstmt.setString(2, app.getStoreAddress());
        pstmt.setString(3, app.getStorePhone());
        pstmt.setString(4, app.getStoreEmail());
        pstmt.setString(5, app.getPasswordHash());
        pstmt.setString(6, app.getBusinessLicense());
        pstmt.setString(7, app.getApprovalToken());
        pstmt.setString(8, "pending");

        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            app.setApplicationId(rs.getInt(1));
        }

        rs.close();
        pstmt.close();
        return app.getApplicationId();
    }

    // ===== 承認トークン検索（pendingのみ） =====
    public Application selectByToken(String token) throws Exception {

        String sql =
            "SELECT * FROM T001_1_applications " +
            "WHERE T001_1_FD7_applications = ? " +
            "AND T001_1_FD8_applications = 'pending'";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, token);

        ResultSet rs = pstmt.executeQuery();

        Application app = null;
        if (rs.next()) {
            app = map(rs);
        }

        rs.close();
        pstmt.close();
        return app;
    }

    // ===== 承認処理（★修正済み） =====
    public void approve(int applicationId) throws Exception {

        String sql =
            "UPDATE T001_1_applications SET " +
            "T001_1_FD8_applications = 'approved', " +
            "T001_1_FD10_applications = CURRENT_TIMESTAMP " +
            "WHERE T001_1_PK1_applications = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, applicationId);
            pstmt.executeUpdate();
        }
    }

    // ===== pending一覧取得 =====
    public List<Application> selectPendingApplications() throws Exception {

        List<Application> list = new ArrayList<>();

        String sql =
            "SELECT * FROM T001_1_applications " +
            "WHERE T001_1_FD8_applications = 'pending' " +
            "ORDER BY T001_1_FD9_applications DESC";

        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            list.add(map(rs));
        }

        rs.close();
        pstmt.close();
        return list;
    }

    // ===== メール重複チェック（pendingのみ） =====
    public boolean existsByEmail(String email) throws SQLException {

        String sql =
            "SELECT 1 FROM T001_1_applications " +
            "WHERE T001_1_FD4_applications = ? " +
            "AND T001_1_FD8_applications = 'pending'";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== 電話番号重複チェック（pendingのみ） =====
    public boolean existsByPhone(String phone) throws SQLException {

        String sql =
            "SELECT 1 FROM T001_1_applications " +
            "WHERE T001_1_FD3_applications = ? " +
            "AND T001_1_FD8_applications = 'pending'";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, phone);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===== ResultSet → Application =====
    private Application map(ResultSet rs) throws SQLException {

        Application app = new Application();
        app.setApplicationId(rs.getInt("t001_1_pk1_applications"));
        app.setStoreName(rs.getString("t001_1_fd1_applications"));
        app.setStoreAddress(rs.getString("t001_1_fd2_applications"));
        app.setStorePhone(rs.getString("t001_1_fd3_applications"));
        app.setStoreEmail(rs.getString("t001_1_fd4_applications"));
        app.setPasswordHash(rs.getString("t001_1_fd5_applications"));
        app.setBusinessLicense(rs.getString("t001_1_fd6_applications"));
        app.setApprovalToken(rs.getString("t001_1_fd7_applications"));
        app.setStatus(rs.getString("t001_1_fd8_applications"));
        app.setCreatedAt(rs.getTimestamp("t001_1_fd9_applications"));
        app.setApprovedAt(rs.getTimestamp("t001_1_fd10_applications"));
        return app;
    }
}
