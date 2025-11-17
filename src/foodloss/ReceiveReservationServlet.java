package foodloss;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Store;
import dao.BookingDAO;

@WebServlet("/receive_reservation")
public class ReceiveReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession();

        // ログインチェック（店舗側のみアクセス可能）
        Store store = (Store) session.getAttribute("store");
        if (store == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/store_login.jsp");
            return;
        }

        // CSRFトークンチェック（オプション）
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            request.setAttribute("errorMessage", "不正なリクエストです。");
            request.getRequestDispatcher("/reservation_list").forward(request, response);
            return;
        }

        try {
            // 予約IDを取得
            String reservationIdStr = request.getParameter("reservationId");

            if (reservationIdStr == null || reservationIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "予約IDが指定されていません。");
                request.getRequestDispatcher("/reservation_list").forward(request, response);
                return;
            }

            int reservationId = Integer.parseInt(reservationIdStr);

            BookingDAO bookingDAO = new BookingDAO();

            // 予約を取得
            Booking booking = bookingDAO.selectById(reservationId);

            if (booking == null) {
                request.setAttribute("errorMessage", "指定された予約が見つかりません。");
                request.getRequestDispatcher("/reservation_list").forward(request, response);
                return;
            }

            // 受け取りステータスをtrueに更新
            booking.setPickupStatus(true);
            int result = bookingDAO.update(booking);

            if (result > 0) {
                session.setAttribute("successMessage", "予約を受け取り済みにしました。");
            } else {
                request.setAttribute("errorMessage", "予約の更新に失敗しました。");
            }

            // 予約リストページにリダイレクト
            response.sendRedirect(request.getContextPath() + "/reservation_list");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "予約IDの形式が不正です。");
            request.getRequestDispatcher("/reservation_list").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "予約の受け取り処理中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/reservation_list").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GETリクエストは受け付けない
        response.sendRedirect(request.getContextPath() + "/reservation_list");
    }
}