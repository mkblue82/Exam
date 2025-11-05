package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Merchandise;

public class MerchandiseDAO extends DAO {
    
    // 全商品を取得（店舗名も取得）
    public List<Merchandise> selectAll() throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_ID_merchandise, T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "order by T002_ID_merchandise");
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setId(rs.getInt("T002_ID_merchandise"));
            m.setMerchandiseCode(rs.getString("T002_PK1_merchandise"));
            m.setField1(rs.getString("T002_FD1_merchandise"));
            m.setField2(rs.getString("T002_FD2_merchandise"));
            m.setField3(rs.getDate("T002_FD3_merchandise"));
            m.setField4(rs.getString("T002_FD4_merchandise"));
            m.setField5(rs.getString("T002_FD5_merchandise"));
            m.setField6(rs.getString("T002_FD6_merchandise"));
            m.setField7(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreCode(rs.getString("T002_FD8_merchandise"));
            m.setField9(rs.getBoolean("T002_FD9_merchandise"));
            m.setStoreName(rs.getString("T001_FD1_store"));
            list.add(m);
        }
        
        st.close();
        con.close();
        return list;
    }
    
    // 商品コードで検索
    public Merchandise selectByCode(String merchandiseCode) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_ID_merchandise, T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "where T002_PK1_merchandise = ?");
        st.setString(1, merchandiseCode);
        ResultSet rs = st.executeQuery();
        
        Merchandise m = null;
        if (rs.next()) {
            m = new Merchandise();
            m.setId(rs.getInt("T002_ID_merchandise"));
            m.setMerchandiseCode(rs.getString("T002_PK1_merchandise"));
            m.setField1(rs.getString("T002_FD1_merchandise"));
            m.setField2(rs.getString("T002_FD2_merchandise"));
            m.setField3(rs.getDate("T002_FD3_merchandise"));
            m.setField4(rs.getString("T002_FD4_merchandise"));
            m.setField5(rs.getString("T002_FD5_merchandise"));
            m.setField6(rs.getString("T002_FD6_merchandise"));
            m.setField7(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreCode(rs.getString("T002_FD8_merchandise"));
            m.setField9(rs.getBoolean("T002_FD9_merchandise"));
            m.setStoreName(rs.getString("T001_FD1_store"));
        }
        
        st.close();
        con.close();
        return m;
    }
    
    // IDで検索
    public Merchandise selectById(int id) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_ID_merchandise, T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "where T002_ID_merchandise = ?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        
        Merchandise m = null;
        if (rs.next()) {
            m = new Merchandise();
            m.setId(rs.getInt("T002_ID_merchandise"));
            m.setMerchandiseCode(rs.getString("T002_PK1_merchandise"));
            m.setField1(rs.getString("T002_FD1_merchandise"));
            m.setField2(rs.getString("T002_FD2_merchandise"));
            m.setField3(rs.getDate("T002_FD3_merchandise"));
            m.setField4(rs.getString("T002_FD4_merchandise"));
            m.setField5(rs.getString("T002_FD5_merchandise"));
            m.setField6(rs.getString("T002_FD6_merchandise"));
            m.setField7(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreCode(rs.getString("T002_FD8_merchandise"));
            m.setField9(rs.getBoolean("T002_FD9_merchandise"));
            m.setStoreName(rs.getString("T001_FD1_store"));
        }
        
        st.close();
        con.close();
        return m;
    }
    
    // 店舗コードで商品一覧を取得
    public List<Merchandise> selectByStoreCode(String storeCode) throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_ID_merchandise, T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "where T002_FD8_merchandise = ? " +
            "order by T002_ID_merchandise");
        st.setString(1, storeCode);
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setId(rs.getInt("T002_ID_merchandise"));
            m.setMerchandiseCode(rs.getString("T002_PK1_merchandise"));
            m.setField1(rs.getString("T002_FD1_merchandise"));
            m.setField2(rs.getString("T002_FD2_merchandise"));
            m.setField3(rs.getDate("T002_FD3_merchandise"));
            m.setField4(rs.getString("T002_FD4_merchandise"));
            m.setField5(rs.getString("T002_FD5_merchandise"));
            m.setField6(rs.getString("T002_FD6_merchandise"));
            m.setField7(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreCode(rs.getString("T002_FD8_merchandise"));
            m.setField9(rs.getBoolean("T002_FD9_merchandise"));
            m.setStoreName(rs.getString("T001_FD1_store"));
            list.add(m);
        }
        
        st.close();
        con.close();
        return list;
    }
    
    // 商品を登録
    public int insert(Merchandise merchandise) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "insert into T002_merchandise (T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        st.setString(1, merchandise.getMerchandiseCode());
        st.setString(2, merchandise.getField1());
        st.setString(3, merchandise.getField2());
        st.setDate(4, merchandise.getField3());
        st.setString(5, merchandise.getField4());
        st.setString(6, merchandise.getField5());
        st.setString(7, merchandise.getField6());
        st.setTimestamp(8, merchandise.getField7());
        st.setString(9, merchandise.getStoreCode());
        st.setBoolean(10, merchandise.isField9());
        
        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
    
    // 商品を更新
    public int update(Merchandise merchandise) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "update T002_merchandise set T002_FD1_merchandise = ?, T002_FD2_merchandise = ?, " +
            "T002_FD3_merchandise = ?, T002_FD4_merchandise = ?, T002_FD5_merchandise = ?, " +
            "T002_FD6_merchandise = ?, T002_FD7_merchandise = ?, T002_FD8_merchandise = ?, " +
            "T002_FD9_merchandise = ? where T002_PK1_merchandise = ?");
        
        st.setString(1, merchandise.getField1());
        st.setString(2, merchandise.getField2());
        st.setDate(3, merchandise.getField3());
        st.setString(4, merchandise.getField4());
        st.setString(5, merchandise.getField5());
        st.setString(6, merchandise.getField6());
        st.setTimestamp(7, merchandise.getField7());
        st.setString(8, merchandise.getStoreCode());
        st.setBoolean(9, merchandise.isField9());
        st.setString(10, merchandise.getMerchandiseCode());
        
        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
    
    // 商品を削除
    public int delete(String merchandiseCode) throws Exception {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "delete from T002_merchandise where T002_PK1_merchandise = ?");
        st.setString(1, merchandiseCode);
        
        int line = st.executeUpdate();
        st.close();
        con.close();
        return line;
    }
    
    // 有効な商品のみを取得（field9がtrueのもの）
    public List<Merchandise> selectActive() throws Exception {
        List<Merchandise> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement(
            "select T002_ID_merchandise, T002_PK1_merchandise, T002_FD1_merchandise, " +
            "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
            "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
            "T002_FD8_merchandise, T002_FD9_merchandise, T001_FD1_store " +
            "from T002_merchandise " +
            "join T001_store on T002_merchandise.T002_FD8_merchandise = T001_store.T001_PK1_store " +
            "where T002_FD9_merchandise = true " +
            "order by T002_ID_merchandise");
        ResultSet rs = st.executeQuery();
        
        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setId(rs.getInt("T002_ID_merchandise"));
            m.setMerchandiseCode(rs.getString("T002_PK1_merchandise"));
            m.setField1(rs.getString("T002_FD1_merchandise"));
            m.setField2(rs.getString("T002_FD2_merchandise"));
            m.setField3(rs.getDate("T002_FD3_merchandise"));
            m.setField4(rs.getString("T002_FD4_merchandise"));
            m.setField5(rs.getString("T002_FD5_merchandise"));
            m.setField6(rs.getString("T002_FD6_merchandise"));
            m.setField7(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreCode(rs.getString("T002_FD8_merchandise"));
            m.setField9(rs.getBoolean("T002_FD9_merchandise"));
            m.setStoreName(rs.getString("T001_FD1_store"));
            list.add(m);
        }
        
        st.close();
        con.close();
        return list;
    }
}
