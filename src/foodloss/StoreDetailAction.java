package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.DAO;
import dao.StoreDAO;
import tool.Action;

public class StoreDetailAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションから店舗情報を取得
        HttpSession session = request.getSession();
        Store sessionStore = (Store) session.getAttribute("store");

        Connection con = null;
        try {
            // データベースから最新の店舗情報を取得
            DAO dao = new DAO();
            con = dao.getConnection();
            StoreDAO storeDao = new StoreDAO(con);

            Store store = storeDao.selectById(sessionStore.getStoreId());

            if (store == null) {
                request.setAttribute("errorMessage", "店舗情報が見つかりません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // リクエストスコープに設定
            request.setAttribute("store", store);

            // JSPに転送
            request.getRequestDispatcher("/store_jsp/store_detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "店舗情報の取得中にエラーが発生しました。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}