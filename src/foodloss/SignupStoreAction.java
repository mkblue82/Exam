package foodloss;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
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
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Part permitFilePart = req.getPart("permitFile");

        String uploadDir = req.getServletContext().getRealPath("/uploads");
        new File(uploadDir).mkdirs();

        String savedName = "license_" + UUID.randomUUID() + ".pdf";
        String filePath = uploadDir + File.separator + savedName;

        // ===== ファイル保存（Java 8対応）=====
        try (InputStream is = permitFilePart.getInputStream();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        // ===== 添付用 byte[] 作成（Java 8対応）=====
        byte[] permitFileData;
        try (InputStream is = permitFilePart.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            permitFileData = baos.toByteArray();
        }

        String passwordHash = hash(password);

        Connection conn = getConnection();
        try {
            StoreDAO storeDAO = new StoreDAO(conn);
            ApplicationDAO appDAO = new ApplicationDAO();

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

            String approvalUrl =
                    req.getScheme() + "://" +
                    req.getServerName() + ":" +
                    req.getServerPort() +
                    req.getContextPath() +
                    "/foodloss/ApproveStore.action?token=" + token;

            String adminBody =
                    "新規店舗申請がありました。\n\n" +
                    "店舗名: " + storeName + "\n" +
                    "住所: " + address + "\n" +
                    "電話: " + phone + "\n" +
                    "メール: " + email + "\n\n" +
                    "▼承認URL\n" + approvalUrl;

            MailSender.sendEmailWithAttachmentAndReplyTo(
                    "ahi559933@gmail.com",
                    email,
                    "【要承認】新規店舗申請 - " + storeName,
                    adminBody,
                    permitFileData,
                    storeName + "_permit.pdf"
            );

            MailSender.sendEmail(
                    email,
                    "【申請受付】" + storeName,
                    storeName + " 様\n\n申請を受け付けました。"
            );

        } finally {
            conn.close();
        }

        req.getRequestDispatcher("/store_jsp/signup_done_store.jsp")
           .forward(req, res);
    }

    private String hash(String src) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] b = md.digest(src.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
