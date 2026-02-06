package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import dao.DAO;
import dao.StoreDAO;
import tool.Action;

public class StoreEditFormAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        // デバッグ: セッションチェック
        if (session == null) {
            System.out.println("DEBUG: セッションがnullです");
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        Integer storeId = (Integer) session.getAttribute("storeId");

        // デバッstoreIdチェック
        if (storeId == null) {
            System.out.println("DEBUG: セッションにstoreIdが存在しません");
            response.sendRedirect(request.getContextPath() + "/store_jsp/login_store.jsp");
            return;
        }

        System.out.println("DEBUG: storeId = " + storeId);

        Connection con = null;

        try {
            // 店舗情報取得
            DAO db = new DAO();
            con = db.getConnection();

            // デバッグ: Connection確認
            if (con == null) {
                System.out.println("DEBUG: データベース接続に失敗しました");
                request.setAttribute("errorMessage", "データベース接続に失敗しました。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            System.out.println("DEBUG: データベース接続成功");

            StoreDAO storeDAO = new StoreDAO(con);
            Store store = storeDAO.selectById(storeId);

            // デバッグ: Store取得確認
            if (store == null) {
                System.out.println("DEBUG: 店舗情報が見つかりません (storeId=" + storeId + ")");
                request.setAttribute("errorMessage", "店舗情報が見つかりません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            System.out.println("DEBUG: 店舗情報取得成功 - " + store.getStoreName());

            request.setAttribute("store", store);
            request.getRequestDispatcher("/store_jsp/store_edit.jsp").forward(request, response);

        } catch (Exception e) {
            System.out.println("DEBUG: 例外発生 - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "エラーが発生しました: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            // データベース接続をクローズ
            if (con != null) {
                try {
                    con.close();
                    System.out.println("DEBUG: データベース接続をクローズしました");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}