package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Store;

public class StoreDAO extends DAO {

    // 全店舗を取得
    public List<Store> selectAll() throws Exception {
        List<Store> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select * from T001_store order by T001_ID_store");
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Store s = new Store();
            s.setStoreId(rs.getInt("T001_PK1_store"));
            s.setStoreName(rs.getString("T001_FD1_store"));
            s.setAddress(rs.getString("T001_FD2_store"));
            s.setPhone(rs.getString("T001_FD3_store"));
            s.setRepresentative(rs.getString("T001_FD4_store"));
            s.setOpeningTime(rs.getTime("T001_FD5_store"));
            s.setCategory(rs.getString("T001_FD6_store"));
            s.setEmail(rs.getString("T001_FD7_store"));
            s.setLicense(rs.getBytes("T001_FD8_store"));
            s.setLicenseFileName(rs.getString("T001_FD9_store"));
            list.add(s);
        }

        st.close();
        con.close();
        return list;
    }

    // 店舗コードで検索
    public Store selectByCode(String storeId) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select * from T001_store where T001_PK1_store = ?");
        st.setString(1, storeId);
        ResultSet rs = st.executeQuery();

        Store s = null;
        if (rs.next()) {
            s = new Store();
            s.setStoreId(rs.getInt("T001_PK1_store"));
            s.setStoreName(rs.getString("T001_FD1_store"));
            s.setAddress(rs.getString("T001_FD2_store"));
            s.setPhone(rs.getString("T001_FD3_store"));
            s.setRepresentative(rs.getString("T001_FD4_store"));
            s.setOpeningTime(rs.getTime("T001_FD5_store"));
            s.setCategory(rs.getString("T001_FD6_store"));
            s.setEmail(rs.getString("T001_FD7_store"));
            s.setLicense(rs.getBytes("T001_FD8_store"));
            s.setLicenseFileName(rs.getString("T001_FD9_store"));
        }

        st.close();
        con.close();
        return s;
    }

    // IDで検索
    public Store selectById(int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select * from T001_store where T001_ID_store = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();

        Store s = null;
        if (rs.next()) {
            s = new Store();
            s.setStoreId(rs.getInt("T001_PK1_store"));
            s.setStoreName(rs.getString("T001_FD1_store"));
            s.setAddress(rs.getString("T001_FD2_store"));
            s.setPhone(rs.getString("T001_FD3_store"));
            s.setRepresentative(rs.getString("T001_FD4_store"));
            s.setOpeningTime(rs.getTime("T001_FD5_store"));
            s.setCategory(rs.getString("T001_FD6_store"));
            s.setEmail(rs.getString("T001_FD7_store"));
            s.setLicense(rs.getBytes("T001_FD8_store"));
            s.setLicenseFileName(rs.getString("T001_FD9_store"));
        }

        st.close();
        con.close();
        return s;
    }

    // 店舗を登録
    public int insert(Store store) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "insert into T001_store (T001_PK1_store, T001_FD1_store, T001_FD2_store, " +
            "T001_FD3_store, T001_FD4_store, T001_FD5_store, T001_FD6_store, " +
            "T001_FD7_store, T001_FD8_store, T001_FD9_store) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        st.setInt(1, store.getStoreId());
        st.setString(2, store.getStoreName());
        st.setString(3, store.getAddress());
        st.setString(4, store.getPhone());
        st.setString(5, store.getRepresentative());
        st.setTime(6, store.getOpeningTime());
        st.setString(7, store.getCategory());
        st.setString(8, store.getEmail());
        st.setBytes(9, store.getLicense());
        st.setString(10, store.getLicenseFileName());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 店舗を更新
    public int update(Store store) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "update T001_store set T001_FD1_store = ?, T001_FD2_store = ?, " +
            "T001_FD3_store = ?, T001_FD4_store = ?, T001_FD5_store = ?, " +
            "T001_FD6_store = ?, T001_FD7_store = ?, T001_FD8_store = ?, " +
            "T001_FD9_store = ? where T001_PK1_store = ?");

        st.setString(1, store.getStoreName());
        st.setString(2, store.getAddress());
        st.setString(3, store.getPhone());
        st.setString(4, store.getRepresentative());
        st.setTime(5, store.getOpeningTime());
        st.setString(6, store.getCategory());
        st.setString(7, store.getEmail());
        st.setBytes(8, store.getLicense());
        st.setString(9, store.getLicenseFileName());
        st.setInt(10, store.getStoreId());

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }

    // 店舗を削除
    public int delete(String storeCode) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "delete from T001_store where T001_PK1_store = ?");
        st.setString(1, storeCode);

        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
}