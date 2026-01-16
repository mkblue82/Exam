package tool;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "*.action" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // 1MB
    maxFileSize = 5 * 1024 * 1024,     // 5MB
    maxRequestSize = 10 * 1024 * 1024  // 10MB
)
public class FrontController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            System.out.println("FrontController called: " + req.getServletPath());

            // パスを取得
            String path = req.getServletPath();

            // クラス名を構築
            String name;
            if (path.startsWith("/foodloss/")) {

                name = path.substring(1)
                          .replace(".action", "Action")
                          .replace('/', '.');
            } else if (path.startsWith("/")) {

                name = "foodloss" + path.replace(".action", "Action")
                                        .replace('/', '.');
            } else {
                // その他のパターン
                name = path.replace(".action", "Action")
                          .replace('/', '.');
            }

            System.out.println("Trying to load class: " + name);

            // アクションクラスのインスタンスを作成
            Action action = (Action) Class.forName(name)
                                          .getDeclaredConstructor()
                                          .newInstance();

            // executeメソッド実行
            action.execute(req, res);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "エラーが発生しました: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}