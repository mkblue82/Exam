package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;
import tool.DBManager;

public class EmployeeDAO {

    private Connection connection;

    // デフォルトコンストラクタ（新しい接続を取得）
    public EmployeeDAO() throws Exception {
        this.connection = new DBManager().getConnection();
    }

    // 接続を受け取るコンストラクタ（トランザクション管理用）
    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    // DB接続取得（互換性のため残す）
    private Connection getConnection() throws Exception {
        if (this.connection != null) {
            return this.connection;
        }
        return new DBManager().getConnection();
    }

    // 社員登録（社員番号を追加）
    public int insert(Employee employee) throws Exception {
        String sql =
            "INSERT INTO t003_employee (t003_fd1_employee, t003_fd2_employee, t003_fd3_employee) " +
            "VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employee.getEmployeeName());
            st.setInt(2, Integer.parseInt(employee.getStoreCode()));
            st.setString(3, employee.getEmployeeNumber());
            return st.executeUpdate();
        }
    }

    // 店舗コードで社員一覧取得
    public List<Employee> selectByStoreCode(String storeCode) throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "WHERE t003_fd2_employee = ? ORDER BY t003_pk1_employee";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, Integer.parseInt(storeCode));
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));  // 社員番号を社員コードとして使用
                e.setEmployeeNumber(rs.getString("t003_fd3_employee")); // 社員番号
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                e.setStoreName(rs.getString("t001_fd1_store"));
                list.add(e);
            }
        }
        return list;
    }

    // 社員番号で検索（trimを追加してデバッグ強化）
    public Employee selectByCode(String employeeCode) throws Exception {
        // 前後の空白を削除
        if (employeeCode != null) {
            employeeCode = employeeCode.trim();
        }

        System.out.println("🔍 EmployeeDAO.selectByCode 呼び出し");
        System.out.println("   検索する社員番号: [" + employeeCode + "]");
        System.out.println("   社員番号の長さ: " + (employeeCode != null ? employeeCode.length() : "null"));

        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "WHERE t003_fd3_employee = ?";

        Connection con = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = con.prepareStatement(sql);
            st.setString(1, employeeCode);

            System.out.println("   SQL実行: " + sql);
            System.out.println("   パラメータ: [" + employeeCode + "]");

            rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                e.setStoreName(rs.getString("t001_fd1_store"));

                System.out.println("✅ 社員が見つかりました！");
                System.out.println("   社員ID: " + e.getId());
                System.out.println("   社員名: " + e.getEmployeeName());
                System.out.println("   DB上の社員番号: [" + e.getEmployeeNumber() + "]");

                return e;
            } else {
                System.out.println("❌ 社員が見つかりませんでした");

                // デバッグ: DBに登録されている社員番号を表示
                String debugSql = "SELECT t003_fd3_employee FROM t003_employee LIMIT 10";
                try (PreparedStatement debugSt = con.prepareStatement(debugSql);
                     ResultSet debugRs = debugSt.executeQuery()) {
                    System.out.println("   📋 DB内の社員番号一覧（最大10件）:");
                    while (debugRs.next()) {
                        String dbCode = debugRs.getString("t003_fd3_employee");
                        System.out.println("      - [" + dbCode + "] (長さ: " + (dbCode != null ? dbCode.length() : "null") + ")");
                    }
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            // connectionはクローズしない（呼び出し元で管理）
        }

        return null;
    }

    public Employee selectById(int id) throws Exception {
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee " +
            "FROM t003_employee WHERE t003_pk1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                return e;
            }
        }
        return null;
    }

    // 全社員取得（必要なら）
    public List<Employee> selectAll() throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "ORDER BY t003_pk1_employee";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                e.setStoreName(rs.getString("t001_fd1_store"));
                list.add(e);
            }
        }
        return list;
    }

    // 更新
    public int update(Employee employee) throws Exception {
        String sql =
            "UPDATE t003_employee SET t003_fd1_employee = ?, t003_fd2_employee = ?, t003_fd3_employee = ? " +
            "WHERE t003_pk1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employee.getEmployeeName());
            st.setInt(2, Integer.parseInt(employee.getStoreCode()));
            st.setString(3, employee.getEmployeeNumber());
            st.setInt(4, employee.getId());
            return st.executeUpdate();
        }
    }

    // 削除
    public int delete(String employeeCode) throws Exception {
        String sql =
            "DELETE FROM t003_employee WHERE t003_pk1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, Integer.parseInt(employeeCode));
            return st.executeUpdate();
        }
    }
}