package foodloss;

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

        try (Connection con = getConnection()) {
            UserDAO dao = new UserDAO(con);
            User user = dao.findByEmailAndPassword(email, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/jsp/main.jsp");
            } else {
                request.setAttribute("error", "メールアドレスまたはパスワードが違います。");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "サーバーエラーが発生しました。");
            try {
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
