package tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


// 直す時は一言ください！！！！！
public class DBManager {

    private static final String URL = "jdbc:postgresql://ep-rapid-surf-a100ysmf-pooler.ap-southeast-1.aws.neon.tech/neondb"; // DB名に変更OK
    private static final String USER = "neondb_owner";  // あなたのユーザー名
    private static final String PASSWORD = "npg_fpWk07TEejNO"; // あなたのパスワード

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
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
