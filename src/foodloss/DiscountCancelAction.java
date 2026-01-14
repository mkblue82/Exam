package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import tool.Action;
import tool.DBManager;

public class DiscountCancelAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションから店舗情報を取得
        HttpSession session = request.getSession(false);

        Store store = (Store) session.getAttribute("store");
        if (store == null) {
            response.sendRedirect(request.getContextPath() + "/error.jsp?error=ログインしてください");
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // データベース接続
            DBManager dbManager = new DBManager();
            con = dbManager.getConnection();

            // トランザクション開始
            con.setAutoCommit(false);

            // 店舗テーブルの割引設定をNULLに更新
            String sql = "UPDATE t001_store SET t001_fd5_store = NULL, t001_fd6_store = NULL WHERE t001_pk1_store = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, store.getStoreId());

            int result = ps.executeUpdate();

            // コミット
            con.commit();

            if (result > 0) {
                // 成功 - セッションの店舗情報も更新
                store.setDiscountTime(null);
                store.setDiscountRate(0);
                session.setAttribute("store", store);

                // 成功メッセージ付きで設定画面にリダイレクト
                session.setAttribute("successMessage", "割引設定を取り消しました");
                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp");
            } else {
                // 更新失敗
                session.setAttribute("errorMessage", "割引設定の取り消しに失敗しました");
                response.sendRedirect(request.getContextPath() + "/store_jsp/discount_setting.jsp");
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