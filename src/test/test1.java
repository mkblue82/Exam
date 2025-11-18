package test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class test1 {

    public static void main(String[] args) {

        // PostgreSQL接続情報
        String url = "jdbc:postgresql://ep-rapid-surf-a100ysmf-pooler.ap-southeast-1.aws.neon.tech:5432/neondb";
        String user = "neondb_owner";
        String password = "npg_fpWk07TEejNO";

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            // T001_storeテーブル作成
            String createStoreTable = "CREATE TABLE IF NOT EXISTS T001_store (" +
                "T001_PK1_store SERIAL PRIMARY KEY, " +
                "T001_FD1_store VARCHAR(30) NOT NULL, " +
                "T001_FD2_store VARCHAR(100) NOT NULL, " +
                "T001_FD3_store VARCHAR(12) NOT NULL, " +
                "T001_FD4_store VARCHAR(30) NOT NULL, " +
                "T001_FD5_store TIME, " +
                "T001_FD6_store CHAR(10), " +
                "T001_FD7_store VARCHAR(100), " +
                "T001_FD8_store BYTEA, " +
                "T001_FD9_store VARCHAR(100))";
            stmt.execute(createStoreTable);
            System.out.println("✓ T001_storeテーブル作成完了");

            // T002_merchandiseテーブル作成（割引カラム追加）
            String createMerchandiseTable = "CREATE TABLE IF NOT EXISTS T002_merchandise (" +
                "T002_PK1_merchandise SERIAL PRIMARY KEY, " +
                "T002_FD1_merchandise CHAR(5) NOT NULL DEFAULT '0', " +
                "T002_FD2_merchandise CHAR(10) NOT NULL DEFAULT '0', " +
                "T002_FD3_merchandise DATE NOT NULL, " +
                "T002_FD4_merchandise VARCHAR(40) NOT NULL, " +
                "T002_FD5_merchandise VARCHAR(50) NOT NULL, " +
                "T002_FD6_merchandise CHAR(50), " +
                "T002_FD7_merchandise TIMESTAMP NOT NULL, " +
                "T002_FD8_merchandise INTEGER NOT NULL, " +
                "T002_FD9_merchandise BOOLEAN NOT NULL DEFAULT FALSE, " +
                "T002_FD10_discount_percent INTEGER DEFAULT 0 CHECK (T002_FD10_discount_percent >= 0 AND T002_FD10_discount_percent <= 100), " +
                "T002_FD11_discount_start_time INTEGER DEFAULT NULL CHECK (T002_FD11_discount_start_time IS NULL OR (T002_FD11_discount_start_time >= 0 AND T002_FD11_discount_start_time <= 23)), " +
                "FOREIGN KEY (T002_FD8_merchandise) REFERENCES T001_store(T001_PK1_store))";
            stmt.execute(createMerchandiseTable);
            System.out.println("✓ T002_merchandiseテーブル作成完了");

            // T002_1_merchandise_imageテーブル作成
            String createMerchandiseImageTable = "CREATE TABLE IF NOT EXISTS T002_1_merchandise_image (" +
                "T002_1_PK1_image SERIAL PRIMARY KEY, " +
                "T002_1_FD1_merchandise_id INTEGER NOT NULL, " +
                "T002_1_FD2_image_data BYTEA NOT NULL, " +
                "T002_1_FD3_file_name VARCHAR(100) NOT NULL, " +
                "T002_1_FD4_display_order INTEGER DEFAULT 0, " +
                "T002_1_FD5_uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (T002_1_FD1_merchandise_id) REFERENCES T002_merchandise(T002_PK1_merchandise) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createMerchandiseImageTable);
            System.out.println("✓ T002_1_merchandise_imageテーブル作成完了");

            // T003_employeeテーブル作成
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS T003_employee (" +
                "T003_PK1_employee SERIAL PRIMARY KEY, " +
                "T003_FD1_employee VARCHAR(30) NOT NULL, " +
                "T003_FD2_employee INTEGER NOT NULL, " +
                "FOREIGN KEY (T003_FD2_employee) REFERENCES T001_store(T001_PK1_store) " +
                "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createEmployeeTable);
            System.out.println("✓ T003_employeeテーブル作成完了");

            // T004_userテーブル作成
            String createUserTable = "CREATE TABLE IF NOT EXISTS T004_user (" +
                "T004_PK1_user SERIAL PRIMARY KEY, " +
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

            // T005_bookingテーブル作成
            String createBookingTable = "CREATE TABLE IF NOT EXISTS T005_booking (" +
                "T005_PK1_booking SERIAL PRIMARY KEY, " +
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

            System.out.println("\n【T002_merchandiseの割引カラム】NEW!");
            System.out.println("• T002_FD10_discount_percent: 割引率 (INTEGER 0-100)");
            System.out.println("• T002_FD11_discount_start_time: 割引開始時間 (INTEGER 0-23 or NULL)");

            System.out.println("\n【自動採番ID】");
            System.out.println("• T001_store.T001_PK1_store (SERIAL)");
            System.out.println("• T002_merchandise.T002_PK1_merchandise (SERIAL)");
            System.out.println("• T002_1_merchandise_image.T002_1_PK1_image (SERIAL)");
            System.out.println("• T003_employee.T003_PK1_employee (SERIAL)");
            System.out.println("• T004_user.T004_PK1_user (SERIAL)");
            System.out.println("• T005_booking.T005_PK1_booking (SERIAL)");

            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("エラーが発生しました:");
            e.printStackTrace();
        }
    }
}