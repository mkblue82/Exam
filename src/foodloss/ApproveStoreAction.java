package foodloss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.Store;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;
import tool.MailSender;

public class ApproveStoreAction extends Action {

    // 一時保存ディレクトリ（SignupStoreActionと同じ）
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "store_applications";

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        System.out.println("DEBUG: ========== ApproveStoreAction START ==========");

        req.setCharacterEncoding("UTF-8");

        // --- トークン取得 ---
        String token = req.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            showError(req, res, "無効なリンクです。");
            return;
        }

        // --- 一時ファイルの存在確認 ---
        File dataFile = new File(TEMP_DIR + File.separator + token + ".txt");
        File permitFile = new File(TEMP_DIR + File.separator + token + "_permit");

        if (!dataFile.exists() || !permitFile.exists()) {
            showError(req, res, "申請データが見つかりません。\n既に承認済みか、有効期限が切れている可能性があります。");
            return;
        }

        // --- ファイルからデータ読み込み ---
        String storeName = null;
        String address = null;
        String phone = null;
        String email = null;
        String password = null;
        String permitFileName = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("storeName=")) {
                    storeName = line.substring("storeName=".length());
                } else if (line.startsWith("address=")) {
                    address = line.substring("address=".length());
                } else if (line.startsWith("phone=")) {
                    phone = line.substring("phone=".length());
                } else if (line.startsWith("email=")) {
                    email = line.substring("email=".length());
                } else if (line.startsWith("password=")) {
                    password = line.substring("password=".length());
                } else if (line.startsWith("permitFileName=")) {
                    permitFileName = line.substring("permitFileName=".length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(req, res, "申請データの読み込みに失敗しました。");
            return;
        }

        // --- 営業許可書ファイル読み込み ---
        byte[] permitFileData;
        try {
            permitFileData = Files.readAllBytes(permitFile.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            showError(req, res, "営業許可書ファイルの読み込みに失敗しました。");
            return;
        }

        // --- データ検証 ---
        if (storeName == null || address == null || phone == null ||
            email == null || password == null || permitFileData == null) {
            showError(req, res, "申請データが不完全です。");
            return;
        }

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            StoreDAO dao = new StoreDAO(conn);

            // 念のため再度重複チェック
            if (isPhoneExists(dao, phone)) {
                showError(req, res, "この電話番号は既に登録されています。\n申請を却下してください。");
                return;
            }
            if (isEmailExists(dao, email)) {
                showError(req, res, "このメールアドレスは既に登録されています。\n申請を却下してください。");
                return;
            }

            // Storeオブジェクト作成
            Store store = new Store();
            store.setStoreName(storeName);
            store.setAddress(address);
            store.setPhone(phone);
            store.setEmail(email);
            store.setLicense(permitFileData);
            store.setPassword(password);

            // DB登録
            dao.insert(store);

            System.out.println("DEBUG: Store registered - " + storeName);

            // --- 一時ファイル削除 ---
            try {
                dataFile.delete();
                permitFile.delete();
                System.out.println("DEBUG: Temporary files deleted for token: " + token);
            } catch (Exception e) {
                e.printStackTrace();
                // ファイル削除失敗してもエラーにはしない
            }

            // --- 店舗への完了メール送信 ---
            try {
                String completionBody = storeName + " 様\n\n" +
                                       "店舗の登録が完了しました。\n\n" +
                                       "以下のログインページからログインして、商品登録などを行ってください。\n\n" +
                                       "ログインURL: " + req.getScheme() + "://" + req.getServerName() +
                                       ":" + req.getServerPort() + req.getContextPath() + "/StoreLogin\n\n" +
                                       "メールアドレス: " + email + "\n" +
                                       "※パスワードは登録時に設定したものをご使用ください。\n\n" +
                                       "ご不明な点がございましたら、お気軽にお問い合わせください。";

                MailSender.sendEmail(
                    email,
                    "【完了】店舗登録完了のお知らせ",
                    completionBody
                );

                System.out.println("DEBUG: Completion email sent to: " + email);

            } catch (Exception e) {
                e.printStackTrace();
                // メール送信失敗してもDB登録は完了しているのでエラーページは表示しない
                System.err.println("WARNING: Failed to send completion email to: " + email);
            }

            // --- 成功ページ表示 ---
            req.setAttribute("storeName", storeName);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/admin_jsp/approve_success.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            showError(req, res, "データベースへの登録に失敗しました。\n" + e.getMessage());
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse res, String message) throws Exception {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/admin_jsp/approve_error.jsp").forward(req, res);
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
}