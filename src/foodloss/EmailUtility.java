package foodloss;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * メール送信ユーティリティクラス
 */
public class EmailUtility {

    private static Properties mailConfig = null;

    /**
     * mail.propertiesから設定を読み込み
     */
    private static Properties getMailConfig() {
        if (mailConfig == null) {
            mailConfig = new Properties();
            try (InputStream input = EmailUtility.class.getClassLoader()
                    .getResourceAsStream("config/mail.properties")) {
                if (input != null) {
                    mailConfig.load(input);
                    System.out.println("✅ mail.properties読み込み成功");
                } else {
                    System.err.println("❌ mail.propertiesが見つかりません");
                }
            } catch (IOException e) {
                System.err.println("❌ mail.properties読み込みエラー");
                e.printStackTrace();
            }
        }
        return mailConfig;
    }

    /**
     * 値引き開始通知メールを送信
     * @param toEmails 送信先メールアドレスのリスト
     * @param storeName 店舗名
     * @param discountRate 割引率
     * @param discountStartTime 割引開始時刻
     */
    public static void sendDiscountNotification(
            List<String> toEmails,
            String storeName,
            int discountRate,
            java.sql.Time discountStartTime) {

    	System.out.println("━━━ sendDiscountNotification 開始 ━━━");
        System.out.println("toEmails: " + (toEmails == null ? "null" : toEmails.size() + "件"));
        System.out.println("storeName: " + storeName);
        System.out.println("discountRate: " + discountRate);
        System.out.println("discountStartTime: " + discountStartTime);

        if (toEmails == null || toEmails.isEmpty()) {
            System.out.println("通知対象のメールアドレスがありません");
            return;
        }

        Properties config = getMailConfig();

        // SMTP設定
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", config.getProperty("smtp.port", "587"));
        props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        String fromEmail = config.getProperty("smtp.user");
        String password = config.getProperty("smtp.password");

        if (fromEmail == null || password == null) {
            System.err.println("❌ メール設定が不完全です(smtp.user または smtp.password が未設定)");
            return;
        }

        // 認証情報
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // 各メールアドレスに送信
        int successCount = 0;
        int failCount = 0;

        for (String toEmail : toEmails) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("【値引き開始】" + storeName + "で値引きが開始されました");

                // 時刻をフォーマット
                String timeStr = discountStartTime != null
                               ? discountStartTime.toString().substring(0, 5)
                               : "設定時刻";

                // メール本文
                String body = String.format(
                    "お気に入り店舗「%s」で値引きが開始されました！\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n" +
                    "割引率: %d%%OFF\n" +
                    "開始時刻: %s\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "お得な商品をぜひチェックしてください。\n\n" +
                    "※このメールは自動送信です。",
                    storeName, discountRate, timeStr
                );

                message.setText(body);
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");

                // 送信
                Transport.send(message);
                System.out.println("✅ メール送信成功: " + toEmail);
                successCount++;

            } catch (MessagingException e) {
                System.err.println("❌ メール送信失敗: " + toEmail);
                e.printStackTrace();
                failCount++;
            }
        }

        System.out.println("📧 メール送信結果 - 成功: " + successCount + "件 / 失敗: " + failCount + "件");
    }

    /**
     * 商品登録通知メールを送信
     * @param toEmails 送信先メールアドレスのリスト
     * @param merchandiseName 商品名
     * @param price 価格
     * @param storeName 店舗名
     */
    public static void sendMerchandiseRegistrationNotification(
            List<String> toEmails,
            String merchandiseName,
            int price,
            String storeName) {

        if (toEmails == null || toEmails.isEmpty()) {
            System.out.println("通知対象のメールアドレスがありません");
            return;
        }

        Properties config = getMailConfig();

        // SMTP設定
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", config.getProperty("smtp.port", "587"));
        props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        String fromEmail = config.getProperty("smtp.user");
        String password = config.getProperty("smtp.password");

        if (fromEmail == null || password == null) {
            System.err.println("❌ メール設定が不完全です(smtp.user または smtp.password が未設定)");
            return;
        }

        // 認証情報
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // 各メールアドレスに送信
        int successCount = 0;
        int failCount = 0;

        for (String toEmail : toEmails) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("【新商品登録】" + storeName + "で新しい商品が登録されました");

                // メール本文
                String body = String.format(
                    "お気に入り店舗「%s」で新しい商品が登録されました。\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n" +
                    "商品名: %s\n" +
                    "価格: %,d円\n" +
                    "━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "詳細はアプリでご確認ください。\n\n" +
                    "※このメールは自動送信です。",
                    storeName, merchandiseName, price
                );

                message.setText(body);
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");

                // 送信
                Transport.send(message);
                System.out.println("✅ メール送信成功: " + toEmail);
                successCount++;

            } catch (MessagingException e) {
                System.err.println("❌ メール送信失敗: " + toEmail);
                e.printStackTrace();
                failCount++;
            }
        }

        System.out.println("📧 メール送信結果 - 成功: " + successCount + "件 / 失敗: " + failCount + "件");
    }
}