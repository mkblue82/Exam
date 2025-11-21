package tool;

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

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private static final String SMTP_USER = "あなたのGmail@gmail.com";
    private static final String SMTP_PASSWORD = "アプリパスワード";

    public static void sendEmail(String to, String subject, String body) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER));
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

    public static void sendEmailWithAttachment(
            String to, String subject, String body,
            byte[] attachmentData, String fileName) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USER));
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
