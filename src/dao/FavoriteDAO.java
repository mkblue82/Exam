package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Favorite;

public class FavoriteDAO {

    private Connection con;

    public FavoriteDAO(Connection con) {
        this.con = con;
    }

    public List<String> getNotificationEnabledEmails(int storeId) throws SQLException {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT u.t004_fd2_user " +
                     "FROM T006_favorite f " +
                     "INNER JOIN t004_user u ON f.T006_FD1_favorite = u.t004_pk1_user " +
                     "WHERE f.T006_FD2_favorite = ? " +
                     "AND f.T006_FD3_favorite = true " +
                     "AND u.t004_fd2_user IS NOT NULL";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("t004_fd2_user");
                    if (email != null && !email.trim().isEmpty()) {
                        emails.add(email);
                    }
                }
            }
        }
        return emails;
    }

    // お気に入りに追加
    public void addFavorite(int userId, int storeId) throws SQLException {
        String sql = "INSERT INTO T006_favorite (T006_FD1_favorite, T006_FD2_favorite, T006_FD3_favorite) "
                   + "VALUES (?, ?, ?) "
                   + "ON CONFLICT (T006_FD1_favorite, T006_FD2_favorite) DO NOTHING";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            pstmt.setBoolean(3, false); // 初期値はfalse（通知OFF）
            pstmt.executeUpdate();
        }
    }

    // お気に入りから削除
    public void removeFavorite(int userId, int storeId) throws SQLException {
        String sql = "DELETE FROM T006_favorite "
                   + "WHERE T006_FD1_favorite = ? AND T006_FD2_favorite = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            pstmt.executeUpdate();
        }
    }

    // お気に入り登録済みかチェック
    public boolean isFavorite(int userId, int storeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM T006_favorite "
                   + "WHERE T006_FD1_favorite = ? AND T006_FD2_favorite = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ユーザーのお気に入り店舗一覧を取得（店舗情報込み）
    public List<Favorite> getFavoriteStoresByUserId(int userId) throws SQLException {
        List<Favorite> list = new ArrayList<>();
        String sql = "SELECT f.T006_PK1_favorite, f.T006_FD1_favorite, f.T006_FD2_favorite, "
                   + "f.T006_FD3_favorite, s.T001_FD1_store, s.T001_FD2_store "
                   + "FROM T006_favorite f "
                   + "INNER JOIN t001_store s ON f.T006_FD2_favorite = s.T001_PK1_store "
                   + "WHERE f.T006_FD1_favorite = ? "
                   + "ORDER BY f.T006_PK1_favorite";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Favorite favorite = new Favorite();
                    favorite.setFavoriteStoreId(rs.getInt("T006_PK1_favorite"));
                    favorite.setUserId(rs.getInt("T006_FD1_favorite"));
                    favorite.setStoreId(rs.getInt("T006_FD2_favorite"));
                    favorite.setNotificationSetting(rs.getBoolean("T006_FD3_favorite"));
                    favorite.setStoreName(rs.getString("T001_FD1_store"));
                    favorite.setStoreAddress(rs.getString("T001_FD2_store"));
                    list.add(favorite);
                }
            }
        }
        return list;
    }

    // 通知設定を更新
    public void updateNotification(int userId, int storeId, boolean notificationEnabled) throws SQLException {
        String sql = "UPDATE T006_favorite "
                   + "SET T006_FD3_favorite = ? "
                   + "WHERE T006_FD1_favorite = ? AND T006_FD2_favorite = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setBoolean(1, notificationEnabled);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, storeId);
            pstmt.executeUpdate();
        }
    }

    // 複数店舗の通知設定を一括更新
    public void updateNotifications(int userId, List<Integer> storeIds, List<Boolean> notifications) throws SQLException {
        String sql = "UPDATE T006_favorite "
                   + "SET T006_FD3_favorite = ? "
                   + "WHERE T006_FD1_favorite = ? AND T006_FD2_favorite = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (int i = 0; i < storeIds.size(); i++) {
                pstmt.setBoolean(1, notifications.get(i));
                pstmt.setInt(2, userId);
                pstmt.setInt(3, storeIds.get(i));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // 特定のお気に入り情報を取得
    public Favorite getFavorite(int userId, int storeId) throws SQLException {
        String sql = "SELECT f.T006_PK1_favorite, f.T006_FD1_favorite, f.T006_FD2_favorite, "
                   + "f.T006_FD3_favorite, s.T001_FD1_store, s.T001_FD2_store "
                   + "FROM T006_favorite f "
                   + "INNER JOIN t001_store s ON f.T006_FD2_favorite = s.T001_PK1_store "
                   + "WHERE f.T006_FD1_favorite = ? AND f.T006_FD2_favorite = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Favorite favorite = new Favorite();
                    favorite.setFavoriteStoreId(rs.getInt("T006_PK1_favorite"));
                    favorite.setUserId(rs.getInt("T006_FD1_favorite"));
                    favorite.setStoreId(rs.getInt("T006_FD2_favorite"));
                    favorite.setNotificationSetting(rs.getBoolean("T006_FD3_favorite"));
                    favorite.setStoreName(rs.getString("T001_FD1_store"));
                    favorite.setStoreAddress(rs.getString("T001_FD2_store"));
                    return favorite;
                }
            }
        }
        return null;
    }
}