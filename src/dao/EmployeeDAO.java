package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;
import tool.DBManager;

public class EmployeeDAO {

    // DB接続取得
    private Connection getConnection() throws Exception {
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


    // 社員コードで検索
    public Employee selectByCode(String employeeCode) throws Exception {
        String sql =
            "SELECT t003_pk1_employee, t003_fd1_employee, t003_fd2_employee, t003_fd3_employee, t001_fd1_store " +
            "FROM t003_employee " +
            "JOIN t001_store ON t003_employee.t003_fd2_employee = t001_store.t001_pk1_store " +
            "WHERE t003_fd3_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employeeCode);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("t003_pk1_employee"));
                e.setEmployeeCode(rs.getString("t003_fd3_employee"));
                e.setEmployeeNumber(rs.getString("t003_fd3_employee"));
                e.setEmployeeName(rs.getString("t003_fd1_employee"));
                e.setStoreCode(String.valueOf(rs.getInt("t003_fd2_employee")));
                e.setStoreName(rs.getString("t001_fd1_store"));
                return e;
            }
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