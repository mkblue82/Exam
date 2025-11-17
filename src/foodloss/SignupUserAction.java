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

        // ▼▼ ここを修正：confirmPassword を取得 ▼▼
        String passwordConfirm = req.getParameter("confirmPassword");
        // ▲▲ 修正完了 ▲▲

        System.out.println("DEBUG: name = " + name);
        System.out.println("DEBUG: email = " + email);
        System.out.println("DEBUG: phone = " + phone);
        System.out.println("DEBUG: passwordRaw = " + (passwordRaw != null ? "***" : "null"));
        System.out.println("DEBUG: passwordConfirm = " + (passwordConfirm != null ? "***" : "null"));

        // --- 未入力チェック ---
        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("errorMessage", "氏名を入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("errorMessage", "メールアドレスを入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (phone == null || phone.trim().isEmpty()) {
            req.setAttribute("errorMessage", "電話番号を入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (passwordRaw == null || passwordRaw.trim().isEmpty()) {
            req.setAttribute("errorMessage", "パスワードを入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
            req.setAttribute("errorMessage", "確認用パスワードを入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // --- パスワード一致チェック ---
        if (!passwordRaw.equals(passwordConfirm)) {
            req.setAttribute("errorMessage", "パスワードが一致しません。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // パスワードの桁数チェック
        if (passwordRaw.length() < 8) {
            req.setAttribute("errorMessage", "パスワードは8文字以上で入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // 電話番号の形式チェック
        if (!phone.matches("[0-9]{10,11}")) {
            req.setAttribute("errorMessage", "電話番号は10桁または11桁の数字で入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        System.out.println("DEBUG: All validation passed");

        // --- パスワードのハッシュ化 ---
        String password = hashPassword(passwordRaw);
        System.out.println("DEBUG: Password hashed successfully");

        // --- Userオブジェクト作成 ---
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);

        System.out.println("DEBUG: User object created");

        // --- DB登録 ---
        DBManager db = new DBManager();
        try (Connection conn = db.getConnection()) {
            UserDAO dao = new UserDAO(conn);

            // 電話番号の重複チェック
            if (isPhoneExists(dao, phone)) {
                req.setAttribute("errorMessage", "この電話番号は既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
                return;
            }

            // メールアドレスの重複チェック
            if (isEmailExists(dao, email)) {
                req.setAttribute("errorMessage", "このメールアドレスは既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
                return;
            }

            // ユーザー登録
            dao.insert(user);
            System.out.println("DEBUG: User inserted successfully");

            // 成功時：CSRFトークン削除
            session.removeAttribute("csrfToken");
            session.setAttribute("registeredUser", user);

            req.getRequestDispatcher("/jsp/signup_done_user.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
        }

        System.out.println("DEBUG: ========== SignupUserAction END ==========");
    }

    /** 電話番号の重複チェック */
    private boolean isPhoneExists(UserDAO dao, String phone) throws SQLException {
        for (User user : dao.findAll()) {
            if (user.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    /** メールアドレスの重複チェック */
    private boolean isEmailExists(UserDAO dao, String email) throws SQLException {
        for (User user : dao.findAll()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /** パスワードハッシュ化 */
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
