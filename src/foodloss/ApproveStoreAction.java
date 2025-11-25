package foodloss;

import java.sql.Connection;
import java.sql.Timestamp;

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

        req.setCharacterEncoding("UTF-8");
        String token = req.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            showError(req, res, "無効なリンクです。");
            return;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            ApplicationDAO appDAO = new ApplicationDAO();
            StoreDAO storeDAO = new StoreDAO(conn);

            // トークンで申請取得
            Application app = appDAO.selectByToken(token);
            if (app == null) {
                showError(req, res, "申請データが見つかりません。トークンが無効か、既に削除されています。");
                return;
            }
            if ("approved".equals(app.getStatus())) {
                showError(req, res, "この申請は既に承認済みです。");
                return;
            }

            // 重複チェック（念のため）
            if (isPhoneExistsInStore(storeDAO, app.getStorePhone())) {
                showError(req, res, "この電話番号は既に登録されています。");
                return;
            }
            if (isEmailExistsInStore(storeDAO, app.getStoreEmail())) {
                showError(req, res, "このメールアドレスは既に登録されています。");
                return;
            }

            // Store作成
            Store store = new Store();
            store.setStoreName(app.getStoreName());
            store.setAddress(app.getStoreAddress());
            store.setPhone(app.getStorePhone());
            store.setEmail(app.getStoreEmail());
            store.setPassword(app.getPasswordHash());
            store.setLicense(app.getBusinessLicense());
            store.setDiscountTime(null);
            store.setDiscountRate(0);

            // DB登録
            int storeId = storeDAO.insert(store);
            if (storeId == 0) {
                showError(req, res, "店舗の登録に失敗しました。");
                return;
            }

            // Application更新
            app.setStatus("approved");
            app.setApprovedAt(new Timestamp(System.currentTimeMillis()));
            appDAO.updateStatus(app);

            // ログインURL生成
            String loginUrl = req.getScheme() + "://" +
                    req.getServerName() + ":" + req.getServerPort() +
                    req.getContextPath() + "/store_jsp/login_store.jsp";


            // 店舗へ完了メール送信（店舗IDを含む）
            String storeBody = app.getStoreName() + " 様\n\n" +
                    "店舗登録の審査が完了し、承認されました。\n" +
                    "おめでとうございます！\n\n" +
                    "以下のログイン情報で、すぐにご利用いただけます。\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n" +
                    "【ログイン情報】\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n" +
                    "ログインURL: " + loginUrl + "\n" +
                    "店舗ID: " + storeId + "\n" +
                    "メールアドレス: " + app.getStoreEmail() + "\n" +
                    "パスワード: 申請時に設定されたパスワード\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "※店舗IDはログイン時に必要となりますので、大切に保管してください。\n" +
                    "※パスワードを忘れた場合は、パスワード再設定機能をご利用ください。\n\n" +
                    "ご不明な点がございましたら、お気軽にお問い合わせください。\n" +
                    "今後ともよろしくお願いいたします。";

            try {
                MailSender.sendEmail(app.getStoreEmail(), "【登録完了】店舗承認のお知らせ - " + app.getStoreName(), storeBody);
                System.out.println("✓ 承認完了メール送信成功: " + app.getStoreEmail());
            } catch (Exception mailError) {
                // メール送信失敗してもログだけ出して処理を続行
                mailError.printStackTrace();
                System.err.println("✗ 承認メール送信エラー（承認処理は完了）: " + mailError.getMessage());
            }

            // セッションに承認情報をセット
            req.setAttribute("approvedStore", app);

            System.out.println("=== 店舗承認完了 ===");
            System.out.println("Store ID: " + storeId);
            System.out.println("Store Name: " + app.getStoreName());
            System.out.println("Email: " + app.getStoreEmail());

        } catch (Exception e) {
            e.printStackTrace();
            showError(req, res, "承認処理中にエラーが発生しました: " + e.getMessage());
            return;
        } finally {
            if (conn != null) conn.close();
        }

        req.getRequestDispatcher("/store_jsp/approve_success.jsp").forward(req, res);
    }

    private void showError(HttpServletRequest req, HttpServletResponse res, String msg) throws Exception {
        req.setAttribute("errorMessage", msg);
        req.getRequestDispatcher("/store_jsp/approve_error.jsp").forward(req, res);
    }

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

}