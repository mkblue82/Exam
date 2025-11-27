package foodloss;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Booking;
import bean.Merchandise;
import bean.User;
import dao.BookingDAO;
import dao.MerchandiseDAO;
import dao.UserDAO;
import tool.Action;

public class ReserveExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションチェック
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            request.setAttribute("errorMessage", "セッションが切れています。ログインし直してください。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            // パラメータ取得
            String merchandiseIdStr = request.getParameter("merchandiseId");
            String quantityStr = request.getParameter("quantity");
            String pickupDateTime = request.getParameter("pickupDateTime");
            String pointsToUseStr = request.getParameter("pointsToUse");

            // 必須パラメータチェック
            if (merchandiseIdStr == null || quantityStr == null || pickupDateTime == null ||
                merchandiseIdStr.isEmpty() || quantityStr.isEmpty() || pickupDateTime.isEmpty()) {
                request.setAttribute("errorMessage", "必要な情報が不足しています。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // パラメータ変換
            int merchandiseId = Integer.parseInt(merchandiseIdStr);
            int quantity = Integer.parseInt(quantityStr);
            int pointsToUse = (pointsToUseStr != null && !pointsToUseStr.isEmpty()) ? Integer.parseInt(pointsToUseStr) : 0;

            // 日時のパース (YYYY-MM-DD HH:mm:ss 形式)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pickupDate = sdf.parse(pickupDateTime);
            Timestamp pickupTimestamp = new Timestamp(pickupDate.getTime());

            // 商品情報を取得
            Connection con = getConnection();
            MerchandiseDAO merchandiseDAO = new MerchandiseDAO(con);
            Merchandise merchandise = merchandiseDAO.selectById(merchandiseId);

            if (merchandise == null) {
                request.setAttribute("errorMessage", "商品が見つかりませんでした。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 在庫チェック
            if (merchandise.getStock() < quantity) {
                request.setAttribute("errorMessage", "在庫が不足しています。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 消費期限チェック
            Date useByDate = merchandise.getUseByDate();
            if (pickupDate.after(useByDate)) {
                request.setAttribute("errorMessage", "受け取り日時が消費期限を超えています。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 現在時刻より後かチェック
            Date now = new Date();
            if (pickupDate.before(now) || pickupDate.equals(now)) {
                request.setAttribute("errorMessage", "受け取り日時は現在より後の日時を指定してください。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 合計金額を計算
            int totalPrice = merchandise.getPrice() * quantity;

            // ポイント使用のバリデーション
            if (pointsToUse < 0) {
                request.setAttribute("errorMessage", "ポイント使用数が不正です。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // ユーザーの保有ポイントをチェック
            int userPoints = user.getPoint();
            if (pointsToUse > userPoints) {
                request.setAttribute("errorMessage", "保有ポイントが不足しています。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 使用ポイントが合計金額を超えないかチェック
            if (pointsToUse > totalPrice) {
                request.setAttribute("errorMessage", "使用ポイントが合計金額を超えています。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 最終支払い金額を計算
            int finalPrice = totalPrice - pointsToUse;

            // Bookingオブジェクトを作成
            Booking booking = new Booking();
            booking.setUserId(user.getUserId());
            booking.setProductId(merchandiseId);
            booking.setCount(quantity);
            booking.setPickupTime(pickupTimestamp);
            booking.setBookingTime(new Timestamp(now.getTime()));
            booking.setPickupStatus(false); // 未受取
            booking.setMerchandiseName(merchandise.getMerchandiseName());
            booking.setAmount(finalPrice);

            // 予約をデータベースに登録
            BookingDAO bookingDAO = new BookingDAO();
            boolean insertSuccess = bookingDAO.insert(booking);

            if (!insertSuccess) {
                request.setAttribute("errorMessage", "予約の登録に失敗しました。");
                con.close();
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 在庫を減らす
            int updateResult = merchandiseDAO.decreaseStock(merchandiseId, quantity);

            if (updateResult == 0) {
                // 在庫更新失敗時は予約もロールバック（本来はトランザクション処理が必要）
                con.close();
                request.setAttribute("errorMessage", "在庫の更新に失敗しました。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // ポイントを消費（使用した場合のみ）
            if (pointsToUse > 0) {
                int newPoints = userPoints - pointsToUse;
                user.setPoint(newPoints);

                // UserDAOでポイント更新
                UserDAO userDAO = new UserDAO(con);
                userDAO.update(user);

                // セッションのユーザー情報も更新
                session.setAttribute("user", user);
            }

            con.close();

            // 成功時、予約完了ページへ
            request.setAttribute("booking", booking);
            request.setAttribute("merchandise", merchandise);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("pointsUsed", pointsToUse);
            request.setAttribute("finalPrice", finalPrice);
            request.setAttribute("message", "予約が完了しました。");

            request.getRequestDispatcher("/jsp/reserve_success.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "数値の形式が正しくありません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "予約処理中にエラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}