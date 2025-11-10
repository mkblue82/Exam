package action;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class LoginExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection con = null;
        try {
            // DB接続（既存の getConnection() を利用）
            con = getConnection();
            UserDAO dao = new UserDAO(con);

            // ユーザー認証
            User user = dao.findByEmailAndPassword(email, password);

            if (user != null) {
                // ログイン成功
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // ✅ メインメニューにリダイレクト
                response.sendRedirect(request.getContextPath() + "/jsp/main.jsp");

            } else {
                // ログイン失敗
                request.setAttribute("errors", java.util.Arrays.asList("メールアドレスまたはパスワードが違います。"));
                try {
                    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                } catch (Exception ex) { ex.printStackTrace(); }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errors", java.util.Arrays.asList("サーバーエラーが発生しました。"));
            try {
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            } catch (Exception ex) { ex.printStackTrace(); }

        } finally {
            // DB接続クローズ
            if (con != null) try { con.close(); } catch (Exception ignore) {}
        }
    }
}
