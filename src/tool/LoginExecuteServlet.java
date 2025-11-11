package tool;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import foodloss.LoginExecuteAction;

@WebServlet("/LoginExecute.action") // これがブラウザからアクセスするURL
public class LoginExecuteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 既存の LoginExecuteAction を呼び出す
            LoginExecuteAction action = new LoginExecuteAction();
            action.execute(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "サーバーエラーが発生しました。");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }

    // GET でアクセスしても login.jsp を表示する
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }
}
