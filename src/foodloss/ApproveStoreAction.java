package foodloss;

import java.nio.file.Files;
import java.nio.file.Paths;
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

    	MailSender.setServletContext(req.getServletContext());

        req.setCharacterEncoding("UTF-8");
        String token = req.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            sendAlert(res, "無効なリンクです。");
            return;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            ApplicationDAO appDAO = new ApplicationDAO();
            StoreDAO storeDAO = new StoreDAO(conn);

            Application app = appDAO.selectByToken(token);
            if (app == null) {
                sendAlert(res, "申請データが見つかりません。トークンが無効か、既に削除されています。");
                return;
            }
            if ("approved".equals(app.getStatus())) {
                sendAlert(res, "この申請は既に承認済みです。");
                return;
            }

            if (isPhoneExistsInStore(storeDAO, app.getStorePhone())) {
                sendAlert(res, "この電話番号は既に登録されています。");
                return;
            }
            if (isEmailExistsInStore(storeDAO, app.getStoreEmail())) {
                sendAlert(res, "このメールアドレスは既に登録されています。");
                return;
            }

            Store store = new Store();
            store.setStoreName(app.getStoreName());
            store.setAddress(app.getStoreAddress());
            store.setPhone(app.getStorePhone());
            store.setEmail(app.getStoreEmail());
            store.setPassword(app.getPasswordHash());

            // ★★★ ここを変更 ★★★
            // 営業許可書をファイルパスからバイナリデータに変換
            byte[] licenseData = null;
            String licensePath = app.getBusinessLicense();
            if (licensePath != null && !licensePath.isEmpty()) {
                try {
                    // サーバー上の実際のファイルパスを取得
                    String realPath = req.getServletContext().getRealPath(licensePath);
                    licenseData = Files.readAllBytes(Paths.get(realPath));
                    System.out.println("✓ 営業許可書読み込み成功: " + realPath);
                } catch (Exception e) {
                    System.err.println("✗ 営業許可書の読み込みエラー: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            store.setLicense(licenseData);
            // ★★★ ここまで ★★★

            store.setDiscountTime(null);
            store.setDiscountRate(0);

            int storeId = storeDAO.insert(store);
            if (storeId == 0) {
                sendAlert(res, "店舗の登録に失敗しました。");
                return;
            }

            app.setStatus("approved");
            app.setApprovedAt(new Timestamp(System.currentTimeMillis()));
            appDAO.updateStatus(app);

            String loginUrl = req.getScheme() + "://" +
                    req.getServerName() + ":" + req.getServerPort() +
                    req.getContextPath() + "/store_jsp/login_store.jsp";

            String storeBody = app.getStoreName() + " 様\n\n" +
                    "店舗登録の審査が完了し、承認されました。\n" +
                    "ログイン情報：\n" +
                    "URL: " + loginUrl + "\n" +
                    "店舗ID: " + storeId + "\n" +
                    "メール: " + app.getStoreEmail() + "\n" +
                    "パスワード: 申請時に設定されたパスワード\n";

            try {
                MailSender.sendEmail(app.getStoreEmail(), "【登録完了】店舗承認のお知らせ - " + app.getStoreName(), storeBody);
                System.out.println("✓ 承認完了メール送信成功: " + app.getStoreEmail());
            } catch (Exception mailError) {
                mailError.printStackTrace();
                System.err.println("✗ 承認メール送信エラー（承認処理は完了）: " + mailError.getMessage());
            }

            // 運営用画面は作らない → アラートで完了通知
            sendAlert(res, "店舗登録が完了しました。");

        } catch (Exception e) {
            e.printStackTrace();
            sendAlert(res, "承認処理中にエラーが発生しました: " + e.getMessage());
        } finally {
            if (conn != null) conn.close();
        }
    }


    private void sendAlert(HttpServletResponse res, String msg) throws Exception {
        res.setContentType("text/html;charset=UTF-8");
        res.getWriter().println("<script>alert('" + msg.replace("'", "\\'") + "'); window.close();</script>");
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