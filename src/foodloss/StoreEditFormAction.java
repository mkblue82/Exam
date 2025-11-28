package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.DAO;
import dao.StoreDAO;
import tool.Action;

public class StoreEditFormAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("storeId") == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        Integer storeId = (Integer) session.getAttribute("storeId");

        // 店舗情報取得
        DAO db = new DAO();
        Connection con = db.getConnection();
        StoreDAO storeDAO = new StoreDAO(con);
        Store store = storeDAO.selectById(storeId);
        con.close();

        if (store == null) {
            request.setAttribute("errorMessage", "店舗情報が見つかりません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        request.setAttribute("store", store);
        request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);
    }
}