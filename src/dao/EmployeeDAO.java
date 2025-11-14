package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Employee;

public class EmployeeDAO extends DAO {

    // 全従業員を取得（店舗名も取得）
    public List<Employee> selectAll() throws Exception {
        List<Employee> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "order by T003_ID_employee");
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

        st.close();
        con.close();
        return list;
    }

    // 従業員コードで検索
    public Employee selectByCode(String employeeCode) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "where T003_PK1_employee = ?");
        st.setString(1, employeeCode);
        ResultSet rs = st.executeQuery();

        Employee e = null;
        if (rs.next()) {
            e = new Employee();
            e.setId(rs.getInt("T003_ID_employee"));
            e.setEmployeeCode(rs.getString("T003_PK1_employee"));
            e.setEmployeeName(rs.getString("T003_FD1_employee"));
            e.setStoreCode(rs.getString("T003_FD2_employee"));
            e.setStoreName(rs.getString("T001_FD1_store"));
        }

        st.close();
        con.close();
        return e;
    }

    // IDで検索
    public Employee selectById(int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "where T003_ID_employee = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        Employee e = null;
        if (rs.next()) {
            e = new Employee();
            e.setId(rs.getInt("T003_ID_employee"));
            e.setEmployeeCode(rs.getString("T003_PK1_employee"));
            e.setEmployeeName(rs.getString("T003_FD1_employee"));
            e.setStoreCode(rs.getString("T003_FD2_employee"));
            e.setStoreName(rs.getString("T001_FD1_store"));
        }

        st.close();
        con.close();
        return e;
    }

    // 店舗IDで従業員一覧を取得
    public List<Employee> selectByStoreCode(String storeCode) throws Exception {
        List<Employee> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T003_ID_employee, T003_PK1_employee, T003_FD1_employee, " +
            "T003_FD2_employee, T001_FD1_store " +
            "from T003_employee " +
            "join T001_store on T003_employee.T003_FD2_employee = T001_store.T001_PK1_store " +
            "where T003_FD2_employee = ? " +
            "order by T003_ID_employee");
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

        st.close();
        con.close();
        return list;
    }

    // 従業員を登録
    public int insert(Employee employee) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "insert into T003_employee (T003_PK1_employee, T003_FD1_employee, T003_FD2_employee) " +
            "values (?, ?, ?)");

        st.setString(1, employee.getEmployeeCode());
        st.setString(2, employee.getEmployeeName());
        st.setString(3, employee.getStoreCode());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 従業員を更新
    public int update(Employee employee) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "update T003_employee set T003_FD1_employee = ?, T003_FD2_employee = ? " +
            "where T003_PK1_employee = ?");

        st.setString(1, employee.getEmployeeName());
        st.setString(2, employee.getStoreCode());
        st.setString(3, employee.getEmployeeCode());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 従業員を削除
    public int delete(String employeeCode) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "delete from T003_employee where T003_PK1_employee = ?");
        st.setString(1, employeeCode);

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
}
