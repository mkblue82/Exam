package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.User;

public class UserDAO {

    private Connection con;

    public UserDAO(Connection con) {
        this.con = con;
    }

    // ユーザー登録
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO t004_user (t004_fd1_user, t004_fd2_user, t004_fd3_user, t004_fd4_user, t004_fd5_user, t004_fd6_user, t004_fd7_user) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING t004_pk1_user";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getFavoriteStore());

            // storeIdが0の場合はNULLを設定
            if (user.getStoreId() == 0) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, user.getStoreId());
            }

            pstmt.setBoolean(7, user.isNotification());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    user.setUserId(rs.getInt(1));
            	}
            }
        }
    }

    // 全件取得
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM t004_user ORDER BY t004_pk1_user";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("t004_pk1_user"));
                user.setName(rs.getString("t004_fd1_user"));
                user.setEmail(rs.getString("t004_fd2_user"));
                user.setPhone(rs.getString("t004_fd3_user"));
                user.setPassword(rs.getString("t004_fd4_user"));
                user.setFavoriteStore(rs.getString("t004_fd5_user"));

                // NULLチェックを追加
                Integer storeId = (Integer) rs.getObject("t004_fd6_user");
                user.setStoreId(storeId != null ? storeId : 0);

                user.setNotification(rs.getBoolean("t004_fd7_user"));
                list.add(user);
            }
        }
        return list;
    }

    // IDで検索
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM t004_user WHERE t004_pk1_user = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        	pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("t004_pk1_user"));
                user.setName(rs.getString("t004_fd1_user"));
                user.setEmail(rs.getString("t004_fd2_user"));
                user.setPhone(rs.getString("t004_fd3_user"));
                user.setPassword(rs.getString("t004_fd4_user"));
                user.setFavoriteStore(rs.getString("t004_fd5_user"));

                // NULLチェックを追加
                Integer storeId = (Integer) rs.getObject("t004_fd6_user");
                user.setStoreId(storeId != null ? storeId : 0);

                user.setNotification(rs.getBoolean("t004_fd7_user"));
                return user;
            }
        }
        return null;
    }

    // 更新
    public void update(User user) throws SQLException {
        String sql = "UPDATE t004_user SET t004_fd1_user=?, t004_fd2_user=?, t004_fd3_user=?, t004_fd4_user=?, "
                   + "t004_fd5_user=?, t004_fd6_user=?, t004_fd7_user=? WHERE t004_pk1_user=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getFavoriteStore());

            // storeIdが0の場合はNULLを設定
            if (user.getStoreId() == 0) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, user.getStoreId());
            }

            pstmt.setBoolean(7, user.isNotification());
            pstmt.setInt(8, user.getUserId());
            pstmt.executeUpdate();
        }
    }

    // 削除
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM t004_user WHERE t004_pk1_user=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // ログイン
    public User login(String email, String password) throws Exception {
        String sql = "SELECT * FROM t004_user WHERE t004_fd2_user = ? AND t004_fd4_user = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            System.out.println("DEBUG: Searching for email=" + email + ", password=" + password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: User found!");
                    User user = new User();
                    user.setUserId(rs.getInt("t004_pk1_user"));
                    user.setName(rs.getString("t004_fd1_user"));
                    user.setEmail(rs.getString("t004_fd2_user"));
                    user.setPhone(rs.getString("t004_fd3_user"));
                    user.setPassword(rs.getString("t004_fd4_user"));
                    user.setFavoriteStore(rs.getString("t004_fd5_user"));

                    // NULLチェックを追加
                    Integer storeId = (Integer) rs.getObject("t004_fd6_user");
                    user.setStoreId(storeId != null ? storeId : 0);

                    user.setNotification(rs.getBoolean("t004_fd7_user"));
                    return user;
                } else {
                    System.out.println("DEBUG: No user found for email=" + email);
                }
            }
        }
        return null;
    }
}