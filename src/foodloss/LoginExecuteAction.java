package foodloss;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import tool.Action;
import tool.DBManager;

public class LoginExecuteAction extends Action {
    @Override

    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // パスワードをハッシュ化
        String hashedPassword = hashPassword(password);

        DBManager db = new DBManager();
        UserDAO dao = new UserDAO(db.getConnection());
        User user = dao.login(email, hashedPassword);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            // ログイン成功 → Menu.action にリダイレクト
            response.sendRedirect(request.getContextPath() + "/foodloss/Menu.action");
        } else {
            request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
            request.getRequestDispatcher("/jsp/login_user.jsp").forward(request, response);
        }

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
}