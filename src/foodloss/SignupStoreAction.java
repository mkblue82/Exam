package foodloss;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import bean.Application;
import dao.ApplicationDAO;
import dao.StoreDAO;
import tool.Action;
import tool.MailSender;

public class SignupStoreAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        MailSender.setServletContext(req.getServletContext());
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        String storeName = req.getParameter("storeName");
        String address   = req.getParameter("address");
        String phone     = req.getParameter("phone");
        String email     = req.getParameter("email");
        String password  = req.getParameter("password");

        /* ===== 入力値を戻す(エラー時用) ===== */
        req.setAttribute("storeName", storeName);
        req.setAttribute("address", address);
        req.setAttribute("phone", phone);
        req.setAttribute("email", email);

        try (Connection conn = getConnection()) {

            ApplicationDAO appDAO = new ApplicationDAO(conn);
            StoreDAO storeDAO = new StoreDAO(conn);

            /* ===== 重複チェック（店舗テーブルのみ） ===== */
            boolean emailExists = storeDAO.existsByEmail(email);
            boolean phoneExists = storeDAO.existsByPhone(phone);

            // 両方重複している場合
            if (emailExists && phoneExists) {
                req.setAttribute("formError", "このメールアドレスと電話番号は既に使用されています。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp")
                   .forward(req, res);
                return;
            }

            // メールアドレスのみ重複
            if (emailExists) {
                req.setAttribute("formError", "このメールアドレスは既に使用されています。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp")
                   .forward(req, res);
                return;
            }

            // 電話番号のみ重複
            if (phoneExists) {
                req.setAttribute("formError", "この電話番号は既に使用されています。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp")
                   .forward(req, res);
                return;
            }

            /* ===== 許可証ファイル ===== */
            Part permitFilePart = req.getPart("permitFile");
            if (permitFilePart == null || permitFilePart.getSize() == 0) {
                req.setAttribute("formError", "営業許可証ファイルを選択してください。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp")
                   .forward(req, res);
                return;
            }

            String contentType = permitFilePart.getContentType();
            String ext =
                    contentType.equals("application/pdf") ? ".pdf" :
                    contentType.equals("image/jpeg")      ? ".jpg" :
                    contentType.equals("image/png")       ? ".png" : "";

            if (ext.isEmpty()) {
                req.setAttribute("formError", "対応していないファイル形式です。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp")
                   .forward(req, res);
                return;
            }

            String uploadDir = req.getServletContext().getRealPath("/uploads");
            new File(uploadDir).mkdirs();

            String savedName = "license_" + UUID.randomUUID() + ext;
            String filePath = uploadDir + File.separator + savedName;

            byte[] permitFileData;
            try (InputStream is = permitFilePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(filePath);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    baos.write(buffer, 0, len);
                }
                permitFileData = baos.toByteArray();
            }

            /* ===== 登録処理 ===== */
            String passwordHash = hash(password);
            String token = UUID.randomUUID().toString();

            Application app = new Application();
            app.setStoreName(storeName);
            app.setStoreAddress(address);
            app.setStorePhone(phone);
            app.setStoreEmail(email);
            app.setPasswordHash(passwordHash);
            app.setBusinessLicense("/uploads/" + savedName);
            app.setApprovalToken(token);
            app.setStatus("pending");

            appDAO.insert(app);
            session.setAttribute("pendingApplication", app);

            /* ===== メール（管理者のみ） ===== */
            String approvalUrl =
                req.getScheme() + "://" +
                req.getServerName() + ":" + req.getServerPort() +
                req.getContextPath() +
                "/ApproveStore.action?token=" + token;

            String adminBody =
                "新規店舗申請がありました。\n\n" +
                "店舗名: " + storeName + "\n" +
                "住所: " + address + "\n" +
                "電話: " + phone + "\n" +
                "メール: " + email + "\n\n" +
                "▼承認URL\n" + approvalUrl;

            MailSender.sendEmailWithAttachment(
                "ahi559933@gmail.com",
                "【要承認】新規店舗申請 - " + storeName,
                adminBody,
                permitFileData,
                storeName + "_permit" + ext,
                contentType
            );
        }

        req.getRequestDispatcher("/store_jsp/signup_done_store.jsp")
           .forward(req, res);
    }

    private String hash(String src) throws Exception {
        java.security.MessageDigest md =
            java.security.MessageDigest.getInstance("SHA-256");
        byte[] b = md.digest(src.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}