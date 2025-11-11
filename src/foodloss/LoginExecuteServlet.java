package foodloss;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoginExecute.action") // JSP の form action に合わせる
public class LoginExecuteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 既存の LoginExecuteAction を呼び出す
            LoginExecuteAction action = new LoginExecuteAction();
            action.execute(req, resp); // execute は HttpServletRequest, HttpServletResponse を受け取る
        } catch (Exception e) {
            e.printStackTrace();
            // サーバーエラー時は login.jsp にエラー表示
            req.setAttribute("errors", java.util.Arrays.asList("サーバーエラーが発生しました。"));
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // GET でアクセスした場合はログイン画面を表示
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }
}
