package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;
import tool.DBManager;

public class EmployeeDAO {

    // ▼ DB接続を返す
    private Connection getConnection() throws Exception {
        return new DBManager().getConnection();
    }

    // 全従業員取得
    public List<Employee> selectAll() throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql =
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "order by T003_ID_employee";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("T003_ID_employee"));
                e.setEmployeeCode(rs.getString("T003_PK1_employee"));
                e.setEmployeeName(rs.getString("T003_FD1_employee"));
                e.setStoreCode(rs.getString("T003_FD2_employee"));
                e.setStoreName(rs.getString("T001_FD1_store"));
                list.add(e);
            }
        }
        return list;
    }

    // 社員コードで検索
    public Employee selectByCode(String employeeCode) throws Exception {
        String sql =
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "where T003_PK1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employeeCode);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("T003_ID_employee"));
                e.setEmployeeCode(rs.getString("T003_PK1_employee"));
                e.setEmployeeName(rs.getString("T003_FD1_employee"));
                e.setStoreCode(rs.getString("T003_FD2_employee"));
                e.setStoreName(rs.getString("T001_FD1_store"));
                return e;
            }
        }
        return null;
    }

    // 店舗コードで一覧取得
    public List<Employee> selectByStoreCode(String storeCode) throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql =
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "where T003_FD2_employee = ? order by T003_ID_employee";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, storeCode);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("T003_ID_employee"));
                e.setEmployeeCode(rs.getString("T003_PK1_employee"));
                e.setEmployeeName(rs.getString("T003_FD1_employee"));
                e.setStoreCode(rs.getString("T003_FD2_employee"));
                e.setStoreName(rs.getString("T001_FD1_store"));
                list.add(e);
            }
        }
        return list;
    }

    public int insert(Employee employee) throws Exception {
        String sql =
            "insert into T003_employee (T003_PK1_employee, T003_FD1_employee, T003_FD2_employee) " +
            "values (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, employee.getId());  // ← integer PK
            st.setString(2, employee.getEmployeeName());
            st.setString(3, employee.getStoreCode());

            return st.executeUpdate();
        }
    }


    // 更新
    public int update(Employee employee) throws Exception {
        String sql =
            "update T003_employee set T003_FD1_employee = ?, T003_FD2_employee = ? " +
            "where T003_PK1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employee.getEmployeeName());
            st.setString(2, employee.getStoreCode());
            st.setString(3, employee.getEmployeeCode());
            return st.executeUpdate();
        }
    }

    // 削除
    public int delete(String employeeCode) throws Exception {
        String sql =
            "delete from T003_employee where T003_PK1_employee = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, employeeCode);
            return st.executeUpdate();
        }
    }
}
