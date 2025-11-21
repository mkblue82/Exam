package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import tool.Action;
import tool.DBManager;

public class DiscountSettingAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションから店舗情報を取得
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/error.jsp?error=セッションが切れています");
            return;
        }

        Store store = (Store) session.getAttribute("store");
        if (store == null) {
            response.sendRedirect(request.getContextPath() + "/error.jsp?error=ログインしてください");
            return;
        }

        // パラメータ取得
        String timeStr = request.getParameter("time");
        String discountStr = request.getParameter("discount");

        // バリデーション
        if (timeStr == null || discountStr == null ||
            timeStr.isEmpty() || discountStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?error=入力項目が不足しています");
            return;
        }

        int startTime = 0;
        int discountRate = 0;

        try {
            startTime = Integer.parseInt(timeStr);
            discountRate = Integer.parseInt(discountStr);

            // 範囲チェック
            if (startTime < 0 || startTime > 23) {
                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?error=時間は0〜23の範囲で入力してください");
                return;
            }
            if (discountRate < 1 || discountRate > 100) {
                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?error=割引率は1〜100の範囲で入力してください");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?error=数値を正しく入力してください");
            return;
        }

        // 時間をTime型に変換（例: 18時 → 18:00:00）
        Time discountStartTime = Time.valueOf(String.format("%02d:00:00", startTime));

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // データベース接続
            DBManager dbManager = new DBManager();
            con = dbManager.getConnection();

            // トランザクション開始
            con.setAutoCommit(false);

            // 店舗テーブルの割引設定を更新
            String sql = "UPDATE t001_store SET t001_fd5_store = ?, t001_fd6_store = ? WHERE t001_pk1_store = ?";
            ps = con.prepareStatement(sql);
            ps.setTime(1, discountStartTime);
            ps.setString(2, String.valueOf(discountRate)); // character型なのでStringで設定
            ps.setInt(3, store.getStoreId()); // 店舗IDを設定

            int result = ps.executeUpdate();

            // コミット
            con.commit();

            if (result > 0) {
                // 成功 - セッションの店舗情報も更新
                store.setDiscountTime(discountStartTime);
                store.setDiscountRate(discountRate);
                session.setAttribute("store", store);

                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?success=true");
            } else {
                // 更新失敗
                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp?error=割引設定の更新に失敗しました");
            }

        } catch (SQLException e) {
            // エラー時はロールバック
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp?error=データベースエラーが発生しました");

        } finally {
            // リソース解放
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}