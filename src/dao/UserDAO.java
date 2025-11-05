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
        String sql = "INSERT INTO T004_user (氏名, メールアドレス, 電話番号, パスワード, お気に入り店舗, 店舗ID, 通知ON_OFF) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getFavoriteStore());
            pstmt.setInt(6, user.getStoreId());
            pstmt.setBoolean(7, user.isNotification());
            pstmt.executeUpdate();
        }
    }

    // 全件取得
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM T004_user ORDER BY ユーザー_ID";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("ユーザー_ID"));
                user.setName(rs.getString("氏名"));
                user.setEmail(rs.getString("メールアドレス"));
                user.setPhone(rs.getString("電話番号"));
                user.setPassword(rs.getString("パスワード"));
                user.setFavoriteStore(rs.getString("お気に入り店舗"));
                user.setStoreId(rs.getInt("店舗ID"));
                user.setNotification(rs.getBoolean("通知ON_OFF"));
                list.add(user);
            }
        }
        return list;
    }

    // IDで検索
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM T004_user WHERE ユーザー_ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("ユーザー_ID"));
                user.setName(rs.getString("氏名"));
                user.setEmail(rs.getString("メールアドレス"));
                user.setPhone(rs.getString("電話番号"));
                user.setPassword(rs.getString("パスワード"));
                user.setFavoriteStore(rs.getString("お気に入り店舗"));
                user.setStoreId(rs.getInt("店舗ID"));
                user.setNotification(rs.getBoolean("通知ON_OFF"));
                return user;
            }
        }
        return null;
    }

    // 更新
    public void update(User user) throws SQLException {
        String sql = "UPDATE T004_user SET 氏名=?, メールアドレス=?, 電話番号=?, パスワード=?, "
                   + "お気に入り店舗=?, 店舗ID=?, 通知ON_OFF=? WHERE ユーザー_ID=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getFavoriteStore());
            pstmt.setInt(6, user.getStoreId());
            pstmt.setBoolean(7, user.isNotification());
            pstmt.setInt(8, user.getUserId());
            pstmt.executeUpdate();
        }
    }

    // 削除
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM T004_user WHERE ユーザー_ID=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
