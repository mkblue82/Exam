package foodloss;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int ITEMS_PER_PAGE = 10; // 1ページあたりの表示件数

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // セッションからログインユーザー情報を取得
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 現在のページ番号を取得（デフォルトは1ページ目）
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // DAOのインスタンス化
        DAO dao = new DAO();
        BookingDAO bookingDao = new BookingDAO();
        MerchandiseDAO merchandiseDao = new MerchandiseDAO(dao.getConnection());
        StoreDAO storeDao = new StoreDAO(dao.getConnection());

        // ユーザーIDで予約一覧を取得
        List<Booking> allBookingList = bookingDao.selectByUserId(user.getUserId());

        // 予約が存在するか確認
        if (allBookingList == null || allBookingList.isEmpty()) {
            // 予約が存在しない場合
            request.setAttribute("message", "予約商品が存在しません");
            request.setAttribute("bookingList", new ArrayList<Booking>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
        } else {
            // 予約情報に商品名・店舗名を追加
            for (Booking booking : allBookingList) {
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

                // デバッグ用ログ（予約の金額を確認）
                System.out.println("BookingID: " + booking.getBookingId() +
                                   ", Amount: " + booking.getAmount() +
                                   ", ProductName: " + booking.getMerchandiseName());
            }

            // 受取状態でソート（未受取が上、受取済が下）
            Collections.sort(allBookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking b1, Booking b2) {
                    // false(未受取)が先、true(受取済)が後
                    return Boolean.compare(b1.getPickupStatus(), b2.getPickupStatus());
                }
            });

            // 総件数と総ページ数を計算
            int totalItems = allBookingList.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

            // 現在のページが総ページ数を超えている場合は最終ページに修正
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            // 表示する範囲を計算
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

            // 現在のページに表示する予約リストを取得
            List<Booking> bookingList = allBookingList.subList(startIndex, endIndex);

            // リクエストに設定
            request.setAttribute("bookingList", bookingList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
        }

        // 予約参照画面（JSP）へフォワード
        request.getRequestDispatcher("/jsp/booking_list_user.jsp").forward(request, response);
    }
}