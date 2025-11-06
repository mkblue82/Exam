package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
public class test1 {

    public static void main(String[] args) {


    	String url = "jdbc:postgresql://ep-rapid-surf-a100ysmf-pooler.ap-southeast-1.aws.neon.tech/neondb";
    	String user = "neondb_owner";
    	String password = "npg_fpWk07TEejNO";
        try {
            // PostgreSQLドライバのロード
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            // T001_storeテーブル作成（自動採番ID追加）
            String createStoreTable = "CREATE TABLE IF NOT EXISTS T001_store (" +
                "T001_PK1_store SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T001_FD1_store VARCHAR(30) NOT NULL, " +
                "T001_FD2_store VARCHAR(100) NOT NULL, " +
                "T001_FD3_store VARCHAR(12) NOT NULL, " +
                "T001_FD4_store VARCHAR(30) NOT NULL, " +
                "T001_FD5_store TIME, " +
                "T001_FD6_store CHAR(10), " +
                "T001_FD7_store VARCHAR(100), " +  // 店舗連絡用メールアドレス
                "T001_FD8_store BYTEA, " +  // 営業許可証（画像/PDF）
                "T001_FD9_store VARCHAR(100))";  // 営業許可証ファイル名
            stmt.execute(createStoreTable);
            System.out.println("✓ T001_storeテーブル作成完了");
            // T002_merchandiseテーブル作成（自動採番ID追加、外部キー: 店舗ID）
            String createMerchandiseTable = "CREATE TABLE IF NOT EXISTS T002_merchandise (" +
                "T002_PK1_merchandise SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T002_FD1_merchandise CHAR(5) NOT NULL DEFAULT '0', " +
                "T002_FD2_merchandise CHAR(10) NOT NULL DEFAULT '0', " +
                "T002_FD3_merchandise DATE NOT NULL, " +
                "T002_FD4_merchandise VARCHAR(40) NOT NULL, " +
                "T002_FD5_merchandise VARCHAR(50) NOT NULL, " +
                "T002_FD6_merchandise CHAR(50), " +
                "T002_FD7_merchandise TIMESTAMP NOT NULL, " +
                "T002_FD8_merchandise INTEGER NOT NULL, " +
                "T002_FD9_merchandise BOOLEAN NOT NULL DEFAULT FALSE, " +
                "FOREIGN KEY (T002_FD8_merchandise) REFERENCES T001_store(T001_PK1_store))";
            stmt.execute(createMerchandiseTable);
            System.out.println("✓ T002_merchandiseテーブル作成完了");

            // T002_1_merchandise_imageテーブル作成（商品画像専用テーブル）
            String createMerchandiseImageTable = "CREATE TABLE IF NOT EXISTS T002_1_merchandise_image (" +
                "T002_1_PK1_image SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T002_1_FD1_merchandise_id INTEGER NOT NULL, " +  // 商品ID
                "T002_1_FD2_image_data BYTEA NOT NULL, " +  // 画像データ
                "T002_1_FD3_file_name VARCHAR(100) NOT NULL, " +  // ファイル名
                "T002_1_FD4_display_order INTEGER DEFAULT 0, " +  // 表示順序
                "T002_1_FD5_uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +  // アップロード日時
                "FOREIGN KEY (T002_1_FD1_merchandise_id) REFERENCES T002_merchandise(T002_PK1_merchandise) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createMerchandiseImageTable);
            System.out.println("✓ T002_1_merchandise_imageテーブル作成完了");

            // T003_employeeテーブル作成（自動採番ID追加、外部キー: 店舗ID）
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS T003_employee (" +
                "T003_PK1_employee SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T003_FD1_employee VARCHAR(30) NOT NULL, " +
                "T003_FD2_employee INTEGER NOT NULL, " +
                "FOREIGN KEY (T003_FD2_employee) REFERENCES T001_store(T001_PK1_store) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createEmployeeTable);
            System.out.println("✓ T003_employeeテーブル作成完了");

            // T004_userテーブル作成（自動採番ID追加、外部キー: 店舗ID）
            String createUserTable = "CREATE TABLE IF NOT EXISTS T004_user (" +
                "T004_PK1_user SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T004_FD1_user VARCHAR(50) NOT NULL, " +
                "T004_FD2_user VARCHAR(30) NOT NULL, " +
                "T004_FD3_user VARCHAR(12), " +
                "T004_FD4_user VARCHAR(60) NOT NULL, " +
                "T004_FD5_user VARCHAR(30), " +
                "T004_FD6_user INTEGER, " +
                "T004_FD7_user BOOLEAN NOT NULL DEFAULT FALSE, " +
                "FOREIGN KEY (T004_FD6_user) REFERENCES T001_store(T001_PK1_store))";
            stmt.execute(createUserTable);
            System.out.println("✓ T004_userテーブル作成完了");
            // T005_bookingテーブル作成（自動採番ID追加、外部キー: ユーザーID、商品ID）
            String createBookingTable = "CREATE TABLE IF NOT EXISTS T005_booking (" +
                "T005_PK1_booking SERIAL PRIMARY KEY, " +  // 自動採番ID
                "T005_FD1_booking CHAR(7) NOT NULL DEFAULT '0', " +
                "T005_FD2_booking INTEGER NOT NULL, " +
                "T005_FD3_booking TIMESTAMP NOT NULL, " +
                "T005_FD4_booking INTEGER NOT NULL, " +
                "T005_FD5_booking TIMESTAMP NOT NULL, " +
                "T005_FD6_booking BOOLEAN NOT NULL DEFAULT FALSE, " +
                "FOREIGN KEY (T005_FD2_booking) REFERENCES T004_user(T004_PK1_user), " +
                "FOREIGN KEY (T005_FD4_booking) REFERENCES T002_merchandise(T002_PK1_merchandise))";
            stmt.execute(createBookingTable);
            System.out.println("✓ T005_bookingテーブル作成完了");
            System.out.println("\n========================================");
            System.out.println("すべてのテーブル作成が完了しました！");
            System.out.println("========================================");
            System.out.println("\n【自動採番ID】");
            System.out.println("• T001_store.T001_PK1_store (SERIAL)");
            System.out.println("• T002_merchandise.T002_PK1_merchandise (SERIAL)");
            System.out.println("• T002_1_merchandise_image.T002_1_PK1_image (SERIAL)");
            System.out.println("• T003_employee.T003_PK1_employee (SERIAL)");
            System.out.println("• T004_user.T004_PK1_user (SERIAL)");
            System.out.println("• T005_booking.T005_PK1_booking (SERIAL)");
            System.out.println("\n【T001_storeの追加カラム】");
            System.out.println("• T001_FD7_store: 店舗連絡用メールアドレス (VARCHAR(100))");
            System.out.println("• T001_FD8_store: 営業許可証データ (BYTEA - 画像/PDF)");
            System.out.println("• T001_FD9_store: 営業許可証ファイル名 (VARCHAR(100))");
            System.out.println("\n【T002_1_merchandise_imageテーブル（商品画像）】");
            System.out.println("• T002_1_PK1_image: 画像ID (SERIAL PRIMARY KEY)");
            System.out.println("• T002_1_FD1_merchandise_id: 商品ID (INTEGER - 外部キー)");
            System.out.println("• T002_1_FD2_image_data: 画像データ (BYTEA)");
            System.out.println("• T002_1_FD3_file_name: ファイル名 (VARCHAR(100))");
            System.out.println("• T002_1_FD4_display_order: 表示順序 (INTEGER)");
            System.out.println("• T002_1_FD5_uploaded_at: アップロード日時 (TIMESTAMP)");
            System.out.println("→ 1つの商品に対して複数の画像を登録可能");
            System.out.println("\n【設定された外部キー】");
            System.out.println("1. T002_merchandise.T002_FD8_merchandise → T001_store.T001_PK1_store");
            System.out.println("2. T002_1_merchandise_image.T002_1_FD1_merchandise_id → T002_merchandise.T002_PK1_merchandise (CASCADE)");
            System.out.println("3. T003_employee.T003_FD2_employee → T001_store.T001_PK1_store (CASCADE)");
            System.out.println("4. T004_user.T004_FD6_user → T001_store.T001_PK1_store");
            System.out.println("5. T005_booking.T005_FD2_booking → T004_user.T004_PK1_user");
            System.out.println("6. T005_booking.T005_FD4_booking → T002_merchandise.T002_PK1_merchandise");
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQLドライバが見つかりません。");
            System.err.println("Maven/Gradleに以下の依存関係を追加してください:");
            System.err.println("org.postgresql:postgresql:42.7.0");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("エラーが発生しました:");
            e.printStackTrace();
        }
    }
}