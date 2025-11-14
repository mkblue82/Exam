package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;
import tool.DBManager;

public class DeleteAccountAction extends Action {
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String action = req.getParameter("action");

        System.out.println("DEBUG: DeleteAccountAction - action parameter = " + action);

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        // ログインチェック
        if (user == null) {
            System.out.println("DEBUG: User not logged in. Redirecting to Login.");
            res.sendRedirect("Login.action");
            return;
        }

        // アカウント削除画面を表示
        if (action == null || action.isEmpty()) {
            System.out.println("DEBUG: Displaying delete account form");
            req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
            return;
        }

        // パスワード検証
        if ("verify".equals(action)) {
            System.out.println("DEBUG: Password verification started");
            verifyPassword(req, res, user);
            return;
        }

        // アカウント削除実行
        if ("execute".equals(action)) {
            System.out.println("DEBUG: Account deletion started");
            executeDelete(req, res, user);
            return;
        }

        // 不明なアクション
        System.out.println("DEBUG: Unknown action - " + action);
        req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
    }

    private void verifyPassword(HttpServletRequest req, HttpServletResponse res, User user) throws Exception {
        Connection con = null;
        try {
            String password = req.getParameter("password");

            System.out.println("==========================================");
            System.out.println("DEBUG: Raw password input = [" + password + "]");
            System.out.println("DEBUG: Raw password length = " + (password != null ? password.length() : "null"));

            if (password == null || password.isEmpty()) {
                System.out.println("DEBUG: Password is empty");
                req.setAttribute("error", "パスワードを入力してください");
                req.setAttribute("password", "");
                req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
                return;
            }

            // パスワードをハッシュ化
            String hashedPassword = hashPassword(password);
            System.out.println("DEBUG: Hashed input password = [" + hashedPassword + "]");
            System.out.println("DEBUG: Hashed password length = " + hashedPassword.length());

            // パスワードが正しいか確認
            con = new DBManager().getConnection();
            UserDAO userDAO = new UserDAO(con);
            User loginUser = userDAO.findById(user.getUserId());

            System.out.println("DEBUG: User found in DB = " + (loginUser != null));

            if (loginUser == null) {
                System.out.println("DEBUG: User not found in database");
                req.setAttribute("error", "ユーザー情報の取得に失敗しました");
                req.setAttribute("password", password);
                req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
                return;
            }

            String dbPassword = loginUser.getPassword();
            System.out.println("DEBUG: DB stored password = [" + dbPassword + "]");
            System.out.println("DEBUG: DB password length = " + (dbPassword != null ? dbPassword.length() : "null"));
            System.out.println("DEBUG: Password equals = " + hashedPassword.equals(dbPassword));
            System.out.println("DEBUG: Password equalsIgnoreCase = " + hashedPassword.equalsIgnoreCase(dbPassword));

            // 先頭10文字だけ比較（デバッグ用）
            if (hashedPassword != null && dbPassword != null &&
                hashedPassword.length() >= 10 && dbPassword.length() >= 10) {
                System.out.println("DEBUG: First 10 chars of hashed input = " + hashedPassword.substring(0, 10));
                System.out.println("DEBUG: First 10 chars of DB = " + dbPassword.substring(0, 10));
            }
            System.out.println("==========================================");

            // ハッシュ化されたパスワードで比較
            if (!hashedPassword.equals(dbPassword)) {
                System.out.println("DEBUG: Password does not match");
                req.setAttribute("error", "パスワードが正しくありません");
                req.setAttribute("password", password);
                req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
                return;
            }

            // パスワードが正しい場合、最終確認画面へ
            System.out.println("DEBUG: Password verified successfully. Forwarding to confirm page.");
            req.setAttribute("verified", true);
            req.getRequestDispatcher("/jsp/delete_account_confirm.jsp").forward(req, res);

        } catch (Exception e) {
            System.out.println("DEBUG: Exception in verifyPassword");
            e.printStackTrace();
            req.setAttribute("error", "エラーが発生しました: " + e.getMessage());
            req.setAttribute("password", req.getParameter("password"));
            req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
        } finally {
            if (con != null) {
                try {
                    con.close();
                    System.out.println("DEBUG: Connection closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void executeDelete(HttpServletRequest req, HttpServletResponse res, User user) throws Exception {
        Connection con = null;
        try {
            System.out.println("DEBUG: Starting account deletion for user ID: " + user.getUserId());

            // ユーザーを削除
            con = new DBManager().getConnection();
            UserDAO userDAO = new UserDAO(con);
            User deleteUser = userDAO.findById(user.getUserId());

            if (deleteUser == null) {
                System.out.println("DEBUG: User not found for deletion");
                req.setAttribute("error", "ユーザーが見つかりません");
                req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
                return;
            }

            String userName = deleteUser.getName();
            int userId = user.getUserId();

            System.out.println("DEBUG: Attempting to delete user: " + userName + " (ID: " + userId + ")");

            // 削除実行（戻り値なし）
            userDAO.delete(userId);

            System.out.println("User [" + userName + "] (ID: " + userId + ") deleted at " + new java.util.Date());

            // セッションを無効化
            HttpSession session = req.getSession();
            session.invalidate();

            System.out.println("DEBUG: Session invalidated. Redirecting to done page.");

            // 削除完了画面へリダイレクト
            res.sendRedirect(req.getContextPath() + "/jsp/delete_account_done.jsp");

        } catch (Exception e) {
            System.out.println("DEBUG: Exception in executeDelete");
            e.printStackTrace();
            req.setAttribute("error", "削除処理中にエラーが発生しました: " + e.getMessage());
            req.getRequestDispatcher("/jsp/delete_account.jsp").forward(req, res);
        } finally {
            if (con != null) {
                try {
                    con.close();
                    System.out.println("DEBUG: Connection closed in executeDelete");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * パスワードをSHA-256でハッシュ化
     * LoginExecuteActionと同じロジック
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