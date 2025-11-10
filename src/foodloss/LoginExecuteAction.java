package foodloss;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.User;
import dao.UserDAO;
import tool.Action;

public class LoginExecuteAction extends Action {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection con = getConnection()) {
            UserDAO dao = new UserDAO(con);
            User user = dao.findByEmailAndPassword(email, password); // DAOに認証メソッドが必要！

            if (user != null) {
                // ✅ ログイン成功：セッションに保存
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // メインメニューへ遷移
                return "/jsp/main.jsp";

            } else {
                //  ログイン失敗
                request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
                return "/jsp/login.jsp";
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "サーバーエラーが発生しました。");
            return "/jsp/login.jsp";
        }
    }
}
