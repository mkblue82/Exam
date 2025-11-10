package foodloss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import tool.Action;
import bean.User;
import dao.ConnectionManager;

@WebServlet("/login_user")
public class LoginAction extends Action {

    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String method = req.getMethod();

        // GETリクエスト: ログインページを表示
        if ("GET".equals(method)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            // login_user.jspにフォワード
            req.getRequestDispatcher("/jsp/login_user.jsp").forward(req, res);
            return;
        }

        // POSTリクエスト: ログイン処理
        if ("POST".equals(method)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            HttpSession session = req.getSession();

            // 入力値の検証
            if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                req.setAttribute("error", "メールアドレスとパスワードを入力してください");
                req.getRequestDispatcher("/jsp/login_user.jsp").forward(req, res);
                return;
            }

            // データベースでユーザーを検証
            User user = authenticateUser(email, password);

            if (user != null) {
                // ログイン成功
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());

                // メインメニューにリダイレクト
                res.sendRedirect(req.getContextPath() + "/menu");
            } else {
                // ログイン失敗
                req.setAttribute("error", "メールアドレスまたはパスワードが正しくありません");
                req.getRequestDispatcher("/jsp/login_user.jsp").forward(req, res);
            }
        }
    }

    private User authenticateUser(String email, String password) {
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            String sql = "SELECT user_id, name, email, phone, password, favorite_store, store_id, notification " +
                        "FROM users WHERE email = ? AND password = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setFavoriteStore(rs.getString("favorite_store"));
                user.setStoreId(rs.getInt("store_id"));
                user.setNotification(rs.getBoolean("notification"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();