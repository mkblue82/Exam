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
import bean.User;
import dao.BookingDAO;
import dao.DAO;
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

        System.out.println("===== 受け取り処理開始 =====");
        System.out.println("bookingId: " + bookingId);

        try {
            // ★ コネクション取得
            DAO db = new DAO();
            Connection con = db.getConnection();

            // ★ 予約情報取得
            BookingDAO bookingDAO = new BookingDAO(con);
            Booking booking = bookingDAO.selectById(bookingId);

            System.out.println("booking取得: " + (booking != null ? "成功" : "失敗"));
            if (booking != null) {
                System.out.println("booking.getAmount(): " + booking.getAmount());
                System.out.println("booking.getPickupStatus(): " + booking.getPickupStatus());
                System.out.println("booking.getUserId(): " + booking.getUserId());
            }

            if (booking != null && !booking.getPickupStatus()) {

                // ★ 受取済に変更
            	bookingDAO.updatePickupStatusWithTime(bookingId);
                System.out.println("受取ステータス + 受取時刻 更新: 完了");

                // ★ 在庫処理は削除（予約時に既に減らしているため）

                // ★ ポイント付与（200円につき1ポイント）
                int earnedPoints = booking.getAmount() / 200;

                System.out.println("===== ポイント計算 =====");
                System.out.println("金額: " + booking.getAmount());
                System.out.println("計算式: " + booking.getAmount() + " / 200 = " + earnedPoints);
                System.out.println("========================");

                if (earnedPoints > 0) {
                    System.out.println("ポイント付与処理開始...");

                    UserDAO userDAO = new UserDAO(con);
                    User user = userDAO.findById(booking.getUserId());

                    System.out.println("ユーザー取得: " + (user != null ? "成功" : "失敗"));

                    if (user != null) {
                        int oldPoints = user.getPoint();
                        int newPoints = oldPoints + earnedPoints;
                        user.setPoint(newPoints);

                        System.out.println("===== ポイント更新前 =====");
                        System.out.println("ユーザーID: " + user.getUserId());
                        System.out.println("ユーザー名: " + user.getName());
                        System.out.println("現在のポイント: " + oldPoints + "pt");
                        System.out.println("付与ポイント: " + earnedPoints + "pt");
                        System.out.println("新しいポイント: " + newPoints + "pt");
                        System.out.println("==========================");

                        userDAO.update(user);

                        System.out.println("===== ポイント更新後 =====");
                        System.out.println("update()実行完了");

                        // 更新確認
                        User updatedUser = userDAO.findById(booking.getUserId());
                        if (updatedUser != null) {
                            System.out.println("DBから再取得したポイント: " + updatedUser.getPoint() + "pt");
                        }
                        System.out.println("==========================");
                    } else {
                        System.out.println("エラー: ユーザーが見つかりません");
                    }
                } else {
                    System.out.println("ポイント付与なし（200円未満）");
                }



            } else {
                System.out.println("処理スキップ: 予約が存在しないか、既に受取済み");
            }

            con.close();
            System.out.println("===== 受け取り処理終了 =====");

        } catch (Exception e) {
            System.out.println("===== エラー発生 =====");
            e.printStackTrace();
            System.out.println("======================");
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


}