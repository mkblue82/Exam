package foodloss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;

import bean.Store;
import sun.rmi.transport.Transport;
@WebServlet("/signup_store")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 最大10MB
public class SignupAction_store extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            req.setAttribute("errorMessage", "不正なアクセスです。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // 入力値取得
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        Part licensePart = req.getPart("license");

        // 入力チェック
        if (storeName.isEmpty() || address.isEmpty() || phone.isEmpty() ||
            email.isEmpty() || password.isEmpty() || licensePart == null) {
            req.setAttribute("errorMessage", "未入力の項目があります。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (!phone.matches("\\d+")) {
            req.setAttribute("errorMessage", "電話番号には数字のみを入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // Store オブジェクト作成（DB保存はしない）
        Store store = new Store();
        store.setStoreName(storeName);
        store.setAddress(address);
        store.setPhone(phone);
        store.setEmail(email);

        // 一時フォルダに営業許可証を保存
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = licensePart.getSubmittedFileName();
        File tempFile = new File(tempDir, fileName);
        try (InputStream is = licensePart.getInputStream()) {
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // メール送信（運営側へ）
        sendMailWithAttachment(store, password, tempFile);

        // 一時ファイル削除
        tempFile.delete();

        session.removeAttribute("csrfToken");
        req.getRequestDispatcher("/jsp/signupsuccess_store.jsp").forward(req, res);
    }

    // メール送信（運営側へ）
    private void sendMailWithAttachment(Store store, String password, File attachment) {
        final String from = "noreply@foodloss.com";
        final String to = "mklblue82@gmail.com"; // 運営側メール
        final String host = "smtp.example.com"; // SMTPサーバ
        final String username = "noreply@foodloss.com";
        final String pass = "yourpassword";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pass);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "FoodLoss System"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("【新規店舗申請】" + store.getStoreName() + " 様からの申請");

            // 本文
            String body = String.format(
                "新しい店舗申請がありました。\n\n" +
                "【店舗名】%s\n【住所】%s\n【電話番号】%s\n【メールアドレス】%s\n【パスワード】%s\n",
                store.getStoreName(), store.getAddress(), store.getPhone(), store.getEmail(), password
            );

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body, "UTF-8");

            MimeBodyPart attachPart = new MimeBodyPart();
            attachPart.attachFile(attachment);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("メール送信に失敗しました: " + e.getMessage());
        }
    }
}
