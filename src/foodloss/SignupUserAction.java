package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;
import tool.DBManager;


public class SignupUserAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        System.out.println("DEBUG: ========== SignupUserAction START ==========");
        System.out.println("DEBUG: Method = " + req.getMethod());

        // GETリクエストの場合 - フォーム表示
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            System.out.println("DEBUG: GET request - showing form");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
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
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        System.out.println("DEBUG: CSRF token OK");

        // --- 入力値取得 ---
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String passwordRaw = req.getParameter("password");

        System.out.println("DEBUG: name = " + name);
        System.out.println("DEBUG: email = " + email);
        System.out.println("DEBUG: phone = " + phone);
        System.out.println("DEBUG: passwordRaw = " + (passwordRaw != null ? "***" : "null"));

        // --- 未入力チェック ---
        if (name == null || name.trim().isEmpty()) {
            System.out.println("DEBUG: name is empty");
            req.setAttribute("errorMessage", "氏名を入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("DEBUG: email is empty");
            req.setAttribute("errorMessage", "メールアドレスを入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("DEBUG: phone is empty");
            req.setAttribute("errorMessage", "電話番号を入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (passwordRaw == null || passwordRaw.trim().isEmpty()) {
            System.out.println("DEBUG: password is empty");
            req.setAttribute("errorMessage", "パスワードを入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // パスワードの桁数チェック
        if (passwordRaw.length() < 8) {
            System.out.println("DEBUG: password too short");
            req.setAttribute("errorMessage", "パスワードは8文字以上で入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // 電話番号の形式チェック
        if (!phone.matches("[0-9]{10,11}")) {
            System.out.println("DEBUG: phone format invalid");
            req.setAttribute("errorMessage", "電話番号は10桁または11桁の数字で入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        System.out.println("DEBUG: All validation passed");

        // パスワードのハッシュ化
        String password = hashPassword(passwordRaw);
        System.out.println("DEBUG: Password hashed successfully");

        // --- Userオブジェクト作成 ---
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setFavoriteStore(null);
        // user.setStoreId(0); // ← この行を削除（デフォルト値0のままにする）
        user.setNotification(false);

        System.out.println("DEBUG: User object created");

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            System.out.println("DEBUG: DB connection established");
            UserDAO dao = new UserDAO(conn);

            // メールアドレスの重複チェック
            System.out.println("DEBUG: Checking email duplication");
            if (isEmailExists(dao, email)) {
                System.out.println("DEBUG: Email already exists");
                req.setAttribute("errorMessage", "このメールアドレスは既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
                return;
            }

            System.out.println("DEBUG: Email is unique, proceeding with insert");

            // ユーザー登録（SERIAL型でユーザーIDが自動生成される）
            dao.insert(user);
            System.out.println("DEBUG: User inserted successfully");

            // 成功時：CSRFトークンを削除
            session.removeAttribute("csrfToken");
            session.setAttribute("registeredUser", user);

            System.out.println("DEBUG: Forwarding to success page");
            // 登録完了画面へ遷移
            req.getRequestDispatcher("/jsp/signup_done_user.jsp").forward(req, res);

        } catch (SQLException e) {
            System.out.println("DEBUG: SQLException occurred");
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
        }

        System.out.println("DEBUG: ========== SignupUserAction END ==========");
    }

    /**
     * メールアドレスの重複チェック
     */
    private boolean isEmailExists(UserDAO dao, String email) throws SQLException {
        System.out.println("DEBUG: isEmailExists called for email: " + email);
        for (User user : dao.findAll()) {
            if (user.getEmail().equals(email)) {
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
}