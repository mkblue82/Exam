package foodloss;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import dao.BookingDAO;

/**
 * 予約を「受取済」に更新するアクション
 */
@WebServlet("/foodloss/PickupBooking.action")
public class PickupBookingAction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ★ 店舗ログインしていない場合はログイン画面へ
        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        // ★ パラメータの予約ID取得
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        try {
            BookingDAO dao = new BookingDAO();
            Booking b = dao.selectById(bookingId);

            if (b != null) {
                // ★ ステータスを「受取済」に変更
                b.setPickupStatus(true);

                // ★ DB更新
                dao.update(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ★ 処理後は予約一覧へ戻る
        response.sendRedirect(
            request.getContextPath() + "/foodloss/StoreBookingList.action"
        );
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
