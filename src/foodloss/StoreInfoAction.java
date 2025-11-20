package foodloss;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Store;
import bean.User;
import dao.DAO;
import dao.FavoriteDAO;
import dao.StoreDAO;
import tool.Action;

public class StoreInfoAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // セッションからユーザー情報を取得（ログイン済み前提）
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // パラメータから店舗IDを取得
        String storeIdStr = request.getParameter("storeId");
        if (storeIdStr == null || storeIdStr.isEmpty()) {
            request.setAttribute("errorMessage", "店舗IDが指定されていません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        Connection con = null;
        try {
            int storeId = Integer.parseInt(storeIdStr);

            // データベース接続を取得
            DAO dao = new DAO();
            con = dao.getConnection();

            // 店舗情報を取得
            StoreDAO storeDao = new StoreDAO(con);
            Store store = storeDao.selectById(storeId);

            if (store == null) {
                request.setAttribute("errorMessage", "指定された店舗が見つかりません。");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            // お気に入り状態を確認
            FavoriteDAO favoriteDao = new FavoriteDAO(con);
            boolean isFavorite = favoriteDao.isFavorite(user.getUserId(), storeId);

            // リクエストスコープに設定
            request.setAttribute("store", store);
            request.setAttribute("isFavorite", isFavorite);

            // JSPに転送
            request.getRequestDispatcher("/jsp/store_info.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "店舗IDの形式が正しくありません。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "店舗情報の取得中にエラーが発生しました。");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            // データベース接続をクローズ
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}