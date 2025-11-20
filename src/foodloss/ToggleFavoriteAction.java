package foodloss;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.DAO;
import dao.FavoriteDAO;
import tool.Action;

public class ToggleFavoriteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // セッションからユーザー情報を取得
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            // パラメータ取得
            String storeIdStr = request.getParameter("storeId");
            String action = request.getParameter("action");

            if (storeIdStr == null || action == null) {
                out.print("{\"success\": false, \"message\": \"パラメータが不足しています\"}");
                return;
            }

            int storeId = Integer.parseInt(storeIdStr);
            int userId = user.getUserId();

            // データベース接続
            DAO dao = new DAO();
            Connection con = dao.getConnection();

            try {
                FavoriteDAO favoriteDao = new FavoriteDAO(con);

                if ("add".equals(action)) {
                    // お気に入り追加
                    favoriteDao.addFavorite(userId, storeId);
                    out.print("{\"success\": true, \"message\": \"お気に入りに追加しました\"}");

                } else if ("remove".equals(action)) {
                    // お気に入り削除
                    favoriteDao.removeFavorite(userId, storeId);
                    out.print("{\"success\": true, \"message\": \"お気に入りから削除しました\"}");

                } else {
                    out.print("{\"success\": false, \"message\": \"不正なアクションです\"}");
                }

            } finally {
                if (con != null) {
                    con.close();
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"店舗IDの形式が正しくありません\"}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"エラーが発生しました\"}");
        }
    }
}