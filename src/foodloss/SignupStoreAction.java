package foodloss;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import bean.Store;
import dao.StoreDAO;
import tool.Action;
import tool.DBManager;

public class SignupStoreAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        System.out.println("DEBUG: ========== SignupStoreAction START ==========");
        System.out.println("DEBUG: Method = " + req.getMethod());

        // GETリクエストの場合 - フォーム表示
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            System.out.println("DEBUG: GET request - showing form");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // POSTリクエストの場合 - 登録処理
        System.out.println("DEBUG: POST request - processing registration");
        req.setCharacterEncoding("UTF-8");

        // --- CSRFトークンチェック ---
        HttpSession session = req.getSession();
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        System.out.println("DEBUG: token from request = " + token);
        System.out.println("DEBUG: sessionToken = " + sessionToken);

        if (sessionToken == null || !sessionToken.equals(token)) {
            System.out.println("DEBUG: CSRF token mismatch - showing error");
            req.setAttribute("errorMessage", "不正なアクセスです。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        System.out.println("DEBUG: CSRF token OK");

        // --- 入力値取得 ---
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String passwordRaw = req.getParameter("password");

        System.out.println("DEBUG: storeName = " + storeName);
        System.out.println("DEBUG: address = " + address);
        System.out.println("DEBUG: phone = " + phone);
        System.out.println("DEBUG: email = " + email);
        System.out.println("DEBUG: passwordRaw = " + (passwordRaw != null ? "***" : "null"));

        // --- 未入力チェック ---
        if (storeName == null || storeName.trim().isEmpty()) {
            System.out.println("DEBUG: storeName is empty");
            req.setAttribute("errorMessage", "店舗名を入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (address == null || address.trim().isEmpty()) {
            System.out.println("DEBUG: address is empty");
            req.setAttribute("errorMessage", "店舗住所を入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("DEBUG: phone is empty");
            req.setAttribute("errorMessage", "電話番号を入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("DEBUG: email is empty");
            req.setAttribute("errorMessage", "メールアドレスを入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (passwordRaw == null || passwordRaw.trim().isEmpty()) {
            System.out.println("DEBUG: password is empty");
            req.setAttribute("errorMessage", "パスワードを入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // パスワードの桁数チェック
        if (passwordRaw.length() < 8) {
            System.out.println("DEBUG: password too short");
            req.setAttribute("errorMessage", "パスワードは8文字以上で入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // 電話番号の形式チェック
        if (!phone.matches("[0-9]{10,11}")) {
            System.out.println("DEBUG: phone format invalid");
            req.setAttribute("errorMessage", "電話番号は10桁または11桁の数字で入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // --- ファイルアップロード処理 ---
        Part permitFilePart = req.getPart("permitFile");
        byte[] permitFileData = null;
        String permitFileName = null;

        if (permitFilePart != null && permitFilePart.getSize() > 0) {
            System.out.println("DEBUG: permitFile found, size = " + permitFilePart.getSize());
            permitFileName = getFileName(permitFilePart);
            System.out.println("DEBUG: permitFileName = " + permitFileName);

            // ファイルの拡張子チェック
            if (!isValidFileType(permitFileName)) {
                System.out.println("DEBUG: invalid file type");
                req.setAttribute("errorMessage", "営業許可書はJPG、PNG、またはPDF形式でアップロードしてください。");
                req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
                return;
            }

            // ファイルサイズチェック（例：5MB以下）
            if (permitFilePart.getSize() > 5 * 1024 * 1024) {
                System.out.println("DEBUG: file too large");
                req.setAttribute("errorMessage", "ファイルサイズは5MB以下にしてください。");
                req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
                return;
            }

            // ファイルデータをバイト配列に変換（Java 8対応）
            try (InputStream is = permitFilePart.getInputStream()) {
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                permitFileData = baos.toByteArray();
                System.out.println("DEBUG: File data read successfully");
            }
        } else {
            System.out.println("DEBUG: permitFile is required but not found");
            req.setAttribute("errorMessage", "営業許可書をアップロードしてください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        System.out.println("DEBUG: All validation passed");

        // パスワードのハッシュ化
        String password = hashPassword(passwordRaw);
        System.out.println("DEBUG: Password hashed successfully");

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            System.out.println("DEBUG: DB connection established");
            StoreDAO dao = new StoreDAO(conn);  // ✅ Connectionを渡す

            // 電話番号の重複チェック
            System.out.println("DEBUG: Checking phone duplication");
            if (isPhoneExists(dao, phone)) {
                System.out.println("DEBUG: Phone already exists");
                req.setAttribute("errorMessage", "この電話番号は既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
                return;
            }

            // メールアドレスの重複チェック
            System.out.println("DEBUG: Checking email duplication");
            if (isEmailExists(dao, email)) {
                System.out.println("DEBUG: Email already exists");
                req.setAttribute("errorMessage", "このメールアドレスは既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
                return;
            }

            System.out.println("DEBUG: Email and phone are unique, proceeding with insert");

            // Storeオブジェクト作成
            Store store = new Store();
            // storeIdは自動採番されるのでセット不要
            store.setStoreName(storeName);
            store.setAddress(address);
            store.setPhone(phone);
            store.setEmail(email);
            store.setLicense(permitFileData);
            store.setLicenseFileName(permitFileName);
            store.setPassword(password);


            System.out.println("DEBUG: Store object created");

            // 店舗登録（この時点でstoreIdが自動設定される）
            dao.insert(store);
            System.out.println("DEBUG: Store inserted successfully with ID: " + store.getStoreId());

            // 成功時：CSRFトークンを削除
            session.removeAttribute("csrfToken");
            session.setAttribute("registeredStore", store);

            System.out.println("DEBUG: Forwarding to success page");
            // 登録完了画面へ遷移
            req.getRequestDispatcher("/jsp/signup_done_store.jsp").forward(req, res);

        } catch (SQLException e) {
            System.out.println("DEBUG: SQLException occurred");
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
        } catch (Exception e) {
            System.out.println("DEBUG: Exception occurred");
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
        }

        System.out.println("DEBUG: ========== SignupStoreAction END ==========");
    }

    /**
     * 電話番号の重複チェック
     */
    private boolean isPhoneExists(StoreDAO dao, String phone) throws Exception {
        System.out.println("DEBUG: isPhoneExists called for phone: " + phone);
        for (Store store : dao.selectAll()) {
            if (store.getPhone() != null && store.getPhone().equals(phone)) {
                System.out.println("DEBUG: Found duplicate phone");
                return true;
            }
        }
        System.out.println("DEBUG: No duplicate phone found");
        return false;
    }

    /**
     * メールアドレスの重複チェック
     */
    private boolean isEmailExists(StoreDAO dao, String email) throws Exception {
        System.out.println("DEBUG: isEmailExists called for email: " + email);
        for (Store store : dao.selectAll()) {
            if (store.getEmail() != null && store.getEmail().equals(email)) {
                System.out.println("DEBUG: Found duplicate email");
                return true;
            }
        }
        System.out.println("DEBUG: No duplicate email found");
        return false;
    }

    /**
     * パスワードハッシュ化
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }

    /**
     * アップロードされたファイル名を取得
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1)
                        .trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * ファイルタイプのバリデーション
     */
    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") ||
               lowerCaseFileName.endsWith(".jpeg") ||
               lowerCaseFileName.endsWith(".png") ||
               lowerCaseFileName.endsWith(".pdf");
    }
}
