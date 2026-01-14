package foodloss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
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

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        // CSRFチェック
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            forwardWithError(req, res, "不正なアクセスです。");
            return;
        }

        // 入力値取得
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String passwordRaw = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");

        // バリデーション
        if (storeName == null || storeName.trim().isEmpty()) { forwardWithError(req, res, "店舗名を入力してください。"); return; }
        if (address == null || address.trim().isEmpty()) { forwardWithError(req, res, "店舗住所を入力してください。"); return; }
        if (phone == null || phone.trim().isEmpty()) { forwardWithError(req, res, "電話番号を入力してください。"); return; }
        if (email == null || email.trim().isEmpty()) { forwardWithError(req, res, "メールアドレスを入力してください。"); return; }
        if (passwordRaw == null || passwordRaw.trim().isEmpty()) { forwardWithError(req, res, "パスワードを入力してください。"); return; }
        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) { forwardWithError(req, res, "確認用パスワードを入力してください。"); return; }
        if (!passwordRaw.equals(passwordConfirm)) { forwardWithError(req, res, "パスワードが一致しません。"); return; }
        if (passwordRaw.length() < 8) { forwardWithError(req, res, "パスワードは8文字以上で入力してください。"); return; }
        if (!phone.matches("[0-9]{10,11}")) { forwardWithError(req, res, "電話番号は10桁または11桁の数字で入力してください。"); return; }

        // ファイルアップロード
        Part permitFilePart = req.getPart("permitFile");
        if (permitFilePart == null || permitFilePart.getSize() == 0) { forwardWithError(req, res, "営業許可書をアップロードしてください。"); return; }
        String permitFileName = getFileName(permitFilePart);
        if (!isValidFileType(permitFileName)) { forwardWithError(req, res, "営業許可書はJPG、PNG、PDF形式でアップロードしてください。"); return; }
        if (permitFilePart.getSize() > 5 * 1024 * 1024) { forwardWithError(req, res, "ファイルサイズは5MB以下にしてください。"); return; }

        // ★★★ ファイルをサーバーに保存 ★★★
        String uploadDir = req.getServletContext().getRealPath("/uploads");
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileExtension = permitFileName.substring(permitFileName.lastIndexOf("."));
        String savedFileName = "license_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir + File.separator + savedFileName;

        try (InputStream is = permitFilePart.getInputStream();
             FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        // DBに保存するパス（相対パス）
        String relativePath = "/uploads/" + savedFileName;

        // メール添付用にbyte配列も読み込む
        byte[] permitFileData;
        try (InputStream is = permitFilePart.getInputStream();
             java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024]; int len;
            while ((len = is.read(buffer)) != -1) { baos.write(buffer, 0, len); }
            permitFileData = baos.toByteArray();
        }

        // パスワードハッシュ
        String passwordHash = hashPassword(passwordRaw);

        Connection conn = null;
        try {
            conn = getConnection();
            StoreDAO storeDAO = new StoreDAO(conn);
            ApplicationDAO appDAO = new ApplicationDAO();

            // **店舗テーブルのみ重複チェック**
            if (isPhoneExists(storeDAO, phone)) { forwardWithError(req, res, "この電話番号は既に登録されています。"); return; }
            if (isEmailExists(storeDAO, email)) { forwardWithError(req, res, "このメールアドレスは既に登録されています。"); return; }

            // 承認用トークン
            String approvalToken = UUID.randomUUID().toString();

            // Application作成
            Application app = new Application();
            app.setStoreName(storeName);
            app.setStoreAddress(address);
            app.setStorePhone(phone);
            app.setStoreEmail(email);
            app.setPasswordHash(passwordHash);
            app.setBusinessLicense(relativePath); // ★★★ ファイルパスを保存 ★★★
            app.setApprovalToken(approvalToken);
            app.setStatus("pending");

            // DB登録
            int applicationId = appDAO.insert(app);
            if (applicationId == 0) { forwardWithError(req, res, "申請登録に失敗しました。"); return; }

            // ★★★ 重要:メール送信前にセッションに保存 ★★★
            session.setAttribute("pendingApplication", app);
            session.removeAttribute("csrfToken");

            // 運営メール送信URL
            String approvalUrl = req.getScheme() + "://" +
                    req.getServerName() + ":" + req.getServerPort() +
                    req.getContextPath() + "/foodloss/ApproveStore.action?token=" + approvalToken;

            String adminBody = "新規店舗申請がありました。\n\n" +
                    "店舗名: " + storeName + "\n" +
                    "住所: " + address + "\n" +
                    "電話: " + phone + "\n" +
                    "メール: " + email + "\n\n" +
                    "承認するには下記URLをクリックしてください:\n" + approvalUrl + "\n\n" +
                    "営業許可証を添付ファイルでご確認ください。";

            // メール送信（エラーが出てもセッションには既に保存済み）
            try {
                MailSender.sendEmailWithAttachment(
                        "ahi559933@gmail.com",
                        "【要承認】新規店舗申請通知 - " + storeName,
                        adminBody,
                        permitFileData,
                        storeName + "_permit.pdf"
                );

                // 店舗に受付メール
                String storeBody = storeName + " 様\n\n" +
                        "店舗登録の申請を受け付けました。\n" +
                        "審査完了までしばらくお待ちください。\n\n" +
                        "審査完了後、メールにてご連絡いたします。";
                MailSender.sendEmail(email, "【店舗申請受付】" + storeName, storeBody);

                System.out.println("✓ メール送信成功: " + email);

            } catch (Exception mailError) {
                // メール送信失敗してもログだけ出して処理を続行
                mailError.printStackTrace();
                System.err.println("✗ メール送信エラー（処理は継続）: " + mailError.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            forwardWithError(req, res, "申請処理中にエラーが発生しました: " + e.getMessage());
            return;
        } finally {
            if (conn != null) conn.close();
        }

        // デバッグ用
        System.out.println("=== 申請完了画面へ遷移 ===");
        System.out.println("pendingApplication: " + session.getAttribute("pendingApplication"));
        System.out.println("Store Name: " + storeName);

        req.getRequestDispatcher("/store_jsp/signup_done_store.jsp").forward(req, res);
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, String msg) throws Exception {
        req.setAttribute("errorMessage", msg);
        req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
    }

    private boolean isPhoneExists(StoreDAO dao, String phone) throws Exception {
        for (Store s : dao.selectAll()) { if (s.getPhone() != null && s.getPhone().equals(phone)) return true; }
        return false;
    }

    private boolean isEmailExists(StoreDAO dao, String email) throws Exception {
        for (Store s : dao.selectAll()) { if (s.getEmail() != null && s.getEmail().equals(email)) return true; }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) { String h = Integer.toHexString(0xff & b); if (h.length() == 1) sb.append('0'); sb.append(h); }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    private String getFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        for (String elem : cd.split(";")) { if (elem.trim().startsWith("filename")) { return elem.substring(elem.indexOf('=')+1).trim().replace("\"",""); } }
        return null;
    }

    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".pdf");
    }
}