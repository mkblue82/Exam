package tool;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

    private static final Properties mailConfig = new Properties();

    static {
        try (InputStream in = MailSender.class.getClassLoader().getResourceAsStream("config/mail.properties")) {
            if (in == null) {
                throw new RuntimeException("mail.propertiesが見つかりません。");
            }
            mailConfig.load(in);
        } catch (Exception e) {
            throw new RuntimeException("mail.propertiesの読み込みに失敗しました。", e);
        }
    }

    private static Session createSession() {
        return Session.getInstance(mailConfig, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        mailConfig.getProperty("smtp.user"),
                        mailConfig.getProperty("smtp.password")
                );
            }
        });
    }

    // 本文のみ送信
    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailConfig.getProperty("smtp.user")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setHeader("Content-Transfer-Encoding", "8bit");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        message.setContent(multipart);
        Transport.send(message);

        System.out.println("DEBUG: メール送信完了 → " + to);
    }

    // 添付付きメール送信
    public static void sendEmailWithAttachment(
            String to, String subject, String body,
            byte[] attachmentData, String fileName) throws MessagingException {

        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailConfig.getProperty("smtp.user")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setHeader("Content-Transfer-Encoding", "8bit");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(fileName);
        attachmentPart.setContent(attachmentData, "application/octet-stream");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
        Transport.send(message);

        System.out.println("DEBUG: 添付メール送信完了 → " + to);
    }
}
