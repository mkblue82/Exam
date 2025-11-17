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
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {

        System.out.println("DEBUG: ========== SignupStoreAction START ==========");
        System.out.println("DEBUG: Method = " + req.getMethod());

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
            return;
        }

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        // --- CSRFチェック ---
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (sessionToken == null || !sessionToken.equals(token)) {
            forwardWithError(req, res, "不正なアクセスです。");
            return;
        }

        // --- 入力値 ---
        String storeName = req.getParameter("storeName");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String passwordRaw = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");

        if (storeName == null || storeName.trim().isEmpty()) { forwardWithError(req, res, "店舗名を入力してください。"); return; }
        if (address == null || address.trim().isEmpty()) { forwardWithError(req, res, "店舗住所を入力してください。"); return; }
        if (phone == null || phone.trim().isEmpty()) { forwardWithError(req, res, "電話番号を入力してください。"); return; }
        if (email == null || email.trim().isEmpty()) { forwardWithError(req, res, "メールアドレスを入力してください。"); return; }
        if (passwordRaw == null || passwordRaw.trim().isEmpty()) { forwardWithError(req, res, "パスワードを入力してください。"); return; }
        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) { forwardWithError(req, res, "確認用パスワードを入力してください。"); return; }
        if (!passwordRaw.equals(passwordConfirm)) { forwardWithError(req, res, "パスワードが一致しません。"); return; }
        if (passwordRaw.length() < 8) { forwardWithError(req, res, "パスワードは8文字以上で入力してください。"); return; }
        if (!phone.matches("[0-9]{10,11}")) { forwardWithError(req, res, "電話番号は10桁または11桁の数字で入力してください。"); return; }

        // --- ファイル ---
        Part permitFilePart = req.getPart("permitFile");
        if (permitFilePart == null || permitFilePart.getSize() == 0) { forwardWithError(req, res, "営業許可書をアップロードしてください。"); return; }
        String permitFileName = getFileName(permitFilePart);
        if (!isValidFileType(permitFileName)) { forwardWithError(req, res, "営業許可書はJPG、PNG、またはPDF形式でアップロードしてください。"); return; }
        if (permitFilePart.getSize() > 5 * 1024 * 1024) { forwardWithError(req, res, "ファイルサイズは5MB以下にしてください。"); return; }

        byte[] permitFileData;
        try (InputStream is = permitFilePart.getInputStream();
             java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            permitFileData = baos.toByteArray();
        }

        // --- パスワードハッシュ ---
        String password = hashPassword(passwordRaw);

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            StoreDAO dao = new StoreDAO(conn);

            if (isPhoneExists(dao, phone)) { forwardWithError(req, res, "この電話番号は既に登録されています。"); return; }
            if (isEmailExists(dao, email)) { forwardWithError(req, res, "このメールアドレスは既に登録されています。"); return; }

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
            forwardWithError(req, res, "システムエラーが発生しました。");
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse res, String message) throws Exception {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/store_jsp/signup_store.jsp").forward(req, res);
    }

    private boolean isPhoneExists(StoreDAO dao, String phone) throws Exception {
        for (Store s : dao.selectAll()) if (s.getPhone() != null && s.getPhone().equals(phone)) return true;
        return false;
    }

    private boolean isEmailExists(StoreDAO dao, String email) throws Exception {
        for (Store s : dao.selectAll()) if (s.getEmail() != null && s.getEmail().equals(email)) return true;
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
        String cd = part.getHeader("content-disposition");
        for (String elem : cd.split(";")) {
            if (elem.trim().startsWith("filename")) return elem.substring(elem.indexOf('=') + 1).trim().replace("\"", "");
        }
        return null;
    }

    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".pdf");
    }
}
