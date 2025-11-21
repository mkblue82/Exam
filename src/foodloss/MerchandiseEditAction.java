package foodloss;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.MerchandiseImage;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import tool.Action;
import tool.DBManager;

public class MerchandiseEditAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // セッションから店舗ID取得
            HttpSession session = request.getSession();
            Integer storeId = (Integer) session.getAttribute("storeId");

            // 商品ID取得
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect("error.jsp");
                return;
            }

            int merchandiseId = Integer.parseInt(idParam);

            // DB接続とデータ取得
            try (Connection conn = DBManager.getInstance().getConnection()) {
                MerchandiseDAO dao = new MerchandiseDAO(conn);
                Merchandise merchandise = dao.selectById(merchandiseId);

                if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                    response.sendRedirect("error.jsp");
                    return;
                }

                // 画像取得
                MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(conn);
                List<MerchandiseImage> images = imageDAO.selectByMerchandiseId(merchandiseId);

                // JSPへデータ渡す
                request.setAttribute("merchandise", merchandise);
                request.setAttribute("images", images);
                request.getRequestDispatcher("/store_jsp/merchandise_edit.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
