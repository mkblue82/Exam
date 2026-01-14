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
import javax.servlet.ServletContext;

public class MailSender {

    private static Properties mailConfig = new Properties();
    private static ServletContext servletContext;
    private static boolean initialized = false;

    public static void setServletContext(ServletContext context) {
        if (!initialized && context != null) {
            servletContext = context;
            loadConfig();
            initialized = true;
        }
    }

    private static void loadConfig() {
        try {
            InputStream in = servletContext.getResourceAsStream(
                    "/WEB-INF/classes/config/mail.properties");
            if (in == null) {
                throw new RuntimeException("mail.properties が見つかりません");
            }
            mailConfig.load(in);
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailConfig.getProperty("smtp.host"));
        props.put("mail.smtp.port", mailConfig.getProperty("smtp.port"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", mailConfig.getProperty("smtp.host"));

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        mailConfig.getProperty("smtp.user"),
                        mailConfig.getProperty("smtp.password")
                );
            }
        });
    }

    // ===== 通常メール =====
    public static void sendEmail(String to, String subject, String body)
            throws MessagingException {

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(mailConfig.getProperty("smtp.user")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        message.setContent(multipart);
        Transport.send(message);
    }

    // ===== 添付 + Reply-To 対応（今回の主役）=====
    public static void sendEmailWithAttachmentAndReplyTo(
            String to,
            String replyTo,
            String subject,
            String body,
            byte[] attachmentData,
            String fileName
    ) throws MessagingException {

        Session session = createSession();
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(mailConfig.getProperty("smtp.user")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setReplyTo(new InternetAddress[] {
                new InternetAddress(replyTo)
        });
        message.setSubject(subject, "UTF-8");

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
    }
}
