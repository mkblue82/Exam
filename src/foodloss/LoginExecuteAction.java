package foodloss;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class LoginExecuteAction extends Action {

    private static final Logger logger = Logger.getLogger(LoginExecuteAction.class.getName());

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse res)
            throws Exception {

        // パラメータ取得
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        logger.info("Login attempt: " + email);

        // DB接続
        try (Connection con = getConnection()) {
            UserDAO dao = new UserDAO(con);
            User user = dao.login(email, password);


            if (user != null) {
                // ✅ ログイン成功
                logger.info("Login successful: " + email);

                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);

                // メインメニューへ遷移
                res.sendRedirect(req.getContextPath() + "/jsp/main_user.jsp");

            } else {
                // ❌ ログイン失敗
                logger.warning("Login failed: " + email);
                List<String> errors = new ArrayList<>();
                errors.add("メールアドレスまたはパスワードが違います。");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/jsp/login_user.jsp").forward(req, res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            List<String> errors = new ArrayList<>();
            errors.add("サーバーエラーが発生しました。");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/jsp/login_user.jsp").forward(req, res);
        }
    }
}
