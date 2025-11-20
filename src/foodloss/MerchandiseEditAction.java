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
            HttpSession session = request.getSession();
            Integer storeId = (Integer) session.getAttribute("storeId");
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                request.setAttribute("errorMessage", "商品IDが指定されていません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            int merchandiseId = Integer.parseInt(idParam);
            DBManager dbManager = new DBManager();
            try (Connection conn = dbManager.getConnection()) {
                MerchandiseDAO dao = new MerchandiseDAO(conn);
                Merchandise merchandise = dao.selectById(merchandiseId);
                if (merchandise == null || (storeId != null && merchandise.getStoreId() != storeId)) {
                    request.setAttribute("errorMessage", "商品が見つからないか、アクセス権限がありません。");
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
                // ★ 商品画像も取得してJSPへ渡す
                MerchandiseImageDAO imageDao = new MerchandiseImageDAO(conn);
                List<MerchandiseImage> images = imageDao.selectByMerchandiseId(merchandiseId);
                request.setAttribute("images", images);
                // 商品情報をJSPへ渡す
                request.setAttribute("merchandise", merchandise);
                request.getRequestDispatcher("/store_jsp/merchandise_edit.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。");
            try {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}