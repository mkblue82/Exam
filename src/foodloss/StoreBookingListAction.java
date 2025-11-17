package foodloss;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import dao.BookingDAO;

/**
 * 店舗側 予約一覧表示アクション
 * ログイン中の店舗IDに紐づく予約を取得して JSP に渡す
 */
@WebServlet("/foodloss/StoreBookingList.action")
public class StoreBookingListAction extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        BookingDAO dao = new BookingDAO();
        List<Booking> bookingList = null;

        try {
            bookingList = dao.selectByStoreId(storeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("bookingList", bookingList);
        request.getRequestDispatcher("/store_jsp/booking_list_store.jsp")
               .forward(request, response);
    }
}
