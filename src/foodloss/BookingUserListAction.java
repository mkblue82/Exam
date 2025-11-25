package foodloss;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Merchandise;
import bean.Store;
import bean.User;
import dao.BookingDAO;
import dao.DAO;
import dao.MerchandiseDAO;
import dao.StoreDAO;
import tool.Action;

public class BookingUserListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションからログインユーザー情報を取得
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // ログインチェック
        if (user == null) {
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // DAOのインスタンス化
        DAO dao = new DAO();
        BookingDAO bookingDao = new BookingDAO();
        MerchandiseDAO merchandiseDao = new MerchandiseDAO(dao.getConnection());
        StoreDAO storeDao = new StoreDAO(dao.getConnection());

        // ユーザーIDで予約一覧を取得
        List<Booking> bookingList = bookingDao.selectByUserId(user.getUserId());

        // 予約が存在するか確認
        if (bookingList == null || bookingList.isEmpty()) {
            // 予約が存在しない場合
            request.setAttribute("message", "予約商品が存在しません");
            request.setAttribute("bookingList", new ArrayList<Booking>());
        } else {
            // 予約情報に商品名・店舗名を追加
            for (Booking booking : bookingList) {
                // 商品情報を取得
                Merchandise merchandise = merchandiseDao.selectById(booking.getProductId());
                if (merchandise != null) {
                    booking.setMerchandiseName(merchandise.getMerchandiseName());

                    // 店舗情報を取得
                    Store store = storeDao.selectById(merchandise.getStoreId());
                    if (store != null) {
                        request.setAttribute("store_" + booking.getBookingId(), store.getStoreName());
                    }
                }
            }
            request.setAttribute("bookingList", bookingList);
        }

        // 予約参照画面（JSP）へフォワード
        request.getRequestDispatcher("/jsp/booking_list_user.jsp").forward(request, response);
    }
}