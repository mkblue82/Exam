package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import bean.Merchandise;
import dao.FavoriteDAO;
import dao.MerchandiseDAO;
import tool.Action;

public class MerchandiseListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        Integer storeId = (Integer) session.getAttribute("storeId");

        // ログインしてない場合
        if (storeId == null) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // JNDIでDB接続
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:/comp/env/jdbc/postgres");
            con = ds.getConnection();

            // DAO
            MerchandiseDAO dao = new MerchandiseDAO(con);
            List<Merchandise> list = dao.selectByStoreId(storeId);

            // 店舗の割引設定と店舗名を取得
            String sql = "SELECT t001_fd1_store, t001_fd5_store, t001_fd6_store FROM t001_store WHERE t001_pk1_store = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, storeId);
            rs = ps.executeQuery();

            String storeName = null;
            Time discountStartTime = null;
            Integer discountRate = null;

            if (rs.next()) {
                storeName = rs.getString("t001_fd1_store");
                discountStartTime = rs.getTime("t001_fd5_store");
                String discountRateStr = rs.getString("t001_fd6_store");

                if (discountRateStr != null && !discountRateStr.trim().isEmpty()) {
                    try {
                        discountRate = Integer.parseInt(discountRateStr.trim());
                    } catch (NumberFormatException e) {
                        // 割引率が数値でない場合は無視
                    }
                }
            }

            // 現在時刻を取得
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();

            // 割引が適用されるかチェック
            boolean isDiscountApplied = false;
            if (discountStartTime != null && discountRate != null && discountRate > 0) {
                LocalTime discountTime = discountStartTime.toLocalTime();
                // 現在時刻が割引開始時刻以降なら割引適用
                if (now.isAfter(discountTime) || now.equals(discountTime)) {
                    isDiscountApplied = true;
                }
            }

            // ★★★ 値引き通知メール送信処理 ★★★
            if (isDiscountApplied && discountRate != null) {
                // セッションから前回の通知状態を取得
                String lastNotifiedDateKey = "discountNotified_" + storeId;
                LocalDate lastNotifiedDate = (LocalDate) session.getAttribute(lastNotifiedDateKey);

                // 今日まだ通知していない場合のみ送信
                if (lastNotifiedDate == null || !lastNotifiedDate.equals(today)) {
                    try {
                        System.out.println("★ 値引き開始 - メール通知を送信します");

                        FavoriteDAO favoriteDao = new FavoriteDAO(con);
                        List<String> notificationEmails = favoriteDao.getNotificationEnabledEmails(storeId);

                        if (!notificationEmails.isEmpty()) {
                            System.out.println("★ 通知対象メールアドレス数: " + notificationEmails.size());

                            // 店舗名がない場合のデフォルト
                            String storeNameForEmail = (storeName != null && !storeName.trim().isEmpty())
                                                     ? storeName
                                                     : "お気に入り店舗";

                            // メール送信
                            EmailUtility.sendDiscountNotification(
                                notificationEmails,
                                storeNameForEmail,
                                discountRate,
                                discountStartTime
                            );

                            System.out.println("✅ 値引き通知メール送信完了: " + notificationEmails.size() + "件");

                            // 今日通知済みとしてセッションに保存
                            session.setAttribute(lastNotifiedDateKey, today);
                        } else {
                            System.out.println("★ 通知対象ユーザーなし");
                        }
                    } catch (Exception emailEx) {
                        // メール送信エラーは表示処理に影響させない
                        System.err.println("❌ メール送信エラー: " + emailEx.getMessage());
                        emailEx.printStackTrace();
                    }
                } else {
                    System.out.println("★ 本日は既に通知済み");
                }
            }
            // ★★★ メール通知処理ここまで ★★★

            // 割引情報をリクエストに設定
            request.setAttribute("isDiscountApplied", isDiscountApplied);
            if (isDiscountApplied) {
                request.setAttribute("discountRate", discountRate);
            }

            // JSPに渡す
            request.setAttribute("merchandiseList", list);

        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }

        // 遷移先 JSP
        request.getRequestDispatcher("/store_jsp/merchandise_list.jsp")
               .forward(request, response);
    }
}