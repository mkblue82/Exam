package foodloss;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.BookingDAO;
import dao.DAO;
import dao.MerchandiseDAO;
import dao.MerchandiseImageDAO;
import tool.Action;

public class MerchandiseDeleteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            HttpSession session = request.getSession();
            Integer storeId = (Integer) session.getAttribute("storeId");

            if (storeId == null) {
                response.sendRedirect(request.getContextPath() + "/login_store.jsp");
                return;
            }

            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/foodloss/MerchandiseList.action");
                return;
            }

            int merchandiseId = Integer.parseInt(idParam);

            Connection con = new DAO().getConnection();

            // ▼ 共有トランザクションDAO
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);
            MerchandiseImageDAO imageDAO = new MerchandiseImageDAO(con);
            BookingDAO bookingDAO = new BookingDAO(con);

            // ========== 予約チェック ==========
            int bookingCount = bookingDAO.countByMerchandiseId(merchandiseId);

            if (bookingCount > 0) {
                session.setAttribute("deleteError", "この商品はすでに予約されているため削除できません。");
                response.sendRedirect(request.getContextPath() + "/foodloss/MerchandiseList.action");
                return;
            }

            // ========== 削除処理 ==========
            con.setAutoCommit(false);

            imageDAO.deleteByMerchandiseId(merchandiseId);
            merchandiseDAO.delete(merchandiseId);

            con.commit();

            response.sendRedirect(request.getContextPath() + "/foodloss/MerchandiseList.action");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
