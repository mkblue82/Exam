package tool;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Front extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        execute(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        execute(request, response);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // URL からアクション名を取得（例：LoginExecute.action → LoginExecuteAction）
            String path = request.getServletPath().substring(1);
            String name = path.substring(0, path.lastIndexOf(".action"));
            String className = "foodloss." + name + "Action";

            // 該当クラスを動的ロード
            Class<?> clazz = Class.forName(className);
            Action action = (Action) clazz.getDeclaredConstructor().newInstance();

            // executeメソッド呼び出し
            action.execute(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "ページを読み込めませんでした。");
            RequestDispatcher rd = request.getRequestDispatcher("/jsp/error.jsp");
            rd.forward(request, response);
        }
    }
}
