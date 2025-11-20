package foodloss;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Application;
import bean.Store;
import dao.ApplicationDAO;
import dao.StoreDAO;
import tool.Action;
import tool.MailSender;

public class ApproveStoreAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        System.out.println("DEBUG: ========== ApproveStoreAction START ==========");

        req.setCharacterEncoding("UTF-8");

        // --- トークン取得 ---
        String token = req.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            showError(req, res, "無効なリンクです。");
            return;
        }

        System.out.println("DEBUG: Approval token = " + token);

        Connection conn = null;

        try {
            conn = getConnection();
            ApplicationDAO appDAO = new ApplicationDAO(conn);
            StoreDAO storeDAO = new StoreDAO(conn);

            // --- トークンで申請データを取得 ---
            Application app = appDAO.selectByToken(token);

            if (app == null) {
                showError(req, res, "申請データが見つかりません。\n既に承認済みか、無効なトークンです。");
                return;
            }

            // --- 既に承認済みかチェック ---
            if ("approved".equals(app.getStatus())) {
                showError(req, res, "この申請は既に承認済みです。");
                return;
            }

            System.out.println("DEBUG: Application found - " + app.getStoreName());

            // --- 重複チェック ---
            // Storeテーブル
            if (isPhoneExistsInStore(storeDAO, app.getStorePhone())) {
                showError(req, res, "この電話番号は既に登録されています。\n申請を却下してください。");
                return;
            }
            if (isEmailExistsInStore(storeDAO, app.getStoreEmail())) {
                showError(req, res, "このメールアドレスは既に登録されています。\n申請を却下してください。");
                return;
            }

            // Applicationテーブル（pendingの申請）
            if (isPhoneExistsInPending(appDAO, app.getStorePhone(), app.getApplicationId())) {
                showError(req, res, "同じ電話番号で他の未承認申請があります。\n申請を却下してください。");
                return;
            }
            if (isEmailExistsInPending(appDAO, app.getStoreEmail(), app.getApplicationId())) {
                showError(req, res, "同じメールアドレスで他の未承認申請があります。\n申請を却下してください。");
                return;
            }

            // --- Storeオブジェクト作成 ---
            Store store = new Store();
            store.setStoreName(app.getStoreName());
            store.setAddress(app.getStoreAddress());
            store.setPhone(app.getStorePhone());
            store.setEmail(app.getStoreEmail());
            store.setPassword(app.getPasswordHash());
            store.setLicense(app.getBusinessLicense());

            // --- DB登録 ---
            storeDAO.insert(store);
            System.out.println("DEBUG: Store registered - ID: " + store.getStoreId());

            // --- 申請ステータスを更新 ---
            appDAO.updateStatus(app.getApplicationId(), "approved");
            System.out.println("DEBUG: Application status updated to 'approved'");

            // --- 店舗への完了メール送信 ---
            try {
                String loginUrl = req.getScheme() + "://" + req.getServerName() +
                                ":" + req.getServerPort() + req.getContextPath() +
                                "/foodloss/LoginStore.action";

                String completionBody = app.getStoreName() + " 様\n\n" +
                                       "店舗登録の審査が完了しました。\n" +
                                       "以下の情報でログインできます。\n\n" +
                                       "━━━━━━━━━━━━━━━━━━\n" +
                                       "店舗ID: " + store.getStoreId() + "\n" +
                                       "ログインURL: " + loginUrl + "\n" +
                                       "━━━━━━━━━━━━━━━━━━\n\n" +
                                       "※パスワードは申請時に設定したものをご使用ください。\n\n" +
                                       "ご利用ありがとうございます。";

                MailSender.sendEmail(app.getStoreEmail(),
                                     "【登録完了】ログイン情報のお知らせ",
                                     completionBody);

                System.out.println("DEBUG: Completion email sent to: " + app.getStoreEmail());

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("WARNING: Failed to send completion email to: " + app.getStoreEmail());
            }

            // --- 運営への完了通知 ---
            try {
                String adminNotification = "店舗承認が完了しました。\n\n" +
                                          "店舗名: " + app.getStoreName() + "\n" +
                                          "店舗ID: " + store.getStoreId() + "\n" +
                                          "メール: " + app.getStoreEmail() + "\n" +
                                          "登録日時: " + new java.util.Date();

                MailSender.sendEmail("mklblue82@gmail.com",
                                     "【完了】店舗承認処理完了通知",
                                     adminNotification);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // --- 成功ページ表示 ---
            req.setAttribute("storeName", app.getStoreName());
            req.setAttribute("storeId", store.getStoreId());
            req.setAttribute("email", app.getStoreEmail());
            req.getRequestDispatcher("/admin_jsp/approve_success.jsp").forward(req, res);

        } catch (Exception e) {
            e.printStackTrace();
            showError(req, res, "承認処理中にエラーが発生しました。\n" + e.getMessage());
        } finally {
            if (conn != null) conn.close();
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse res, String message) throws Exception {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/admin_jsp/approve_error.jsp").forward(req, res);
    }

    // --- Storeテーブル重複チェック ---
    private boolean isPhoneExistsInStore(StoreDAO dao, String phone) throws Exception {
        for (Store s : dao.selectAll()) {
            if (s.getPhone() != null && s.getPhone().equals(phone)) return true;
        }
        return false;
    }

    private boolean isEmailExistsInStore(StoreDAO dao, String email) throws Exception {
        for (Store s : dao.selectAll()) {
            if (s.getEmail() != null && s.getEmail().equals(email)) return true;
        }
        return false;
    }

    // --- Applicationテーブル（pending）重複チェック ---
    private boolean isPhoneExistsInPending(ApplicationDAO dao, String phone, int currentAppId) throws Exception {
        for (Application app : dao.selectPendingApplications()) {
            if (app.getApplicationId() == currentAppId) continue; // 自身の申請はスキップ
            if (app.getStorePhone() != null && app.getStorePhone().equals(phone)) return true;
        }
        return false;
    }

    private boolean isEmailExistsInPending(ApplicationDAO dao, String email, int currentAppId) throws Exception {
        for (Application app : dao.selectPendingApplications()) {
            if (app.getApplicationId() == currentAppId) continue;
            if (app.getStoreEmail() != null && app.getStoreEmail().equals(email)) return true;
        }
        return false;
    }
}
