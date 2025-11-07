package foodloss;

import java.io.IOException;
import java.net.PasswordAuthentication;

import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;

import dao.StoreDAO;
import sun.rmi.transport.Transport;

@WebServlet("/store/register")
@MultipartConfig
public class SignupAction_store extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // フォームからの入力を取得
        String storeName = request.getParameter("storeName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Part licensePart = request.getPart("license");

        // 未入力チェック
        if (storeName == null || storeName.isEmpty() ||
            address == null || address.isEmpty() ||
            phone == null || phone.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty()) {

            request.setAttribute("error", "未入力の項目があります。");
            request.getRequestDispatcher("/store/register.jsp").forward(request, response);
            return;
        }

        // 電話番号バリデーション
        if (!phone.matches("\\d+")) {
            request.setAttribute("error", "電話番号は数字のみで入力してください。");
            request.getRequestDispatcher("/store/register.jsp").forward(request, response);
            return;
        }

        // Storeオブジェクト作成
        Store store = new Store();
        store.setStoreName(storeName);
        store.setAddress(address);
        store.setPhone(phone);
        store.setEmail(email);
        store.setPassword(password);

        try {
            // DB登録
            StoreDAO dao = new StoreDAO(DBConnection.getConnection());
            dao.insert(store);

            // --- メール送信処理 ---
            sendMailToAdmin(store, licensePart);

            // 完了画面へ
            request.getRequestDispatcher("/store/register_complete.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "登録中にエラーが発生しました。");
            request.getRequestDispatcher("/store/register.jsp").forward(request, response);
        }
    }

    /**
     * 運営に新規店舗申請メールを送信
     */
    private void sendMailToAdmin(Store store, Part licensePart) {
        final String from = "noreply@example.com";  // システム送信用アドレス
        final String to = "admin@example.com";      // 運営側のメールアドレス
        final String host = "smtp.gmail.com";       // SMTPサーバー
        final String username = "yourmail@gmail.com"; // 送信元メール(Gmailなら)
        final String password = "アプリパスワードをここに"; // Gmailアプリパスワード

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "FoodLoss運営"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("【新規店舗申請】" + store.getStoreName() + " 様より申請がありました");

            // メール本文
            String text = String.format(
                "以下の内容で新規店舗申請がありました。\n\n" +
                "店舗名：%s\n住所：%s\n電話番号：%s\nメール：%s\n\n営業許可証を添付しています。",
                store.getStoreName(),
                store.getAddress(),
                store.getPhone(),
                store.getEmail()
            );

            // 添付付きメール構築
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text, "UTF-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // 添付ファイルを追加
            if (licensePart != null && licensePart.getSize() > 0) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                try (InputStream is = licensePart.getInputStream()) {
                    attachmentPart.setFileName(licensePart.getSubmittedFileName());
                    attachmentPart.setDataHandler(new DataHandler(
                        new ByteArrayDataSource(is, licensePart.getContentType())));
                }
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("運営宛に新規店舗申請メールを送信しました。");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("メール送信に失敗しました。");
        }
    }
}
