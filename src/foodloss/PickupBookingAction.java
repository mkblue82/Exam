package foodloss;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Merchandise;
import dao.BookingDAO;
import dao.DAO;
import dao.MerchandiseDAO;

@WebServlet("/foodloss/PickupBooking.action")
public class PickupBookingAction extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        try {
            BookingDAO bookingDAO = new BookingDAO();
            Booking booking = bookingDAO.selectById(bookingId);

            if (booking != null) {

                // ★ 受取済に変更
                booking.setPickupStatus(true);
                bookingDAO.update(booking);

                // ★ 商品取得
                DAO db = new DAO();
                MerchandiseDAO mdao = new MerchandiseDAO(db.getConnection());
                Merchandise m = mdao.selectById(booking.getProductId());

                if (m != null) {

                    // ★ 在庫を減らす
                    int newStock = m.getStock() - booking.getCount();
                    if (newStock < 0) newStock = 0;
                    m.setStock(newStock);

                    // ★ 在庫0になったら予約不可にする
                    if (newStock == 0) {
                        m.setBookingStatus(false);  // ← 予約不可
                    }

                    // ★ 更新
                    mdao.update(m);

                    // ★ メール通知（後で追加）
                    sendPickupMail(booking);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(
            request.getContextPath() + "/foodloss/StoreBookingList.action"
        );
    }

    // POST → GET
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * ★ 受け取り時のメール送信
     */
    private void sendPickupMail(Booking booking) {
        // 実装は後で説明
    }
}
