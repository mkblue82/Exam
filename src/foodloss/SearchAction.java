package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import tool.Action;
import tool.DBManager;

public class SearchAction extends Action {

    @Override
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            request.setAttribute("searchResults", null);
            request.setAttribute("resultCount", 0);
            return mapping.findForward("success");
        }

        keyword = keyword.trim();
        String pattern = "%" + keyword + "%";

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con = DBManager.getConnection();

            String sql =
                "SELECT T002_PK1_merchandise, T002_FD2_merchandise, " +
                "T002_FD4_merchandise, T002_FD5_merchandise, " +
                "T002_FD8_merchandise, T001_FD1_store " +
                "FROM T002_merchandise " +
                "JOIN T001_store ON T002_FD8_merchandise = T001_PK1_store " +
                "WHERE T002_FD5_merchandise LIKE ? OR T002_FD4_merchandise LIKE ? " +
                "ORDER BY T002_FD8_merchandise, T002_PK1_merchandise";

            st = con.prepareStatement(sql);
            st.setString(1, pattern);
            st.setString(2, pattern);
            rs = st.executeQuery();

            List<Map<String, Object>> results = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("merchandiseId", rs.getInt("T002_PK1_merchandise"));
                item.put("price", rs.getInt("T002_FD2_merchandise"));
                item.put("merchandiseTag", rs.getString("T002_FD4_merchandise"));
                item.put("merchandiseName", rs.getString("T002_FD5_merchandise"));
                item.put("storeId", rs.getInt("T002_FD8_merchandise"));
                item.put("storeName", rs.getString("T001_FD1_store"));
                results.add(item);
            }

            request.setAttribute("searchResults", results);
            request.setAttribute("resultCount", results.size());
            request.setAttribute("keyword", keyword);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "検索中にエラーが発生しました");
        } finally {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();
        }

        return mapping.findForward("success");
    }
}