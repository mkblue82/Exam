package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Merchandise;
import bean.Store;
import bean.User;
import dao.MerchandiseDAO;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;

/**
 * 予約確認画面表示Action
 * 商品詳細画面から予約ボタンを押した後、確認画面を表示する
 */
public class ReserveConfirmAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("===== ReserveConfirmAction START =====");

        // セッションチェック
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            request.setAttribute("errorMessage", "ログインが必要です。");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }
        User user = (User) session.getAttribute("user");

        // パラメータ取得
        String merchandiseIdStr = request.getParameter("merchandiseId");
        String quantityStr = request.getParameter("quantity");

        // パラメータチェック
        if (merchandiseIdStr == null || merchandiseIdStr.isEmpty()) {
            request.setAttribute("errorMessage", "商品IDが指定されていません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int merchandiseId;
        int quantity = 1; // デフォルト値

        try {
            merchandiseId = Integer.parseInt(merchandiseIdStr);
            if (quantityStr != null && !quantityStr.isEmpty()) {
                quantity = Integer.parseInt(quantityStr);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "数値の形式が正しくありません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // 数量の妥当性チェック
        if (quantity <= 0) {
            quantity = 1;
        }

        Connection con = null;

        try {
            con = new DBManager().getConnection();

            // 商品情報取得
            MerchandiseDAO merchDao = new MerchandiseDAO(con);
            Merchandise merch = merchDao.selectById(merchandiseId);

            if (merch == null) {
                request.setAttribute("errorMessage", "商品が見つかりません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 在庫チェック
            if (merch.getStock() < quantity) {
                request.setAttribute("errorMessage",
                    "在庫が不足しています。現在の在庫: " + merch.getStock() + "個");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 在庫チェック（0の場合）
            if (merch.getStock() <= 0) {
                request.setAttribute("errorMessage", "この商品は在庫切れです。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // 店舗情報取得
            Store store = null;
            if (merch.getStoreId() > 0) {
                StoreDAO storeDao = new StoreDAO(con);
                store = storeDao.selectById(merch.getStoreId());
            }

            // 合計金額計算
            int totalPrice = merch.getPrice() * quantity;

            // リクエストスコープに設定
            request.setAttribute("merchandise", merch);
            request.setAttribute("store", store);
            request.setAttribute("quantity", quantity);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("user", user);

            // 確認画面へフォワード
            request.getRequestDispatcher("/jsp/reserve_confirm.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("===== ReserveConfirmAction END =====");
    }
}