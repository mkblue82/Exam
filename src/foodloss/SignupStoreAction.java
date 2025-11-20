package foodloss;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import bean.Application;
import bean.Store;
import dao.ApplicationDAO;
import dao.StoreDAO;
import tool.Action;
import tool.MailSender;

public class SignupStoreAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        System.out.println("DEBUG: ========== SignupStoreAction START ==========");
        System.out.println("DEBUG: Method = " + req.getMethod());

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        // --- CSRFチェック ---
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            forwardWithError(req, res, "不正なアクセスです。");
            return;
        }

        // --- 入力値 ---
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String passwordRaw = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");

        // バリデーション
        if (storeName == null || storeName.trim().isEmpty()) {
            forwardWithError(req, res, "店舗名を入力してください。");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            forwardWithError(req, res, "店舗住所を入力してください。");
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            forwardWithError(req, res, "電話番号を入力してください。");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            forwardWithError(req, res, "メールアドレスを入力してください。");
            return;
        }
        if (passwordRaw == null || passwordRaw.trim().isEmpty()) {
            forwardWithError(req, res, "パスワードを入力してください。");
            return;
        }
        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
            forwardWithError(req, res, "確認用パスワードを入力してください。");
            return;
        }
        if (!passwordRaw.equals(passwordConfirm)) {
            forwardWithError(req, res, "パスワードが一致しません。");
            return;
        }
        if (passwordRaw.length() < 8) {
            forwardWithError(req, res, "パスワードは8文字以上で入力してください。");
            return;
        }
        if (!phone.matches("[0-9]{10,11}")) {
            forwardWithError(req, res, "電話番号は10桁または11桁の数字で入力してください。");
            return;
        }

        // --- ファイルアップロード ---
        Part permitFilePart = req.getPart("permitFile");
        if (permitFilePart == null || permitFilePart.getSize() == 0) {
            forwardWithError(req, res, "営業許可書をアップロードしてください。");
            return;
        }
        String permitFileName = getFileName(permitFilePart);
        if (!isValidFileType(permitFileName)) {
            forwardWithError(req, res, "営業許可書はJPG、PNG、またはPDF形式でアップロードしてください。");
            return;
        }
        if (permitFilePart.getSize() > 5 * 1024 * 1024) {
            forwardWithError(req, res, "ファイルサイズは5MB以下にしてください。");
            return;
        }

        // ファイルデータをbyte配列に変換
        byte[] permitFileData;
        try (InputStream is = permitFilePart.getInputStream();
             java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            permitFileData = baos.toByteArray();
        }

        // --- パスワードハッシュ化 ---
        String passwordHash = hashPassword(passwordRaw);

        // --- 重複チェック（既存店舗テーブルのみ） ---
        Connection conn = null;

        try {
        	conn = getConnection();
        	StoreDAO storeDAO = new StoreDAO(conn);

            if (isPhoneExists(storeDAO, phone)) {
                forwardWithError(req, res, "この電話番号は既に登録されています。");
                return;
            }
            if (isEmailExists(storeDAO, email)) {
                forwardWithError(req, res, "このメールアドレスは既に登録されています。");
                return;
            }

            // --- 承認トークン生成 ---
            String approvalToken = UUID.randomUUID().toString();

            // --- Applicationオブジェクト作成 ---
            Application app = new Application();
            app.setStoreName(storeName);
            app.setStoreAddress(address);
            app.setStorePhone(phone);
            app.setStoreEmail(email);
            app.setPasswordHash(passwordHash);
            app.setBusinessLicense(permitFileData);
            app.setApprovalToken(approvalToken);

            // --- DBに申請データを保存 ---
            ApplicationDAO appDAO = new ApplicationDAO(conn);
            appDAO.insert(app);

            // --- CSRFトークンをクリア ---
            session.removeAttribute("csrfToken");

            // --- 運営へのメール送信（承認リンク付き） ---
            String approvalLink = req.getScheme() + "://" + req.getServerName() +
                                  ":" + req.getServerPort() + req.getContextPath() +
                                  "/foodloss/ApproveStore.action?token=" + approvalToken;

            String body = "新しい店舗申請がありました。\n\n" +
                          "店舗名: " + storeName + "\n" +
                          "住所: " + address + "\n" +
                          "電話番号: " + phone + "\n" +
                          "メール: " + email + "\n\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n" +
                          "承認してDBに登録する場合は、以下のリンクをクリックしてください:\n" +
                          approvalLink + "\n" +
                          "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                          "※営業許可書は添付ファイルをご確認ください。";

            MailSender.sendEmailWithAttachment(
                "mklblue82@gmail.com", // 運営メールアドレス
                "【要承認】新規店舗申請通知",
                body,
                permitFileData,
                storeName + "_permit.pdf"
            );

            // --- 店舗側に申請受付メール送信 ---
            String storeBody = storeName + " 様\n\n" +
                              "店舗申請を受け付けました。\n" +
                              "運営による審査が完了次第、登録完了メールをお送りします。\n\n" +
                              "今しばらくお待ちください。";

            MailSender.sendEmail(
                email,
                "店舗申請受付のお知らせ",
                storeBody
            );

            System.out.println("DEBUG: 申請データをDBに保存しました。Application ID = " + app.getApplicationId());

        } catch (SQLException e) {
            e.printStackTrace();
            forwardWithError(req, res, "データベースエラーが発生しました。");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            forwardWithError(req, res, "メール送信に失敗しました。もう一度お試しください。");
            return;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        req.getRequestDispatcher("/store_jsp/signup_done_store.jsp").forward(req, res);
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, String message) throws Exception {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
    }

    private boolean isPhoneExists(StoreDAO dao, String phone) throws Exception {
        for (Store s : dao.selectAll()) {
            if (s.getPhone() != null && s.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmailExists(StoreDAO dao, String email) throws Exception {
        for (Store s : dao.selectAll()) {
            if (s.getEmail() != null && s.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }

    private String getFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        for (String elem : cd.split(";")) {
            if (elem.trim().startsWith("filename")) {
                return elem.substring(elem.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
               lower.endsWith(".png") || lower.endsWith(".pdf");
    }
}