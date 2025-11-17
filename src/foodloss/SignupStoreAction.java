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
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // POSTリクエストの場合 - 登録処理
        req.setCharacterEncoding("UTF-8");

        // --- CSRFトークンチェック ---
        HttpSession session = req.getSession();
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (sessionToken == null || !sessionToken.equals(token)) {
            req.setAttribute("errorMessage", "不正なアクセスです。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // --- 入力値取得 ---
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String passwordRaw = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm"); // ← 追加

        // --- 未入力チェック ---
        if (storeName == null || storeName.trim().isEmpty()) {
            req.setAttribute("errorMessage", "店舗名を入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            req.setAttribute("errorMessage", "店舗住所を入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            req.setAttribute("errorMessage", "電話番号を入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("errorMessage", "メールアドレスを入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }
        if (passwordRaw == null || passwordRaw.trim().isEmpty()) {
            req.setAttribute("errorMessage", "パスワードを入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }
        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) { // ← 追加
            req.setAttribute("errorMessage", "確認用パスワードを入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // --- パスワードチェック ---
        if (!passwordRaw.equals(passwordConfirm)) {
            req.setAttribute("errorMessage", "パスワードが一致しません。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        if (passwordRaw.length() < 8) {
            req.setAttribute("errorMessage", "パスワードは8文字以上で入力してください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // 電話番号の形式チェック
        if (!phone.matches("[0-9]{10,11}")) {
            req.setAttribute("errorMessage", "電話番号は10桁または11桁の数字で入力してください。");
            req.getRequestDispatcher("/jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // --- ファイルアップロード処理 ---
        Part permitFilePart = req.getPart("permitFile");
        byte[] permitFileData = null;
        String permitFileName = null;

        if (permitFilePart != null && permitFilePart.getSize() > 0) {
            permitFileName = getFileName(permitFilePart);
            if (!isValidFileType(permitFileName)) {
                req.setAttribute("errorMessage", "営業許可書はJPG、PNG、またはPDF形式でアップロードしてください。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
                return;
            }
            if (permitFilePart.getSize() > 5 * 1024 * 1024) {
                req.setAttribute("errorMessage", "ファイルサイズは5MB以下にしてください。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
                return;
            }

            // InputStreamを使ってバイト配列に変換
            try (InputStream is = permitFilePart.getInputStream();
                 java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                permitFileData = baos.toByteArray();
            }
        } else {
            req.setAttribute("errorMessage", "営業許可書をアップロードしてください。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        // --- パスワードハッシュ化 ---
        String password = hashPassword(passwordRaw);

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            StoreDAO dao = new StoreDAO(conn);

            if (isPhoneExists(dao, phone)) {
                req.setAttribute("errorMessage", "この電話番号は既に登録されています。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
                return;
            }

            if (isEmailExists(dao, email)) {
                req.setAttribute("errorMessage", "このメールアドレスは既に登録されています。");
                req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
                return;
            }

            // Storeオブジェクト作成
            Store store = new Store();
            store.setStoreName(storeName);
            store.setAddress(address);
            store.setPhone(phone);
            store.setEmail(email);
            store.setLicense(permitFileData);
            store.setLicenseFileName(permitFileName);
            store.setPassword(password);

            dao.insert(store);

            session.removeAttribute("csrfToken");
            session.setAttribute("registeredStore", store);

            req.getRequestDispatcher("/store_jsp/signup_done_store.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
        }
    }

    private boolean isPhoneExists(StoreDAO dao, String phone) throws Exception {
        for (Store store : dao.selectAll()) {
            if (store.getPhone() != null && store.getPhone().equals(phone)) return true;
        }
        return false;
    }

    private boolean isEmailExists(StoreDAO dao, String email) throws Exception {
        for (Store store : dao.selectAll()) {
            if (store.getEmail() != null && store.getEmail().equals(email)) return true;
        }
        return false;
    }

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

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String element : contentDisposition.split(";")) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".pdf");
    }
}

