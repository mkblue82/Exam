package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Store;
import dao.DAO;
import dao.StoreDAO;
import tool.Action;

public class StoreInfoAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // パラメータから店舗IDを取得
        String storeIdStr = request.getParameter("storeId");

        if (storeIdStr == null || storeIdStr.isEmpty()) {
            request.setAttribute("errorMessage", "店舗IDが指定されていません。");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
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
                request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
                return;
            }

            // お気に入り状態を確認（後で実装）
            // TODO: FavoriteDAOを使ってお気に入り状態を確認
            boolean isFavorite = false; // 暫定的にfalse

            // リクエストスコープに設定
            request.setAttribute("store", store);
            request.setAttribute("isFavorite", isFavorite);

            request.getRequestDispatcher("/store_jsp/store_info.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "店舗IDの形式が正しくありません。");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "店舗情報の取得中にエラーが発生しました。");
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
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