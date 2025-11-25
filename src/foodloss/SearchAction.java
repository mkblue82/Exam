package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Merchandise;
import bean.Store;
import dao.DAO;
import tool.Action;

public class SearchAction extends Action {

    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 検索キーワードを取得
        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            request.setAttribute("error", "検索キーワードを入力してください");
            return "search.jsp";
        }

        keyword = keyword.trim();

        // データベース接続を取得
        DAO dao = new DAO();
        Connection connection = dao.getConnection();

        // 検索結果を格納するリスト
        List<SearchResult> searchResults = new ArrayList<>();

        try {
            // 商品名またはタグで検索
            List<Merchandise> merchandises = searchMerchandisesByKeyword(connection, keyword);

            // 店舗名で検索
            List<Store> stores = searchStoresByKeyword(connection, keyword);

            // 店舗ごとにグループ化
            if (!merchandises.isEmpty()) {
                for (Merchandise merchandise : merchandises) {
                    Store store = getStoreById(connection, merchandise.getStoreId());

                    if (store == null) continue;

                    // 既存の店舗結果を探す
                    SearchResult existingResult = null;
                    for (SearchResult result : searchResults) {
                        if (result.getStoreId() == store.getStoreId()) {
                            existingResult = result;
                            break;
                        }
                    }

                    if (existingResult != null) {
                        existingResult.addMerchandise(merchandise);
                    } else {
                        SearchResult newResult = new SearchResult();
                        newResult.setStore(store);
                        newResult.addMerchandise(merchandise);
                        searchResults.add(newResult);
                    }
                }
            }

            // 店舗名で検索された場合、その店舗の全商品を追加
            for (Store store : stores) {
                List<Merchandise> storeMerchandises = getMerchandisesByStoreId(connection, store.getStoreId());

                boolean storeExists = false;
                for (SearchResult result : searchResults) {
                    if (result.getStoreId() == store.getStoreId()) {
                        storeExists = true;
                        // 重複しない商品のみ追加
                        for (Merchandise m : storeMerchandises) {
                            if (!result.hasMerchandise(m.getMerchandiseId())) {
                                result.addMerchandise(m);
                            }
                        }
                        break;
                    }
                }

                if (!storeExists && !storeMerchandises.isEmpty()) {
                    SearchResult newResult = new SearchResult();
                    newResult.setStore(store);
                    newResult.setMerchandises(storeMerchandises);
                    searchResults.add(newResult);
                }
            }

            // 結果をリクエストに設定
            request.setAttribute("searchResults", searchResults);
            request.setAttribute("keyword", keyword);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "検索中にエラーが発生しました: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    // 商品を検索（商品名またはタグで部分一致）
    private List<Merchandise> searchMerchandisesByKeyword(Connection connection, String keyword) throws Exception {
        List<Merchandise> list = new ArrayList<>();

        String sql = "SELECT T002_PK1_merchandise, T002_FD1_merchandise, " +
                    "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
                    "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
                    "T002_FD8_merchandise, T002_FD9_merchandise " +
                    "FROM T002_merchandise " +
                    "WHERE T002_FD5_merchandise LIKE ? OR T002_FD4_merchandise LIKE ? " +
                    "ORDER BY T002_FD8_merchandise, T002_PK1_merchandise";

        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, "%" + keyword + "%");
        st.setString(2, "%" + keyword + "%");

        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
            list.add(m);
        }

        st.close();
        return list;
    }

    // 店舗を検索（店舗名で部分一致）
    private List<Store> searchStoresByKeyword(Connection connection, String keyword) throws Exception {
        List<Store> list = new ArrayList<>();

        String sql = "SELECT T001_PK1_store, T001_FD1_store, T001_FD2_store, " +
                    "T001_FD3_store, T001_FD7_store " +
                    "FROM T001_store " +
                    "WHERE T001_FD1_store LIKE ? " +
                    "ORDER BY T001_PK1_store";

        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, "%" + keyword + "%");

        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Store store = new Store();
            store.setStoreId(rs.getInt("T001_PK1_store"));
            store.setStoreName(rs.getString("T001_FD1_store"));
            store.setAddress(rs.getString("T001_FD2_store"));
            store.setPhone(rs.getString("T001_FD3_store"));
            store.setEmail(rs.getString("T001_FD7_store"));
            list.add(store);
        }

        st.close();
        return list;
    }

    // 店舗IDから店舗情報を取得
    private Store getStoreById(Connection connection, int storeId) throws Exception {
        Store store = null;

        String sql = "SELECT T001_PK1_store, T001_FD1_store, T001_FD2_store, " +
                    "T001_FD3_store, T001_FD5_store, T001_FD6_store, T001_FD7_store " +
                    "FROM T001_store " +
                    "WHERE T001_PK1_store = ?";

        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, storeId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            store = new Store();
            store.setStoreId(rs.getInt("T001_PK1_store"));
            store.setStoreName(rs.getString("T001_FD1_store"));
            store.setAddress(rs.getString("T001_FD2_store"));
            store.setPhone(rs.getString("T001_FD3_store"));
            store.setDiscountTime(rs.getTime("T001_FD5_store"));
            store.setDiscountRate(rs.getInt("T001_FD6_store"));
            store.setEmail(rs.getString("T001_FD7_store"));
        }

        st.close();
        return store;
    }

    // 店舗IDから商品リストを取得
    private List<Merchandise> getMerchandisesByStoreId(Connection connection, int storeId) throws Exception {
        List<Merchandise> list = new ArrayList<>();

        String sql = "SELECT T002_PK1_merchandise, T002_FD1_merchandise, " +
                    "T002_FD2_merchandise, T002_FD3_merchandise, T002_FD4_merchandise, " +
                    "T002_FD5_merchandise, T002_FD6_merchandise, T002_FD7_merchandise, " +
                    "T002_FD8_merchandise, T002_FD9_merchandise " +
                    "FROM T002_merchandise " +
                    "WHERE T002_FD8_merchandise = ? " +
                    "ORDER BY T002_PK1_merchandise";

        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, storeId);

        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Merchandise m = new Merchandise();
            m.setMerchandiseId(rs.getInt("T002_PK1_merchandise"));
            m.setStock(rs.getInt("T002_FD1_merchandise"));
            m.setPrice(rs.getInt("T002_FD2_merchandise"));
            m.setUseByDate(rs.getDate("T002_FD3_merchandise"));
            m.setMerchandiseTag(rs.getString("T002_FD4_merchandise"));
            m.setMerchandiseName(rs.getString("T002_FD5_merchandise"));
            m.setEmployeeId(rs.getInt("T002_FD6_merchandise"));
            m.setRegistrationTime(rs.getTimestamp("T002_FD7_merchandise"));
            m.setStoreId(rs.getInt("T002_FD8_merchandise"));
            m.setBookingStatus(rs.getBoolean("T002_FD9_merchandise"));
            list.add(m);
        }

        st.close();
        return list;
    }

    // 内部クラス: 検索結果を店舗ごとにまとめる
    public static class SearchResult {
        private Store store;
        private List<Merchandise> merchandises;

        public SearchResult() {
            this.merchandises = new ArrayList<>();
        }

        public Store getStore() {
            return store;
        }

        public void setStore(Store store) {
            this.store = store;
        }

        public int getStoreId() {
            return store != null ? store.getStoreId() : 0;
        }

        public String getStoreName() {
            return store != null ? store.getStoreName() : "";
        }

        public List<Merchandise> getMerchandises() {
            return merchandises;
        }

        public void setMerchandises(List<Merchandise> merchandises) {
            this.merchandises = merchandises;
        }

        public void addMerchandise(Merchandise merchandise) {
            this.merchandises.add(merchandise);
        }

        public boolean hasMerchandise(int merchandiseId) {
            for (Merchandise m : merchandises) {
                if (m.getMerchandiseId() == merchandiseId) {
                    return true;
                }
            }
            return false;
        }
    }
}