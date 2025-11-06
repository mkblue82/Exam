package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.LoginBean;
import dao.DatabaseConnection;
import tool.Action;

/**
 * ログイン処理を行うAction
 */
@WebServlet("/login")
public class LoginAction extends Action {

    /**
     * ログイン処理の実行
     */
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        
        // GETリクエストの場合はログインページを表示
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            // CSRFトークンを生成してセッションに保存
            String csrfToken = generateCSRFToken();
            session.setAttribute("csrfToken", csrfToken);
            req.getRequestDispatcher("jsp/login.jsp").forward(req, res);
            return;
        }
        
        // POSTリクエスト - ログイン実行
        
        // CSRFトークンの検証
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = req.getParameter("csrfToken");
        
        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            req.setAttribute("errorMessage", "不正なリクエストです。");
            req.getRequestDispatcher("jsp/login.jsp").forward(req, res);
            return;
        }

        // フォームデータの取得
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        // LoginBeanに格納
        LoginBean loginBean = new LoginBean(email, password);
        
        // バリデーション
        String validationError = loginBean.validate();
        if (validationError != null) {
            req.setAttribute("errorMessage", validationError);
            req.setAttribute("email", email); // 入力値を保持
            req.getRequestDispatcher("jsp/login.jsp").forward(req, res);
            return;
        }

        // データベース認証
        try {
            if (authenticateUser(email, password, session)) {
                // ログイン成功 - メインページへリダイレクト
                res.sendRedirect("main");
            } else {
                // ログイン失敗
                req.setAttribute("errorMessage", "メールアドレスまたはパスワードが正しくありません。");
                req.setAttribute("email", email);
                req.getRequestDispatcher("jsp/login.jsp").forward(req, res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "システムエラーが発生しました。しばらくしてから再度お試しください。");
            req.getRequestDispatcher("jsp/login.jsp").forward(req, res);
        }
    }

    /**
     * ユーザー認証処理
     */
    private boolean authenticateUser(String email, String password, HttpSession session) 
            throws SQLException {
        
        String sql = "SELECT user_id, email, name, password_hash, user_type, is_active " +
                     "FROM users WHERE email = ? AND is_active = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password);
                    
                    // パスワードハッシュの比較
                    if (storedHash.equals(inputHash)) {
                        // セッションにユーザー情報を保存
                        session.setAttribute("userId", rs.getInt("user_id"));
                        session.setAttribute("userEmail", rs.getString("email"));
                        session.setAttribute("userName", rs.getString("name"));
                        session.setAttribute("userType", rs.getString("user_type"));
                        session.setAttribute("isLoggedIn", true);
                        
                        // セッションタイムアウトを30分に設定
                        session.setMaxInactiveInterval(1800);
                        
                        // ログイン時刻を記録
                        updateLastLogin(rs.getInt("user_id"));
                        
                        return true;
                    }
                }
            }
        }
        
        return false;
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
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードのハッシュ化に失敗しました", e);
        }
    }

    /**
     * 最終ログイン日時を更新
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            // ログイン日時の更新失敗はログインを妨げない
            e.printStackTrace();
        }
    }

    /**
     * CSRFトークンを生成
     */
    private String generateCSRFToken() {
        return Long.toHexString(System.currentTimeMillis()) + 
               Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}