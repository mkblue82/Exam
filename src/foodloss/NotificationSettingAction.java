package foodloss;

import java.util.ArrayList;
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

            // ===== ダミーデータ（画面確認用） =====
            // 実際の運用時はこのブロックを削除してください
            if (favoriteStores == null || favoriteStores.isEmpty()) {
                favoriteStores = new ArrayList<>();

                Favorite store1 = new Favorite();
                store1.setFavoriteStoreId(1);
                store1.setUserId(user.getUserId());
                store1.setStoreId(1);
                store1.setStoreName("ベーカリー山田");
                store1.setStoreAddress("東京都渋谷区代々木1-2-3");
                store1.setNotificationSetting(true);

                Favorite store2 = new Favorite();
                store2.setFavoriteStoreId(2);
                store2.setUserId(user.getUserId());
                store2.setStoreId(2);
                store2.setStoreName("カフェ＆レストラン グリーン");
                store2.setStoreAddress("東京都新宿区新宿3-10-5");
                store2.setNotificationSetting(true);

                Favorite store3 = new Favorite();
                store3.setFavoriteStoreId(3);
                store3.setUserId(user.getUserId());
                store3.setStoreId(3);
                store3.setStoreName("スーパーマーケット田中");
                store3.setStoreAddress("東京都世田谷区三軒茶屋2-15-8");
                store3.setNotificationSetting(false);

                Favorite store4 = new Favorite();
                store4.setFavoriteStoreId(4);
                store4.setUserId(user.getUserId());
                store4.setStoreId(4);
                store4.setStoreName("デリカテッセン佐藤");
                store4.setStoreAddress("東京都港区六本木7-18-12");
                store4.setNotificationSetting(true);

                Favorite store5 = new Favorite();
                store5.setFavoriteStoreId(5);
                store5.setUserId(user.getUserId());
                store5.setStoreId(5);
                store5.setStoreName("和食処 鈴木");
                store5.setStoreAddress("東京都中央区銀座4-5-6");
                store5.setNotificationSetting(false);

                favoriteStores.add(store1);
                favoriteStores.add(store2);
                favoriteStores.add(store3);
                favoriteStores.add(store4);
                favoriteStores.add(store5);
            }
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