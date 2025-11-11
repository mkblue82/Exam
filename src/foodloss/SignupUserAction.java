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

        // GETリクエストの場合
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // POSTリクエストの場合
        req.setCharacterEncoding("UTF-8");

        // --- CSRFトークンチェック ---
        HttpSession session = req.getSession();
        String token = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (sessionToken == null || !sessionToken.equals(token)) {
            req.setAttribute("errorMessage", "不正なアクセスです。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // --- 入力値取得 ---
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String passwordRaw = req.getParameter("password");

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

        // パスワードのハッシュ化
        String password = hashPassword(passwordRaw);

        // --- Userオブジェクト作成 ---
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setFavoriteStore(null);
        user.setStoreId(0);
        user.setNotification(false);

        // --- DB登録 ---
        try (Connection conn = DBManager.getConnection()) {
            UserDAO dao = new UserDAO(conn);

            // メールアドレスの重複チェック
            if (isEmailExists(dao, email)) {
                req.setAttribute("errorMessage", "このメールアドレスは既に登録されています。");
                req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
                return;
            }

            // ユーザー登録（SERIAL型でユーザーIDが自動生成される）
            dao.insert(user);

            // 成功時：CSRFトークンを削除
            session.removeAttribute("csrfToken");
            session.setAttribute("registeredUser", user);

            // 登録完了画面へ遷移
            req.getRequestDispatcher("/jsp/signupsuccess_user.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
        }
    }

    /**
     * メールアドレスの重複チェック
     */
    private boolean isEmailExists(UserDAO dao, String email) throws SQLException {
        for (User user : dao.findAll()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
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