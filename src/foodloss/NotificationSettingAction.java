package foodloss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Favorite;
import bean.User;
import dao.FavoriteDAO;
import tool.Action;
import tool.DBManager;

public class NotificationSettingAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // ログインチェック
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login_user.jsp");
            return;
        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            // POST: 保存処理
            saveNotificationSettings(request, response, user);
        } else {
            // GET: 画面表示
            displayNotificationSettings(request, response, user);
        }
    }

    // 画面表示処理
    private void displayNotificationSettings(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
        try {
            DBManager db = new DBManager();
            FavoriteDAO dao = new FavoriteDAO(db.getConnection());

            // お気に入り店舗一覧を取得
            List<Favorite> favoriteStores = dao.getFavoriteStoresByUserId(user.getUserId());

            // ===== ダミーデータここまで =====

            request.setAttribute("favoriteStores", favoriteStores);
            request.getRequestDispatcher("/jsp/notification_setting.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "エラーが発生しました。");
            request.getRequestDispatcher("/jsp/notification_setting.jsp").forward(request, response);
        }
    }

    // 保存処理
    private void saveNotificationSettings(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
        try {
            DBManager db = new DBManager();
            FavoriteDAO dao = new FavoriteDAO(db.getConnection());

            // お気に入り店舗一覧を取得
            List<Favorite> favoriteStores = dao.getFavoriteStoresByUserId(user.getUserId());

            // 各店舗の通知設定を更新
            for (Favorite favorite : favoriteStores) {
                String paramName = "notification_" + favorite.getStoreId();
                String paramValue = request.getParameter(paramName);

                // チェックボックスがONの場合は"1"が送信される、OFFの場合はnull
                boolean notificationEnabled = "1".equals(paramValue);

                // DBを更新
                dao.updateNotification(user.getUserId(), favorite.getStoreId(), notificationEnabled);
            }

            // 成功メッセージを設定して画面を再表示
            request.setAttribute("successMessage", "通知設定を保存しました");
            displayNotificationSettings(request, response, user);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "保存に失敗しました。再度お試しください。");
            displayNotificationSettings(request, response, user);
        }
    }
}