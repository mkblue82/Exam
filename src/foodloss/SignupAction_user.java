package foodloss;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;

/**
 * 新規ユーザー登録処理サーブレット
 */
@WebServlet("/signup_user")
public class SignupAction_user extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        // CSRFトークンの検証
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = req.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            req.setAttribute("errorMessage", "不正なリクエストです。再度お試しください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // フォームデータ取得
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // 入力チェック
        if (name == null || name.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty()) {
            req.setAttribute("errorMessage", "全ての項目を入力してください。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
            return;
        }

        // メール重複チェック → 登録 → 完了画面
        try (Connection conn = UserDAO.getConnection()) {

            // すでにメール登録済みか確認
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        req.setAttribute("errorMessage", "このメールアドレスはすでに登録されています。");
                        req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
                        return;
                    }
                }
            }

            // パスワードをハッシュ化
            String hashedPassword = hashPassword(password);

            // DB登録
            String insertSql = "INSERT INTO users (name, email, password_hash, user_type, is_active) VALUES (?, ?, ?, 'user', true)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, hashedPassword);
                insertStmt.executeUpdate();
            }

            // 登録成功時、トークンを破棄して完了画面へ
            session.removeAttribute("csrfToken");
            req.getRequestDispatcher("/jsp/signupsuccess.jsp").forward(req, res);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。");
            req.getRequestDispatcher("/jsp/signup_user.jsp").forward(req, res);
        }
    }

    /**
     * パスワードをSHA-256でハッシュ化
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }
}
