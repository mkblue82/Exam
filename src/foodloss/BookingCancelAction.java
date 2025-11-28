package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Merchandise;
import bean.User;
import dao.BookingDAO;
import dao.DAO;
import dao.MerchandiseDAO;
import tool.Action;

public class BookingCancelAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        System.out.println("=== BookingCancelAction START ===");

        // セッションからログインユーザー情報を取得
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("User: " + (user != null ? user.getUserId() : "null"));

        // ログインチェック
        if (user == null) {
            System.out.println("User not logged in, redirecting to login");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        // パラメータから予約IDを取得
        String bookingIdStr = request.getParameter("bookingId");
        String confirm = request.getParameter("confirm");

        System.out.println("bookingId: " + bookingIdStr);
        System.out.println("confirm: " + confirm);

        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            System.out.println("ERROR: bookingId is null or empty");
            request.setAttribute("error", "予約IDが指定されていません");
            request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
            return;
        }

        int bookingId = Integer.parseInt(bookingIdStr);
        System.out.println("Parsed bookingId: " + bookingId);

        Connection con = null;
        try {
            // DAOのインスタンス化
            DAO dao = new DAO();
            con = dao.getConnection();
            System.out.println("Connection established");

            BookingDAO bookingDao = new BookingDAO();
            MerchandiseDAO merchandiseDao = new MerchandiseDAO(con);

            // 予約情報を取得
            System.out.println("Fetching booking...");
            Booking booking = bookingDao.selectById(bookingId);
            System.out.println("Booking found: " + (booking != null));

            if (booking != null) {
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("User ID: " + booking.getUserId());
                System.out.println("Product ID: " + booking.getProductId());
            }

            // 予約が存在しない場合
            if (booking == null) {
                System.out.println("ERROR: Booking not found");
                request.setAttribute("error", "指定された予約が見つかりません");
                request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
                return;
            }

            // ログインユーザーの予約かチェック
            if (booking.getUserId() != user.getUserId()) {
                System.out.println("ERROR: User mismatch");
                request.setAttribute("error", "他のユーザーの予約は取り消せません");
                request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
                return;
            }

            // 既に受取済みの場合
            if (booking.getPickupStatus()) {
                System.out.println("ERROR: Already picked up");
                request.setAttribute("error", "受取済みの予約は取り消せません");
                request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
                return;
            }

            // 商品情報を取得して商品名と価格をセット
            System.out.println("Fetching merchandise...");
            Merchandise merchandise = merchandiseDao.selectById(booking.getProductId());
            if (merchandise != null) {
                System.out.println("Merchandise found: " + merchandise.getMerchandiseName());
                booking.setMerchandiseName(merchandise.getMerchandiseName());

                // 価格情報を取得して設定
                int price = merchandise.getPrice();
                System.out.println("Merchandise price (int): " + price);
                request.setAttribute("price", Integer.valueOf(price));
                System.out.println("Price attribute set: " + request.getAttribute("price"));
            } else {
                System.out.println("WARNING: Merchandise not found");
            }

            // 確認画面の表示（confirmパラメータがない場合）
            if (confirm == null) {
                System.out.println("Showing confirm page");
                request.setAttribute("booking", booking);
                System.out.println("Booking attribute set: " + (request.getAttribute("booking") != null));
                System.out.println("Price in request before forward: " + request.getAttribute("price"));
                request.getRequestDispatcher("/jsp/booking_cancel.jsp").forward(request, response);
                return;
            }

            // 実際の削除処理（confirmパラメータがある場合）
            System.out.println("Deleting booking...");
            int result = bookingDao.delete(bookingId);

            if (result > 0) {
                System.out.println("Delete successful");
                // 削除成功 - 完了画面へ（価格情報も渡す）
                request.setAttribute("bookingId", bookingId);
                request.setAttribute("booking", booking);
                // 価格情報を再取得して設定（削除前の情報を表示するため）
                Merchandise merchandiseForDone = merchandiseDao.selectById(booking.getProductId());
                if (merchandiseForDone != null) {
                    request.setAttribute("price", merchandiseForDone.getPrice());
                }
                request.getRequestDispatcher("/jsp/booking_cancel_done.jsp").forward(request, response);
            } else {
                System.out.println("ERROR: Delete failed");
                // 削除失敗
                request.setAttribute("error", "予約の取り消しに失敗しました");
                request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
            }

        } catch (NumberFormatException e) {
            System.out.println("ERROR: NumberFormatException - " + e.getMessage());
            request.setAttribute("error", "予約IDの形式が不正です");
            request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
        } catch (Exception e) {
            System.out.println("ERROR: Exception - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "予約の取り消し中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/foodloss/BookingUserList.action").forward(request, response);
        } finally {
            if (con != null) {
                con.close();
                System.out.println("Connection closed");
            }
        }

        System.out.println("=== BookingCancelAction END ===");
    }
}