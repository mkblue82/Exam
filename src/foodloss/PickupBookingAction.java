package foodloss;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Merchandise;
import bean.User;
import dao.BookingDAO;
import dao.DAO;
import dao.MerchandiseDAO;
import dao.UserDAO;

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
            // ★ コネクション取得（トランザクション用）
            DAO db = new DAO();
            Connection con = db.getConnection();

            // ★ 予約情報取得
            BookingDAO bookingDAO = new BookingDAO(con);
            Booking booking = bookingDAO.selectById(bookingId);

            if (booking != null && !booking.isPickupStatus()) {  // まだ受け取られていない場合のみ

                // ★ 受取済に変更
                booking.setPickupStatus(true);
                bookingDAO.update(booking);

                // ★ 商品情報取得
                MerchandiseDAO mdao = new MerchandiseDAO(con);
                Merchandise m = mdao.selectById(booking.getProductId());

                if (m != null) {
                    // ★ 在庫を減らす
                    int newStock = m.getStock() - booking.getCount();
                    if (newStock < 0) newStock = 0;
                    m.setStock(newStock);

                    // ★ 在庫0になったら予約不可にする
                    if (newStock == 0) {
                        m.setBookingStatus(false);
                    }

                    mdao.update(m);
                }

                // ★ ポイント付与（200円につき1ポイント）
                int earnedPoints = booking.getAmount() / 200;

                if (earnedPoints > 0) {
                    UserDAO userDAO = new UserDAO(con);
                    User user = userDAO.selectById(booking.getUserId());

                    if (user != null) {
                        int newPoints = user.getPoint() + earnedPoints;
                        user.setPoint(newPoints);
                        userDAO.update(user);

                        // デバッグ用ログ
                        System.out.println("===== ポイント付与 =====");
                        System.out.println("ユーザーID: " + user.getUserId());
                        System.out.println("支払い金額: ¥" + booking.getAmount());
                        System.out.println("付与ポイント: " + earnedPoints + "pt");
                        System.out.println("更新後ポイント: " + newPoints + "pt");
                        System.out.println("========================");
                    }
                }

                // ★ メール通知（後で追加）
                sendPickupMail(booking, earnedPoints);
            }

            con.close();

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
    private void sendPickupMail(Booking booking, int earnedPoints) {
        // 実装は後で
        // メール本文に「〇〇ポイント獲得しました」と追加予定
    }
}