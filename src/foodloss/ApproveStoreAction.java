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
                showError(req, res, "申請データが見つかりません。");
                return;
            }
            if ("approved".equals(app.getStatus())) {
                showError(req, res, "既に承認済みです。");
                return;
            }

            // 重複チェック
            if (isPhoneExistsInStore(storeDAO, app.getStorePhone())) {
                showError(req, res, "電話番号が既に登録されています。");
                return;
            }
            if (isEmailExistsInStore(storeDAO, app.getStoreEmail())) {
                showError(req, res, "メールアドレスが既に登録されています。");
                return;
            }

            // Store作成
            Store store = new Store();
            store.setStoreName(app.getStoreName());
            store.setAddress(app.getStoreAddress());
            store.setPhone(app.getStorePhone());
            store.setEmail(app.getStoreEmail());
            store.setPassword(app.getPasswordHash());
            store.setLicense(app.getBusinessLicense()); // byte[] を直接セット
            store.setDiscountTime(null);
            store.setDiscountRate(0);

            // DB登録
            int storeId = storeDAO.insert(store);

            // Application更新
            app.setStatus("approved");
            app.setApprovedAt(new Timestamp(System.currentTimeMillis()));
            appDAO.updateStatus(app);

            // 店舗へ完了メール
            String storeBody = app.getStoreName() + " 様\n\n" +
                    "店舗登録が承認されました。\n" +
                    "ログイン情報:\n" +
                    "メール: " + app.getStoreEmail() + "\n" +
                    "パスワード: 申請時に設定したパスワード\n";
            MailSender.sendEmail(app.getStoreEmail(), "【登録完了】店舗承認のお知らせ", storeBody);

        } catch (Exception e) {
            e.printStackTrace();
            showError(req, res, "承認処理中にエラーが発生しました: " + e.getMessage());
            return;
        } finally {
            if (conn != null) conn.close();
        }

        req.getRequestDispatcher("/admin_jsp/approve_success.jsp").forward(req, res);
    }

    private void showError(HttpServletRequest req, HttpServletResponse res, String msg) throws Exception {
        req.setAttribute("errorMessage", msg);
        req.getRequestDispatcher("/admin_jsp/approve_error.jsp").forward(req, res);
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
