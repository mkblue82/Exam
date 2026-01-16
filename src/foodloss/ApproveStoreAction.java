package foodloss;

import java.nio.file.Files;
import java.nio.file.Paths;
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

        MailSender.setServletContext(req.getServletContext());
        req.setCharacterEncoding("UTF-8");

        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            sendAlert(res, "無効なリンクです。");
            return;
        }

        Connection conn = null;
        try {
            conn = getConnection();

            ApplicationDAO appDAO = new ApplicationDAO(conn);
            StoreDAO storeDAO = new StoreDAO(conn);

            Application app = appDAO.selectByToken(token);
            if (app == null) {
                sendAlert(res, "申請データが見つかりません。");
                return;
            }

            if ("approved".equals(app.getStatus())) {
                sendAlert(res, "この申請は既に承認済みです。");
                return;
            }

            // 店舗テーブルのみで重複チェック
            if (storeDAO.existsByPhone(app.getStorePhone())) {
                sendAlert(res, "この電話番号は既に登録されています。");
                return;
            }

            if (storeDAO.existsByEmail(app.getStoreEmail())) {
                sendAlert(res, "このメールアドレスは既に登録されています。");
                return;
            }

            Store store = new Store();
            store.setStoreName(app.getStoreName());
            store.setAddress(app.getStoreAddress());
            store.setPhone(app.getStorePhone());
            store.setEmail(app.getStoreEmail());
            store.setPassword(app.getPasswordHash());

            // 営業許可書
            byte[] licenseData = null;
            if (app.getBusinessLicense() != null) {
                String realPath = req.getServletContext()
                        .getRealPath(app.getBusinessLicense());
                licenseData = Files.readAllBytes(Paths.get(realPath));
            }
            store.setLicense(licenseData);

            store.setDiscountTime(null);
            store.setDiscountRate(0);

            int storeId = storeDAO.insert(store);
            if (storeId == 0) {
                sendAlert(res, "店舗登録に失敗しました。");
                return;
            }

            appDAO.approve(app.getApplicationId());

            String loginUrl = req.getScheme() + "://" +
                    req.getServerName() + ":" +
                    req.getServerPort() +
                    req.getContextPath() +
                    "/store_jsp/login_store.jsp";

            String body =
                    app.getStoreName() + " 様\n\n" +
                    "店舗登録が承認されました。\n" +
                    "以下の情報でログインできます。\n\n" +
                    "【ログイン情報】\n" +
                    "店舗ID: " + storeId + "\n" +
                    "メールアドレス: " + app.getStoreEmail() + "\n\n" +
                    "ログインURL:\n" + loginUrl + "\n";

            MailSender.sendEmail(
                    app.getStoreEmail(),
                    "【承認完了】店舗登録のお知らせ",
                    body
            );

            sendAlert(res, "店舗登録が完了しました。");

        } finally {
            if (conn != null) conn.close();
        }
    }

    private void sendAlert(HttpServletResponse res, String msg) throws Exception {
        res.setContentType("text/html;charset=UTF-8");
        res.getWriter().println("<script>");
        res.getWriter().println("alert('" + msg.replace("'", "\\'") + "');");
        res.getWriter().println("window.close();");
        res.getWriter().println("</script>");
    }
}