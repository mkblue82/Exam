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

    // 社員登録（社員番号を追加）
    public int insert(Employee employee) throws Exception {
        String sql =
            "INSERT INTO t003_employee (t003_fd1_employee, t003_fd2_employee, t003_fd3_employee) " +
            "VALUES (?, ?, ?)";

        Connection con = null;
        PreparedStatement st = null;

        try {
            con = new DBManager().getConnection();
            st = con.prepareStatement(sql);
            st.setString(1, employee.getEmployeeName());
            st.setInt(2, Integer.parseInt(employee.getStoreCode()));
            st.setString(3, employee.getEmployeeNumber());
            return st.executeUpdate();
        } finally {
            if (st != null) st.close();
            if (con != null) con.close();
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

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = this.connection.prepareStatement(sql);  // ← this.connectionを使う
            st.setInt(1, Integer.parseInt(storeCode));
            rs = st.executeQuery();

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
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            // connectionはクローズしない
        }

        return list;
    }

    // 社員番号で検索（trimを追加してデバッグ強化）
    public Employee selectByCode(String employeeCode) throws Exception {
        if (employeeCode != null) {
            employeeCode = employeeCode.trim();
        }

        System.out.println("🔍 EmployeeDAO.selectByCode 呼び出し");
        System.out.println("   検索する社員番号: [" + employeeCode + "]");

        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "WHERE t003_fd3_employee = ?";

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = this.connection.prepareStatement(sql);
            st.setString(1, employeeCode);
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
                return e;
            } else {
                System.out.println("❌ 社員が見つかりませんでした");
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return null;
    }

    // ★★★ 社員番号の重複チェック（店舗ごと）★★★
    public Employee getByEmployeeNumber(String employeeNumber, String storeCode) throws Exception {
        // 前後の空白を削除
        if (employeeNumber != null) {
            employeeNumber = employeeNumber.trim();
        }

        System.out.println("🔍 EmployeeDAO.getByEmployeeNumber 呼び出し（重複チェック）");
        System.out.println("   検索する社員番号: [" + employeeNumber + "]");
        System.out.println("   店舗コード: " + storeCode);

        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "WHERE t003_fd3_employee = ? AND t003_fd2_employee = ?";

        // ★ 新しいコネクションを作成（既存のコネクションをクローズしないため）
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con = new DBManager().getConnection();
            st = con.prepareStatement(sql);
            st.setString(1, employeeNumber);
            st.setInt(2, Integer.parseInt(storeCode));

            System.out.println("   SQL実行: " + sql);

            rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                e.setStoreName(rs.getString("t001_fd1_store"));

                System.out.println("⚠️ 重複発見！この社員番号は既に登録されています");
                System.out.println("   社員ID: " + e.getId());
                System.out.println("   社員名: " + e.getEmployeeName());

                return e;
            } else {
                System.out.println("✅ 重複なし。この社員番号は使用可能です");
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();
        }

        return null;
    }

    public Employee selectById(int id) throws Exception {
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee " +
            "FROM t003_employee WHERE t003_pk1_employee = ?";

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = this.connection.prepareStatement(sql);
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                return e;
            }
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return null;
    }

    // 全社員取得
    public List<Employee> selectAll() throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "ORDER BY t003_pk1_employee";

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = this.connection.prepareStatement(sql);
            rs = st.executeQuery();

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
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
        }

        return list;
    }

    // 更新
    public int update(Employee employee) throws Exception {
        String sql =
            "UPDATE t003_employee SET t003_fd1_employee = ?, t003_fd2_employee = ?, t003_fd3_employee = ? " +
            "WHERE t003_pk1_employee = ?";

        PreparedStatement st = null;

        try {
            st = this.connection.prepareStatement(sql);
            st.setString(1, employee.getEmployeeName());
            st.setInt(2, Integer.parseInt(employee.getStoreCode()));
            st.setString(3, employee.getEmployeeNumber());
            st.setInt(4, employee.getId());
            return st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }

    // 削除
    public int delete(String employeeCode) throws Exception {
        String sql =
            "DELETE FROM t003_employee WHERE t003_pk1_employee = ?";

        PreparedStatement st = null;

        try {
            st = this.connection.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(employeeCode));
            return st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }
}