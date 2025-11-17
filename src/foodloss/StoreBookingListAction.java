package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import dao.BookingDAO;
import tool.Action;

public class StoreBookingListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // セッションから店舗ID取得
        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ログインしていない場合はログイン画面へ
        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        // 店舗IDに紐づく予約一覧を取得
        BookingDAO dao = new BookingDAO();
        List<Booking> bookingList = dao.selectByStoreId(storeId);

        // JSP に詰めてフォワード
        request.setAttribute("bookingList", bookingList);
        request.getRequestDispatcher("/store_jsp/booking_list_store.jsp")
               .forward(request, response);
    }
}
