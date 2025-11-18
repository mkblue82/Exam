package foodloss;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import dao.MerchandiseDAO;
import tool.Action;
import tool.DBManager;

public class MerchandiseEditAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            // セッションから店舗ID取得（必要なら）
            HttpSession session = request.getSession();
            Integer storeId = (Integer) session.getAttribute("storeId");

            // パラメータ取得
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect("error.jsp");
                return;
            }

            int merchandiseId = Integer.parseInt(idParam);

            // DB接続とデータ取得
            DBManager dbManager = new DBManager();
            try (Connection conn = dbManager.getConnection()) {
                MerchandiseDAO dao = new MerchandiseDAO(conn);
                Merchandise merchandise = dao.selectById(merchandiseId);

                if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                    response.sendRedirect("error.jsp");
                    return;
                }

                // JSPへ商品情報渡す
                request.setAttribute("merchandise", merchandise);
                request.getRequestDispatcher("/store_jsp/merchandise_edit.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
