package foodloss;

import java.security.MessageDigest;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;
import tool.DBManager;

public class EditInfoExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if ((name == null || name.isEmpty()) &&
            (email == null || email.isEmpty()) &&
            (password == null || password.isEmpty())) {
            request.setAttribute("error", "変更する内容がありません。");
            request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
            return;
        }

        Connection con = null;
        try {
            con = new DBManager().getConnection();
            UserDAO dao = new UserDAO(con);

            User dbUser = dao.findById(loginUser.getUserId());
            if (dbUser == null) {
                request.setAttribute("error", "ユーザー情報が見つかりません。");
                request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
                return;
            }

            // --- 名前更新 ---
            if (name != null && !name.isEmpty()) {
                dbUser.setName(name);
            }

            // --- メール重複チェック ---
            if (email != null && !email.isEmpty()) {
                User exist = dao.findByEmail(email);
                if (exist != null && exist.getUserId() != loginUser.getUserId()) {
                    // 他ユーザーが使っている場合はエラー
                    request.setAttribute("error", "このメールアドレスは既に使用されています。");
                    request.getRequestDispatcher("/jsp/edit_info_user.jsp").forward(request, response);
                    return;
                }
                dbUser.setEmail(email); // 自分用の場合は更新
            }

            // --- パスワードハッシュ化して更新 ---
            if (password != null && !password.isEmpty()) {
                dbUser.setPassword(hashPassword(password));
            }

            // DB更新
            dao.update(dbUser);

            // セッション更新
            session.setAttribute("user", dbUser);

            // 完了画面へ
            request.setAttribute("message", "ユーザー情報を更新しました。");
            request.getRequestDispatcher("/jsp/edit_info_result_user.jsp").forward(request, response);

        } finally {
            if (con != null) con.close();
        }
    }

    // SHA-256 ハッシュ化
    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
