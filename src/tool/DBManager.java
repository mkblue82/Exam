package tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // DB名に変更OK
    private static final String USER = "postgres";  // あなたのユーザー名
    private static final String PASSWORD = "20060318"; // あなたのパスワード

    // JDBCドライバの読み込み
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("PostgreSQL JDBC Driver が見つかりません。", e);
        }
    }

    // データベース接続を返す
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
